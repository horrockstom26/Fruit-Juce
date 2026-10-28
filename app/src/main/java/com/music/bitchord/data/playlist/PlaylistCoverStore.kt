package com.music.bitchord.data.playlist

import android.content.Context
import android.content.SharedPreferences
import com.music.bitchord.data.cover.PlaylistCovers
import org.json.JSONObject

/**
 * Persistence for [PlaylistCovers] on Android: one JSON object of browse id →
 * image URI in shared preferences, republished into the shared store on start.
 *
 * A picked cover is a document URI granted to this app, so it survives
 * restarts; the string is stored as-is rather than copied, which keeps the
 * choice small and lets the listener replace the picture in place.
 */
object PlaylistCoverStore {
    private const val PREFS = "kisetsu_playlist_covers"
    private const val KEY_COVERS = "covers_json"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        PlaylistCovers.setAll(load())
    }

    private fun load(): Map<String, String> = runCatching {
        val raw = prefs.getString(KEY_COVERS, null) ?: return emptyMap()
        val json = JSONObject(raw)
        buildMap {
            json.keys().forEach { key ->
                val value = json.optString(key)
                if (value.isNotBlank()) put(key, value)
            }
        }
    }.getOrDefault(emptyMap())

    fun set(browseId: String, uri: String?) {
        PlaylistCovers.set(browseId, uri)
        persist()
    }

    private fun persist() {
        val json = JSONObject()
        PlaylistCovers.covers.value.forEach { (key, value) -> json.put(key, value) }
        prefs.edit().putString(KEY_COVERS, json.toString()).apply()
    }
}
