package com.music.bitchord.data.settings

import android.content.Context
import android.content.SharedPreferences
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.displayArtist
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

enum class DiscoverKind { RADIO, PODCAST }

/** A radio station or podcast episode/show saved into the Discover page. */
@Serializable
data class DiscoverSave(
    val key: String,
    val kind: String, // radio | podcast
    val title: String,
    val subtitle: String = "",
    val thumbnailUrl: String? = null,
    /** Playable track key for radios/episodes (`src:...` or videoId). */
    val videoId: String? = null,
    /** Browse/show/album id for opening a podcast show page when available. */
    val browseId: String? = null,
    val albumId: String? = null,
    val isPodcastEpisode: Boolean = false,
    val savedAt: Long = System.currentTimeMillis(),
) {
    fun toSong(): Song = Song(
        videoId = videoId ?: browseId ?: key,
        title = title,
        artist = subtitle,
        thumbnailUrl = thumbnailUrl,
        albumId = albumId ?: browseId,
        isPodcastEpisode = isPodcastEpisode || kind == KIND_PODCAST,
    )

    companion object {
        const val KIND_RADIO = "radio"
        const val KIND_PODCAST = "podcast"

        fun ofRadio(song: Song) = DiscoverSave(
            key = song.videoId,
            kind = KIND_RADIO,
            title = song.title,
            subtitle = song.artist,
            thumbnailUrl = song.thumbnailUrl,
            videoId = song.videoId,
            isPodcastEpisode = false,
        )

        fun ofPodcast(song: Song) = DiscoverSave(
            key = song.albumId ?: song.videoId,
            kind = KIND_PODCAST,
            title = song.albumName ?: song.title,
            subtitle = song.displayArtistSafe(),
            thumbnailUrl = song.thumbnailUrl,
            videoId = song.videoId,
            browseId = null,
            albumId = song.albumId,
            isPodcastEpisode = true,
        )
    }
}

private fun Song.displayArtistSafe(): String = try { displayArtist() } catch (_: Throwable) { artist }

/**
 * Device-local Discover saves: liked radios/podcasts and "My Library" pins for the Discover page.
 * Kept in SharedPreferences JSON (same pattern as [HomePins]/[PodcastResume]).
 */
