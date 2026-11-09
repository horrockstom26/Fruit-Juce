package com.music.bitchord.data.cover

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Resolved thumbnails for browse items (albums, playlists, podcast shows) whose
 * [com.music.bitchord.data.model.ShelfItem.thumbnailUrl] arrived empty, and for
 * individual tracks — songs and episodes — whose persisted rows arrived without
 * one: listening-ledger entries and saved podcasts recorded by builds whose
 * parsers missed the cover. Filled on demand by UI when a card is drawn; never
 * persisted — the source of truth is YouTube, and a miss is cheap to re-resolve.
 *
 * The two stores key differently: [covers] on browse ids, [songs] on video ids
 * (exactly 11 characters; every other key the app uses — `src:`, `local:`,
 * `MP*`, `VL*`, `UC*`, `PL*` — is a browse or pseudo id that [covers] would
 * never see from the song lookups below).
 *
 * Distinct from [PlaylistCovers], which holds listener-chosen overrides.
 */
object BrowseThumbnails {
    private val _covers = MutableStateFlow<Map<String, String>>(emptyMap())
    val covers: StateFlow<Map<String, String>> = _covers.asStateFlow()

    private val _songs = MutableStateFlow<Map<String, String>>(emptyMap())
    val songs: StateFlow<Map<String, String>> = _songs.asStateFlow()

    fun get(browseId: String?): String? = browseId?.let { _covers.value[it] }

    fun put(browseId: String, url: String) {
        if (browseId.isBlank() || url.isBlank()) return
        val current = _covers.value
        if (current[browseId] == url) return
        _covers.value = current + (browseId to url)
    }

    fun getSong(videoId: String?): String? = videoId?.let { _songs.value[it] }

    fun putSong(videoId: String, url: String) {
        if (videoId.isBlank() || url.isBlank()) return
        val current = _songs.value
        if (current[videoId] == url) return
        _songs.value = current + (videoId to url)
    }

    /**
     * The YouTube id a song lookup can answer for, or null — pseudo ids that
     * are not catalogue entries (`src:` radios, local files, shelf pseudo ids)
     * would only ever fail.
     */
    fun lookupId(videoId: String?): String? =
        videoId?.takeIf { it.length == YOUTUBE_ID_LENGTH }

    private const val YOUTUBE_ID_LENGTH = 11
}
