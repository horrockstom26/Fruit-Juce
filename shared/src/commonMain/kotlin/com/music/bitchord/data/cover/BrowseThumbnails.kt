package com.music.bitchord.data.cover

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Resolved thumbnails for browse items (albums, playlists, podcast shows) whose
 * [com.music.bitchord.data.model.ShelfItem.thumbnailUrl] arrived empty. Filled
 * on demand by UI when a card is drawn; never persisted — the source of truth
 * is the browse page, and a miss is cheap to re-resolve.
 *
 * Distinct from [PlaylistCovers], which holds listener-chosen overrides.
 */
object BrowseThumbnails {
    private val _covers = MutableStateFlow<Map<String, String>>(emptyMap())
    val covers: StateFlow<Map<String, String>> = _covers.asStateFlow()

    fun get(browseId: String?): String? = browseId?.let { _covers.value[it] }

    fun put(browseId: String, url: String) {
        if (browseId.isBlank() || url.isBlank()) return
        val current = _covers.value
        if (current[browseId] == url) return
        _covers.value = current + (browseId to url)
    }
}
