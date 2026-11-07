package com.music.bitchord.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

/**
 * One shortcut on the home's Quick launch panel — the row of round tiles just
 * under the recent grid.
 *
 * [videoId] set (and [browseId] null) means the tile *plays*: a song, a
 * podcast episode, or a live radio station carrying the source's own track
 * key, which resolves back into its stream at playback. [browseId] set means
 * the tile *opens a page*: an artist, an album, a playlist, a podcast show.
 * A tile never carries both — what a tap does is settled the moment it was
 * pinned, not guessed at again every time it is tapped.
 */
@Immutable
@kotlinx.serialization.Serializable
data class HomePin(
    val videoId: String? = null,
    val browseId: String? = null,
    val title: String,
    val subtitle: String = "",
    val thumbnailUrl: String? = null,
    /** What art (and which fallback icon) to draw: song, radio, artist, … */
    val kind: String = KIND_SONG,
) {
    /** The stable identity a pin is filed and removed under. */
    val key: String get() = videoId ?: browseId ?: title

    companion object {
        const val KIND_SONG = "song"
        const val KIND_EPISODE = "episode"
        const val KIND_RADIO = "radio"
        const val KIND_ARTIST = "artist"
        const val KIND_ALBUM = "album"
        const val KIND_SHOW = "show"

        fun ofSong(song: com.music.bitchord.data.model.Song, kind: String = KIND_SONG) = HomePin(
            videoId = song.videoId,
            title = song.title,
            subtitle = song.artist,
            thumbnailUrl = song.thumbnailUrl,
            kind = kind,
        )
    }
}

/**
 * The home's Quick launch panel, held on this device only.
 *
 * Spotify keeps a handful of one-tap shortcuts at the top of the home feed;
 * this is that row, and the listener decides what sits in it — hold a song,
 * an episode, a station, a release, an artist or a show anywhere a menu
 * opens and choose "Pin to home". Held as JSON in the settings file rather
 * than a database: a dozen short rows with nothing to query is not a job for
 * Room, and it rides the same import/backup as every other preference.
 */
object HomePins {

    /** Wide enough to scroll, shallow enough that every pin stays one tap away. */
    const val MAX_PINS = 12

    private lateinit var prefs: SharedPreferences
    private val json = Json
    private val serializer = ListSerializer(HomePin.serializer())

    private val _pins = MutableStateFlow<List<HomePin>>(emptyList())

    /** Pinned order — oldest first, so a pin never jumps under the listener's thumb. */
    val pins: StateFlow<List<HomePin>> = _pins.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("bitchord_settings", Context.MODE_PRIVATE)
        reload()
    }

    /**
     * Re-reads the list off disk. Shares its preference file with
     * [AppSettings], so an import replaces what is stored here — see
     * [SearchHistory.reload], which does the same for the same reason.
     */
    fun reload() {
        if (!this::prefs.isInitialized) return
        _pins.value = runCatching {
            json.decodeFromString(serializer, prefs.getString(KEY_PINS, null) ?: "[]")
        }.getOrDefault(emptyList())
    }

    /** Whether an item matching [pin]'s key is already on the panel. */
    fun isPinned(pin: HomePin): Boolean = _pins.value.any { it.key == pin.key }

    /**
     * Pins [pin], or takes it off if it is already there. New pins join the
     * end of the row; when the panel is full the oldest pin gives way. Answers
     * whether the pin is on the panel after the call, so the caller can warn
     * when a pin was refused for space rather than silently swallowed.
     */
    fun toggle(pin: HomePin): Boolean {
        val nowPinned = if (isPinned(pin)) {
            remove(pin.key)
            false
        } else {
            save((_pins.value + pin).takeLast(MAX_PINS))
            true
        }
        return nowPinned
    }

    fun remove(key: String) {
        save(_pins.value.filterNot { it.key == key })
    }

    private fun save(value: List<HomePin>) {
        _pins.value = value
        prefs.edit().putString(KEY_PINS, json.encodeToString(serializer, value)).apply()
    }

    private const val KEY_PINS = "home_quick_pins"
}