object DiscoverSaves {
    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }
    private val listSer = ListSerializer(DiscoverSave.serializer())

    private val _likedRadios = MutableStateFlow<List<DiscoverSave>>(emptyList())
    private val _likedPodcasts = MutableStateFlow<List<DiscoverSave>>(emptyList())
    private val _libraryRadios = MutableStateFlow<List<DiscoverSave>>(emptyList())
    private val _libraryPodcasts = MutableStateFlow<List<DiscoverSave>>(emptyList())
    private val _recentRadios = MutableStateFlow<List<DiscoverSave>>(emptyList())
    private val _recentPodcasts = MutableStateFlow<List<DiscoverSave>>(emptyList())

    val likedRadios: StateFlow<List<DiscoverSave>> = _likedRadios.asStateFlow()
    val likedPodcasts: StateFlow<List<DiscoverSave>> = _likedPodcasts.asStateFlow()
    val libraryRadios: StateFlow<List<DiscoverSave>> = _libraryRadios.asStateFlow()
    val libraryPodcasts: StateFlow<List<DiscoverSave>> = _libraryPodcasts.asStateFlow()
    val recentRadios: StateFlow<List<DiscoverSave>> = _recentRadios.asStateFlow()
    val recentPodcasts: StateFlow<List<DiscoverSave>> = _recentPodcasts.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("bitchord_settings", Context.MODE_PRIVATE)
        reload()
    }

    fun reload() {
        if (!this::prefs.isInitialized) return
        _likedRadios.value = read(KEY_LIKED_RADIOS)
        _likedPodcasts.value = read(KEY_LIKED_PODCASTS)
        _libraryRadios.value = read(KEY_LIBRARY_RADIOS)
        _libraryPodcasts.value = read(KEY_LIBRARY_PODCASTS)
        _recentRadios.value = read(KEY_RECENT_RADIOS)
        _recentPodcasts.value = read(KEY_RECENT_PODCASTS)
    }

    private fun read(key: String): List<DiscoverSave> = runCatching {
        json.decodeFromString(listSer, prefs.getString(key, null) ?: "[]")
    }.getOrDefault(emptyList())

    private fun write(key: String, flow: MutableStateFlow<List<DiscoverSave>>, value: List<DiscoverSave>) {
        flow.value = value
        if (this::prefs.isInitialized) prefs.edit().putString(key, json.encodeToString(listSer, value)).apply()
    }

    private fun toggleIn(key: String, flow: MutableStateFlow<List<DiscoverSave>>, item: DiscoverSave, max: Int = 200): Boolean {
        val cur = flow.value
        val exists = cur.any { it.key == item.key }
        val next = if (exists) cur.filterNot { it.key == item.key } else (listOf(item.copy(savedAt = System.currentTimeMillis())) + cur).take(max)
        write(key, flow, next)
        return !exists
    }

    fun isLikedRadio(key: String) = _likedRadios.value.any { it.key == key }
    fun isLikedPodcast(key: String) = _likedPodcasts.value.any { it.key == key }
    fun isLibraryRadio(key: String) = _libraryRadios.value.any { it.key == key }
    fun isLibraryPodcast(key: String) = _libraryPodcasts.value.any { it.key == key }

    fun toggleLikedRadio(song: Song): Boolean = toggleIn(KEY_LIKED_RADIOS, _likedRadios, DiscoverSave.ofRadio(song))
    fun toggleLikedPodcast(song: Song): Boolean = toggleIn(KEY_LIKED_PODCASTS, _likedPodcasts, DiscoverSave.ofPodcast(song))
    fun toggleLibraryRadio(song: Song): Boolean = toggleIn(KEY_LIBRARY_RADIOS, _libraryRadios, DiscoverSave.ofRadio(song), max = HomePins.MAX_PINS)
    fun toggleLibraryPodcast(song: Song): Boolean = toggleIn(KEY_LIBRARY_PODCASTS, _libraryPodcasts, DiscoverSave.ofPodcast(song), max = HomePins.MAX_PINS)
    fun removeLikedRadio(key: String) = write(KEY_LIKED_RADIOS, _likedRadios, _likedRadios.value.filterNot { it.key == key })
    fun removeLikedPodcast(key: String) = write(KEY_LIKED_PODCASTS, _likedPodcasts, _likedPodcasts.value.filterNot { it.key == key })
    fun removeLibraryRadio(key: String) = write(KEY_LIBRARY_RADIOS, _libraryRadios, _libraryRadios.value.filterNot { it.key == key })
    fun removeLibraryPodcast(key: String) = write(KEY_LIBRARY_PODCASTS, _libraryPodcasts, _libraryPodcasts.value.filterNot { it.key == key })
    fun removeRecentRadio(key: String) = write(KEY_RECENT_RADIOS, _recentRadios, _recentRadios.value.filterNot { it.key == key })
    fun removeRecentPodcast(key: String) = write(KEY_RECENT_PODCASTS, _recentPodcasts, _recentPodcasts.value.filterNot { it.key == key })

    fun addRecentRadio(song: Song) = addRecent(KEY_RECENT_RADIOS, _recentRadios, DiscoverSave.ofRadio(song))
    fun addRecentPodcast(song: Song) = addRecent(KEY_RECENT_PODCASTS, _recentPodcasts, DiscoverSave.ofPodcast(song))

    private fun addRecent(key: String, flow: MutableStateFlow<List<DiscoverSave>>, item: DiscoverSave) {
        val next = (listOf(item.copy(savedAt = System.currentTimeMillis())) + flow.value.filterNot { it.key == item.key }).take(50)
        write(key, flow, next)
    }

    private const val KEY_LIKED_RADIOS = "discover_liked_radios"
    private const val KEY_LIKED_PODCASTS = "discover_liked_podcasts"
    private const val KEY_LIBRARY_RADIOS = "discover_library_radios"
    private const val KEY_LIBRARY_PODCASTS = "discover_library_podcasts"
    private const val KEY_RECENT_RADIOS = "discover_recent_radios"
    private const val KEY_RECENT_PODCASTS = "discover_recent_podcasts"
}
