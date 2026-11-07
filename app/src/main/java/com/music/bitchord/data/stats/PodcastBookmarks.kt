package com.music.bitchord.data.stats

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

@Serializable
data class PodcastBookmarkMarker(
    val positionMs: Long,
    val label: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

/** On-device timestamp bookmarks for podcast episodes (scoped, no network). */
object PodcastBookmarks {
    private lateinit var prefs: SharedPreferences
    private val json = Json { ignoreUnknownKeys = true }
    private val mapSerializer = MapSerializer(String.serializer(), ListSerializer(PodcastBookmarkMarker.serializer()))
    private val _all = MutableStateFlow<Map<String, List<PodcastBookmarkMarker>>>(emptyMap())
    val all: StateFlow<Map<String, List<PodcastBookmarkMarker>>> = _all.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("bitchord_settings", Context.MODE_PRIVATE)
        reload()
    }

    fun reload() {
        if (!this::prefs.isInitialized) return
        _all.value = runCatching {
            json.decodeFromString(mapSerializer, prefs.getString(KEY, null) ?: "{}")
        }.getOrDefault(emptyMap())
    }

    fun markers(videoId: String): List<PodcastBookmarkMarker> =
        _all.value[videoId].orEmpty().sortedBy { it.positionMs }

    fun add(videoId: String, positionMs: Long, label: String = "") {
        if (positionMs < 0L) return
        val list = (markers(videoId) + PodcastBookmarkMarker(positionMs, label.ifBlank {
            val s = positionMs / 1000
            "%d:%02d".format(s / 60, s % 60)
        })).distinctBy { it.positionMs / 1000 }.sortedBy { it.positionMs }
        save(_all.value + (videoId to list))
    }

    fun remove(videoId: String, positionMs: Long) {
        val list = markers(videoId).filterNot { it.positionMs == positionMs }
        save(if (list.isEmpty()) _all.value - videoId else _all.value + (videoId to list))
    }

    private fun save(value: Map<String, List<PodcastBookmarkMarker>>) {
        _all.value = value
        if (!this::prefs.isInitialized) return
        prefs.edit().putString(KEY, json.encodeToString(mapSerializer, value)).apply()
    }

    private const val KEY = "podcast_timestamp_bookmarks"
}
