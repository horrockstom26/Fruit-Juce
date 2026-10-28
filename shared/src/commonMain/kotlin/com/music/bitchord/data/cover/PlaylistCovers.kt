package com.music.bitchord.data.cover

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Listener-chosen covers for playlists, keyed by browse id.
 *
 * A playlist's cover normally comes from whichever track YouTube picked for
 * it, which is rarely the picture the listener would choose. Kisetsu v7.2
 * lets a playlist wear a local photo instead: the app persists the chosen
 * `content://` URI, and this store is the one place the shared UI reads it
 * from. Empty by default, so nothing changes for anyone who never picks one.
 *
 * The store itself holds no Android types — the shared pages can read it
 * without knowing how a URI is opened.
 */
object PlaylistCovers {
    private val _covers = MutableStateFlow<Map<String, String>>(emptyMap())

    /** Browse id → image URI. */
    val covers = _covers.asStateFlow()

    fun set(browseId: String, uri: String?) {
        _covers.value = if (uri == null) {
            _covers.value - browseId
        } else {
            _covers.value + (browseId to uri)
        }
    }

    /** Replaces the whole map — used once at startup, from persisted choices. */
    fun setAll(covers: Map<String, String>) {
        _covers.value = covers
    }

    fun coverFor(browseId: String?): String? = browseId?.let { _covers.value[it] }
}
