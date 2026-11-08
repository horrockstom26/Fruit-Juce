package com.music.bitchord.data.stats

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.settings.DiscoverSaves
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * One entry of the home's "Jump back in · Podcasts" row: an episode this
 * device was last heard playing, and exactly how far into it that was.
 *
 * [positionMs] is the point the next tap resumes from — never the top.
 * [durationMs] rides along when the decoder knew one, so the card can draw
 * how much is left without asking the network.
 */
@Immutable
@kotlinx.serialization.Serializable
data class PodcastBookmark(
    val videoId: String,
    val title: String,
    /** The show the episode belongs to — the field the card bills it under. */
    val showName: String,
    val thumbnailUrl: String? = null,
    val durationMs: Long = 0L,
    val positionMs: Long = 0L,
    val updatedAt: Long = 0L,
) {
    /** How much of the episode was behind the listener, 0f..1f. */
    val fraction: Float
        get() = if (durationMs > 0) (positionMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f
}

/**
 * Where the player's ticking becomes podcast resume points.
 *
 * Spotify's home answers "the same episode, from the same second" — which
 * needs a position, and positions are only honest while audio is actually
 * coming out. So this is fed from the service's progress sampler, the one
 * loop that ticks exactly while the player plays, and it keeps the last
 * position of every podcast episode it is shown. Music tracks are waved
 * through: a song half-heard is a song skipped, and no song belongs on a
 * "jump back in" for podcasts.
 *
 * Kept as JSON in the settings file, newest first, twenty deep — a list of
 * twenty short rows is not a job for Room.
 */
object PodcastResume {

    private const val MAX_ENTRIES = 20

    /** A position is written back at most this often per episode, and on a pause. */
    private const val SAVE_EVERY_MS = 5_000L

    private lateinit var prefs: SharedPreferences
    private val json = Json
    private val serializer = ListSerializer(PodcastBookmark.serializer())

    private val _entries = MutableStateFlow<List<PodcastBookmark>>(emptyList())

    /** Most recently heard first. */
    val entries: StateFlow<List<PodcastBookmark>> = _entries.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("bitchord_settings", Context.MODE_PRIVATE)
        reload()
    }

    /** Re-reads off disk — shares the settings file, so an import lands here too. */
    fun reload() {
        if (!this::prefs.isInitialized) return
        _entries.value = runCatching {
            json.decodeFromString(serializer, prefs.getString(KEY_ENTRIES, null) ?: "[]")
        }.getOrDefault(emptyList())
    }

    /**
     * One tick of the sampler: [positionMs] into [song], whose full length is
     * [durationMs] when the decoder reports one. Non-episodes are ignored
     * outright; an episode is filed at the front with its position, and the
     * file written only when the position has moved a [SAVE_EVERY_MS] worth —
     * a pref write a second is a tax on every tick for a fact nobody needs
     * that fresh.
     */
    @Synchronized
    fun onTick(song: Song, positionMs: Long, durationMs: Long) {
        if (!song.isPodcastEpisode) return
        if (positionMs <= 0L) return
        // The listener held the card and took this episode off the row while
        // it was still the thing playing. Their "no" outlives the very next
        // tick — which would otherwise file it right back — and stands only
        // until a different episode is the one coming out of the speakers.
        if (song.videoId != lastTickedId) suppressedId = null
        lastTickedId = song.videoId
        if (song.videoId == suppressedId) return
        val previous = _entries.value.firstOrNull { it.videoId == song.videoId }
        if (previous != null &&
            positionMs - previous.positionMs in 1 until SAVE_EVERY_MS &&
            previous.positionMs < positionMs
        ) {
            return
        }
        val effectiveDuration = durationMs.takeIf { it > 0 } ?: previous?.durationMs ?: 0L
        val bookmark = PodcastBookmark(
            videoId = song.videoId,
            title = song.title,
            showName = song.artist,
            thumbnailUrl = song.thumbnailUrl,
            durationMs = effectiveDuration,
            positionMs = positionMs,
            updatedAt = System.currentTimeMillis(),
        )
        if (effectiveDuration > 0L && positionMs >= (effectiveDuration * 0.92f).toLong()) {
            DiscoverSaves.markEpisodePlayed(song.videoId, true)
        }
        save(listOf(bookmark) + _entries.value.filterNot { it.videoId == song.videoId })
    }

    /** The listener held the card and told it to go away. */
    fun remove(videoId: String) {
        suppressedId = videoId
        save(_entries.value.filterNot { it.videoId == videoId })
    }

    private fun save(value: List<PodcastBookmark>) {
        _entries.value = value
        if (!this::prefs.isInitialized) return
        prefs.edit().putString(KEY_ENTRIES, json.encodeToString(serializer, value)).apply()
    }

    private const val KEY_ENTRIES = "podcast_resume"

    /** An episode the listener removed while it was still playing — see [onTick]. */
    private var suppressedId: String? = null
    private var lastTickedId: String? = null
}
