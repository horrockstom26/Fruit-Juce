package com.music.bitchord.data.sources

import com.music.bitchord.data.TrackLog
import com.music.bitchord.data.model.Song
import java.util.concurrent.ConcurrentHashMap

/**
 * Live radio as a source: every station the radio-browser directory holds,
 * streamed direct the moment it is asked for.
 *
 * The directory is a *search* surface, not a catalogue: a station's rows
 * never enter the substitution walk (search answers nothing — a station is
 * not a recording of the song it was queried with), so the Radio search tab
 * is the one door in. It calls [searchStations], tags each row with this
 * source's [SourceRegistry.trackKey], and playing one resolves right back
 * here through the ordinary `bitchord://source` path.
 */
class RadioSource(
    override val config: SourceConfig,
) : MusicSource, SourceRegistry.ConfigBacked {

    override val configId: String get() = config.id
    override val kind: SourceKind get() = SourceKind.RADIO

    override val displayName: String get() = config.label.ifBlank { SourceKind.RADIO.label }

    override suspend fun health(): SourceHealth {
        val ok = try {
            RadioApi.probe()
        } catch (_: Exception) {
            false
        }
        return if (ok) {
            SourceHealth.Ok("directory reachable")
        } else {
            SourceHealth.Unreachable("radio-browser directory did not answer")
        }
    }

    /**
     * Nothing, always. This source exists so the Radio search tab can find
     * stations and the resolver can serve them; it must never answer the
     * resolver's catalogue question "do you have this song?" — a live station
     * happens to be playing whatever it is playing, and substituting that for
     * a track the listener chose would be a wrong answer delivered with
     * confidence.
     */
    override suspend fun search(
        query: String,
        limit: Int,
        waitForAll: Boolean,
        request: StreamRequest?,
    ): List<Song> = emptyList()

    /** The rows the Radio search tab shows, each tagged to play back through here. */
    suspend fun searchStations(query: String, limit: Int = 30): List<Song> {
        val stations = try {
            RadioApi.search(query, limit)
        } catch (_: Exception) {
            return emptyList()
        }
        stations.forEach { memo[it.uuid] = it }
        return stations.map { it.toSong() }
    }

    override suspend fun stream(trackId: String, request: StreamRequest): SourceStream? {
        val station = memo[trackId] ?: RadioApi.station(trackId) ?: run {
            TrackLog.d(TAG, "  ✗ radio stream: station $trackId not found")
            return null
        }
        memo[trackId] = station
        TrackLog.d(TAG, "  ✓ radio stream '${station.name}' -> ${station.streamUrl.take(80)}")
        return SourceStream(
            url = station.streamUrl,
            format = StreamFormat(
                codec = station.codec?.lowercase(),
                kbps = station.bitrateKbps,
            ),
            // Live: the stream has no end and no runtime. Leaving the duration
            // unset is what tells the player it is a broadcast, not a broken file.
            durationSec = null,
            headers = mapOf("User-Agent" to RadioApi.USER_AGENT),
            backend = BACKEND_LIVE_RADIO,
        )
    }

    /** Station rows found this session, so a replay skips a directory round trip. */
    private val memo = ConcurrentHashMap<String, RadioApi.RadioStation>()

    override fun mimeTypeOf(trackId: String): String? =
        memo[trackId]?.streamUrl?.let { url ->
            val u = url.lowercase()
            when {
                ".m3u8" in u -> "application/x-mpegURL"
                u.endsWith(".mp3") -> "audio/mpeg"
                u.endsWith(".aac") -> "audio/aac"
                u.endsWith(".ogg") || u.endsWith(".opus") -> "audio/ogg"
                u.endsWith(".flac") -> "audio/flac"
                else -> null
            }
        }

    private fun RadioApi.RadioStation.toSong(): Song = Song(
        videoId = SourceRegistry.trackKey(config.id, uuid),
        title = name,
        artist = listOfNotNull(country, tags?.split(',')?.firstOrNull()?.trim()?.replaceFirstChar { it.uppercase() })
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(" · ")
            .ifBlank { "Live radio" },
        thumbnailUrl = favicon,
        durationText = null,
        sourceQuality = "HIGH",
    )

    companion object {
        private const val TAG = "BitChord"

        /** Provenance label, shown in stats-for-nerds so a track's path is answerable on screen. */
        const val BACKEND_LIVE_RADIO = "Live radio"
    }
}
