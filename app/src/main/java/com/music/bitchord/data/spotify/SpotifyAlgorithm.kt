package com.music.bitchord.data.spotify

import com.music.bitchord.data.Http
import com.music.bitchord.data.canvas.CANVAS_UA
import com.music.bitchord.data.canvas.SpotifyToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import java.util.concurrent.ConcurrentHashMap

/**
 * Spotify's recommendation algorithm, used only to choose *what* to play.
 *
 * The songs themselves are matched back to YouTube Music and streamed through
 * the existing player, so this needs no Spotify playback rights, no Premium
 * account and never plays a Spotify advert. The listener still has to be
 * signed in to Spotify (the same `sp_dc` cookie Canvas already uses) so the
 * recommendations can be drawn from their own taste rather than a generic
 * editorial list.
 *
 * Every step is best-effort. A missing cookie, an expired token, a retired
 * endpoint or a rate limit all return an empty list, which lets the caller fall
 * back to the ordinary YouTube radio without surfacing an error.
 */
object SpotifyAlgorithm {

    private const val API = "https://api.spotify.com/v1"
    private const val CACHE_TTL_MS = 10 * 60 * 1000L
    private const val MAX_CACHE_ENTRIES = 32

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val cache = ConcurrentHashMap<String, Cached>()

    private data class Cached(val at: Long, val tracks: List<SpotifyTrack>)
    private data class Seed(val id: String)

    /**
     * Up to [limit] tracks Spotify thinks belong beside [seedTitle] by
     * [seedArtist]. Empty when Spotify cannot answer.
     */
    suspend fun related(seedTitle: String, seedArtist: String, limit: Int): List<SpotifyTrack> =
        withContext(Dispatchers.IO) {
            if (limit <= 0 || seedTitle.isBlank()) return@withContext emptyList()
            // The matching step costs a YouTube search per track, so keep the
            // first batch small enough to start playing promptly.
            val want = limit.coerceAtMost(MAX_FIRST_BATCH)
            val key = "${seedTitle.lowercase()}|${seedArtist.lowercase()}|$want"
            val now = System.currentTimeMillis()
            cache[key]?.takeIf { now - it.at < CACHE_TTL_MS }?.let { return@withContext it.tracks }

            val tracks = try {
                fetch(seedTitle, seedArtist, want)
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e
            } catch (e: Exception) {
                emptyList()
            }
            if (tracks.isEmpty()) return@withContext emptyList()

            if (cache.size >= MAX_CACHE_ENTRIES) cache.clear()
            cache[key] = Cached(now, tracks)
            tracks
        }

    private suspend fun fetch(seedTitle: String, seedArtist: String, limit: Int): List<SpotifyTrack> {
        val headers = authHeaders() ?: return emptyList()
        val seed = findSeed(headers, seedTitle, seedArtist)
        val out = mutableListOf<SpotifyTrack>()

        if (seed != null) {
            out += recommendations(headers, seed.id, limit)
        }
        if (out.size < limit) {
            // Recommendations need a seed track Spotify recognises. Top tracks
            // keep the feature useful for a local or very new song, and are
            // still Spotify's own taste model rather than YouTube's radio.
            out += topTracks(headers, limit - out.size)
        }
        if (out.size < limit) {
            // Last resort: a seed search returns real Spotify tracks too, and
            // they are a much better fit than nothing.
            out += searchTracks(headers, listOf(seedTitle, seedArtist).filter { it.isNotBlank() }.joinToString(" "), limit)
        }
        return out.distinctBy { it.id }.take(limit)
    }

    private suspend fun authHeaders(): Map<String, String>? {
        val token = SpotifyToken.accessToken() ?: return null
        return buildMap {
            put("Authorization", "Bearer $token")
            put("Accept", "application/json")
            put("app-platform", "WebPlayer")
            put("Origin", "https://open.spotify.com")
            put("Referer", "https://open.spotify.com/")
            put("User-Agent", CANVAS_UA)
            SpotifyToken.clientToken()?.let { put("Client-Token", it) }
        }
    }

    private fun findSeed(headers: Map<String, String>, title: String, artist: String): Seed? {
        val query = listOf(title, artist).filter { it.isNotBlank() }.joinToString(" ")
        val root = get(headers, apiUrl("/search", "type" to "track", "limit" to "1", "q" to query))
            ?: return null
        val track = root.obj("tracks")?.arr("items")?.firstOrNull()?.jsonObject ?: return null
        val id = track.str("id") ?: return null
        return Seed(id = id)
    }

    private fun recommendations(headers: Map<String, String>, seedId: String, limit: Int): List<SpotifyTrack> {
        val root = get(headers, apiUrl("/recommendations", "limit" to "$limit", "seed_tracks" to seedId))
            ?: return emptyList()
        return root.arr("tracks").orEmpty().mapNotNull { parseTrack(it.jsonObject) }
    }

    private fun topTracks(headers: Map<String, String>, limit: Int): List<SpotifyTrack> {
        val root = get(
            headers,
            apiUrl("/me/top/tracks", "limit" to "$limit", "time_range" to "short_term"),
        ) ?: return emptyList()
        return root.arr("items").orEmpty().mapNotNull { parseTrack(it.jsonObject) }
    }

    private fun searchTracks(headers: Map<String, String>, query: String, limit: Int): List<SpotifyTrack> {
        val root = get(headers, apiUrl("/search", "type" to "track", "limit" to "$limit", "q" to query))
            ?: return emptyList()
        return root.obj("tracks")?.arr("items").orEmpty().mapNotNull { parseTrack(it.jsonObject) }
    }

    private fun parseTrack(track: JsonObject): SpotifyTrack? {
        val id = track.str("id") ?: return null
        val title = track.str("name") ?: return null
        val artist = track.arr("artists").orEmpty()
            .mapNotNull { it.jsonObject.str("name") }
            .joinToString(", ")
        val album = track.obj("album")
        return SpotifyTrack(
            id = id,
            title = title,
            artist = artist,
            album = album?.str("name"),
            durationMs = track.int("duration_ms") ?: 0,
            imageUrl = album?.arr("images")?.firstOrNull()?.jsonObject?.str("url"),
        )
    }

    private fun get(headers: Map<String, String>, url: String): JsonObject? = runCatching {
        val request = Request.Builder()
            .url(url)
            .apply { headers.forEach { (name, value) -> header(name, value) } }
            .build()
        Http.client.newCall(request).execute().use { response ->
            val body = response.body?.string().orEmpty()
            if (!response.isSuccessful || body.isBlank()) return@use null
            json.parseToJsonElement(body).jsonObject
        }
    }.getOrNull()

    /** The first batch is kept small so a station starts quickly. */
    private const val MAX_FIRST_BATCH = 10

    private fun apiUrl(path: String, vararg params: Pair<String, String>): String {
        val builder = "$API$path".toHttpUrl().newBuilder()
        params.forEach { (name, value) -> builder.addQueryParameter(name, value) }
        return builder.build().toString()
    }
}

private fun JsonObject.obj(key: String): JsonObject? =
    this[key]?.takeIf { it !is JsonNull }?.let { runCatching { it.jsonObject }.getOrNull() }

private fun JsonObject.arr(key: String): JsonArray? =
    this[key]?.takeIf { it !is JsonNull }?.let { runCatching { it.jsonArray }.getOrNull() }

private fun JsonObject.str(key: String): String? =
    this[key]?.takeIf { it !is JsonNull }?.jsonPrimitive?.contentOrNull

private fun JsonObject.int(key: String): Int? =
    this[key]?.takeIf { it !is JsonNull }?.jsonPrimitive?.intOrNull
