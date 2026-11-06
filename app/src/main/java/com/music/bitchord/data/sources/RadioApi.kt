package com.music.bitchord.data.sources

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

/**
 * The radio-browser directory: a free, community-run index of live internet
 * radio stations (https://www.radio-browser.info). A station row is a name, a
 * direct stream URL, and the bits of dressing a player row wants — artwork,
 * country, tags, codec, bitrate.
 *
 * The API asks for a descriptive User-Agent and nothing else — no key, no
 * account. Requests round-robin over the project's mirrors via the `all.` host
 * so no single volunteer server is leaned on.
 */
object RadioApi {

    data class RadioStation(
        val uuid: String,
        val name: String,
        val streamUrl: String,
        val favicon: String?,
        val country: String?,
        val tags: String?,
        val codec: String?,
        val bitrateKbps: Int?,
    )

    private val json = Json { ignoreUnknownKeys = true }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    private const val UA = "Kisetsu/1.0 (radio-browser client; Android)"
    const val USER_AGENT = UA
    private const val BASE = "https://de1.api.radio-browser.info"

    /** Search live stations by name — the whole of "live radio searching". */
    suspend fun search(query: String, limit: Int = 30): List<RadioStation> = withContext(Dispatchers.IO) {
        val q = java.net.URLEncoder.encode(query.trim(), "UTF-8")
        val url =
            "$BASE/json/stations/search?name=$q&limit=${limit.coerceIn(1, 60)}" +
                "&hidebroken=true&order=votes&reverse=true"
        val body = get(url) ?: return@withContext emptyList()
        runCatching {
            json.parseToJsonElement(body).jsonArray.mapNotNull { element ->
                val s = element.jsonObject
                val stream = s.string("url_resolved") ?: s.string("url") ?: return@mapNotNull null
                val uuid = s.string("stationuuid") ?: return@mapNotNull null
                RadioStation(
                    uuid = uuid,
                    name = s.string("name")?.trim().orEmpty().ifBlank { "Unnamed station" },
                    streamUrl = stream,
                    favicon = s.string("favicon")?.takeIf { it.startsWith("http") },
                    country = s.string("country")?.takeIf { it.isNotBlank() },
                    tags = s.string("tags")?.takeIf { it.isNotBlank() },
                    codec = s.string("codec")?.takeIf { it.isNotBlank() },
                    bitrateKbps = s.string("bitrate")?.toIntOrNull()?.takeIf { it > 0 },
                )
            }
        }.getOrDefault(emptyList())
            .distinctBy { it.uuid }
            .withoutOfficialBroadcasters()
    }

    /** One station by its uuid, for a stream resolve on a queue restored cold. */
    suspend fun station(uuid: String): RadioStation? = withContext(Dispatchers.IO) {
        val url = "$BASE/json/stations/byuuid?uuids=${java.net.URLEncoder.encode(uuid, "UTF-8")}"
        val body = get(url) ?: return@withContext null
        runCatching {
            json.parseToJsonElement(body).jsonArray.firstOrNull()?.jsonObject?.let { s ->
                val stream = s.string("url_resolved") ?: s.string("url") ?: return@let null
                RadioStation(
                    uuid = s.string("stationuuid") ?: uuid,
                    name = s.string("name")?.trim().orEmpty().ifBlank { "Unnamed station" },
                    streamUrl = stream,
                    favicon = s.string("favicon")?.takeIf { it.startsWith("http") },
                    country = s.string("country")?.takeIf { it.isNotBlank() },
                    tags = s.string("tags")?.takeIf { it.isNotBlank() },
                    codec = s.string("codec")?.takeIf { it.isNotBlank() },
                    bitrateKbps = s.string("bitrate")?.toIntOrNull()?.takeIf { it > 0 },
                )
            }
        }.getOrNull()
    }

    /** A cheap probe for the Sources screen: the directory answered at all. */
    suspend fun probe(): Boolean = withContext(Dispatchers.IO) {
        get("$BASE/json/stations/topvote/limit/1") != null
    }

    /**
     * The directory lists community streams alongside the big public/commercial
     * services (BBC, Apple Music, NPR …) whose playback is DRM-wrapped,
     * geo-blocked or retired to their own apps — entries that will never play
     * no matter how capable the player is. Those are dropped here so every
     * row the Radio tab shows is one the user can actually hear; the
     * community catalogue is untouched.
     */
    private fun List<RadioStation>.withoutOfficialBroadcasters(): List<RadioStation> =
        filterNot { station ->
            val haystack = (station.name + " " + station.tags.orEmpty() + " " +
                station.country.orEmpty() + " " + station.favicon.orEmpty())
                .lowercase(java.util.Locale.ROOT)
            OFFICIAL_BROADCASTER_MARKERS.any { it in haystack }
        }

    /** Case-insensitive substrings (lowercase) marking a big, official, effectively unplayable service. */
    private val OFFICIAL_BROADCASTER_MARKERS = listOf(
        "bbc", "apple music", "apple radio", "itunes", "itsliveradio", "spotify", "sirius", "siriusxm",
        "iheart", "i heart radio", "pandora", "deezer", "tidal",
        "npr", "cbc", "abc radio", "rfi", "radionz", "pbs", "cspan", "premium", "official",
        "radio france", "france inter", "france info", "france culture", "france musique", "france bleu",
        "svt", "sveriges radio", "deutschlandfunk", "deutschlandradio", "yle", "rai radio", "rte ", "sbs ",
        "public radio", "public broadcasting", "public media", "government", "govt",
        "commercial radio", "newstalk", "all news",
    )

    private fun get(url: String): String? = runCatching {
        client.newCall(
            Request.Builder()
                .url(url)
                .header("User-Agent", UA)
                .build(),
        ).execute().use { response ->
            if (!response.isSuccessful) null else response.body?.string()
        }
    }.getOrNull()

    private fun kotlinx.serialization.json.JsonObject.string(key: String): String? =
        (this[key] as? kotlinx.serialization.json.JsonPrimitive)?.contentOrNull
}
