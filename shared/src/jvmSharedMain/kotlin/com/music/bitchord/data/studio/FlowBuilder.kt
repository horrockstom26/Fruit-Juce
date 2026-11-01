package com.music.bitchord.data.studio

import com.music.bitchord.data.model.Song

/**
 * The Music Studio's moods.
 *
 * A draft playlist is built "on flow": the listener names the mood and the
 * studio answers with a track list that obeys it — familiar artists leading,
 * strangers only where the mood admits them, so the whole thing plays like
 * one thought rather than a shuffle.
 *
 * Each mood carries the words it recognises in a song's own metadata and a
 * [spread] — how loosely the build hews to the seed artists — as a coarse
 * stand-in for the tempo-and-energy analysis a mid-playback engine can do
 * but a build-on-press cannot.
 */
enum class StudioMood(
    val keywords: Set<String>,
    /** 0f is "seeds only, no strangers allowed"; 1f feels like radio. */
    val spread: Float,
) {
    CHILL(
        keywords = setOf("acoustic", "unplugged", "lo-fi", "lofi", "rain", "sunset", "coffee"),
        spread = 0.5f,
    ),
    FOCUS(
        keywords = setOf("focus", "instrumental", "ambient", "study", "groove", "electronic"),
        spread = 0.35f,
    ),
    ENERGY(
        keywords = setOf("anthem", "run", "lean", "energy", "hot", "jump", "alive"),
        spread = 0.55f,
    ),
    PARTY(
        keywords = setOf("dance", "party", "club", "mix", "remix", "pop", "disco"),
        spread = 0.7f,
    ),
    NIGHT(
        keywords = setOf("midnight", "night", "moon", "blue", "late", "drive", "city"),
        spread = 0.45f,
    );

    companion object {
        /** Tolerant read of a mood value remembered in preferences. */
        fun fromName(raw: String?): StudioMood? =
            entries.firstOrNull { it.name.equals(raw, ignoreCase = true) }
    }
}

/** One scored candidate: the pick's song, its score and the reasons to show. */
data class ScoredPick(
    val song: Song,
    val score: Float,
    /**
     * Short human reasons (first wins — "From your Recents", "In your
     * library"), kept on the pick so the UI can show them beside the track
     * instead of re-deriving them.
     */
    val reasons: List<String>,
)

/**
 * The Studio's flow builder: the scoring core behind "the perfect playlist
 * based on flow".
 *
 * The inputs are deliberately boring pools — what the listener loves, what
 * they have played recently, and the recommending source's picks — and the
 * output is an ordered track list whose shape a listener can feel: seeds
 * first, familiar colours under them, strangers only where the mood's
 * [StudioMood.spread] buys room, and a gentle arc so it is not a shuffle.
 *
 * Scoring is deterministic and pure, which is what makes it testable; the
 * caller supplies the candidate pools and this decides what belongs and
 * where it seats.
 */
object FlowBuilder {

    /** Rough schedule: seeds and library favourites open, new colour closes. */
    const val DEFAULT_LENGTH = 24

    /** The reason a "stranger" pick carries — the ones spread>0 allows in. */
    const val STRANGER_REASON = "New for this mood"

    /**
     * Builds a mood playlist.
     *
     * @param favorites tracks the listener has liked or saved — the seeds.
     * @param history what they played recently; ordered most recent first.
     * @param quickPicks the source's own personalised picks — strangers.
     * @param mood the flow the list is being built for.
     * @param length how many tracks to return.
     */
    fun build(
        mood: StudioMood,
        favorites: List<Song>,
        history: List<Song>,
        quickPicks: List<Song>,
        length: Int = DEFAULT_LENGTH,
    ): List<ScoredPick> {
        if (length <= 0) return emptyList()
        val seedPool = favorites + history
        val artistCounts = seedPool
            .filter { it.artist.isNotBlank() }
            .groupingBy { it.artist.trim() }
            .eachCount()

        val scored = LinkedHashMap<String, ScoredPick>()

        fun remember(song: Song, score: Float, vararg reasons: String) {
            if (song.videoId.isBlank()) return
            // First come, first reasoned: the pools are ordered so earlier
            // callers carry the more confident reason for the same track.
            scored.putIfAbsent(song.videoId, ScoredPick(song, score, reasons.toList()))
        }

        // Seeds: liked and library music. These open the list.
        favorites.forEach { song ->
            remember(
                song,
                1.0f
                        + (artistCounts[song.artist.trim()] ?: 0) * 0.03f
                        + moodMomentum(song, mood),
                "In your library",
            )
        }

        // Recent plays: familiar enough to sit beside the seeds, weighted by
        // recency — history arrives most recent first.
        history.forEachIndexed { index, song ->
            val recency = 0.5f * (1f - index.toFloat() / (history.size + 1))
            remember(
                song,
                0.8f + recency + moodMomentum(song, mood),
                "From your Recents",
            )
        }

        // Strangers, only where the mood's spread buys room for them.
        if (mood.spread >= SPREAD_STRANGER_FLOOR) {
            quickPicks.forEach { song ->
                val artist = song.artist.trim()
                val familiarArtist = artist.isNotBlank() &&
                    artistCounts.keys.any { it.equals(artist, ignoreCase = true) }
                remember(
                    song,
                    (if (familiarArtist) 0.7f else 0.3f) + moodMomentum(song, mood),
                    if (familiarArtist) "You play $artist" else STRANGER_REASON,
                )
            }
        }

        if (scored.isEmpty()) return emptyList()

        // The order — a flow is not just a ranking. The seeds lead as ranked;
        // strangers are threaded in every [stride] positions so they colour
        // the list without breaking it.
        val ranked = scored.values.sortedByDescending { it.score }
        val seeds = ranked.filter { it.reasons.firstOrNull() != STRANGER_REASON }
        val strangers = ranked.filter { it.reasons.firstOrNull() == STRANGER_REASON }
        val stride = (seeds.size * (1.1f - mood.spread)).toInt().coerceAtLeast(2)
        val built = interleave(seeds, strangers, stride)

        return built.take(length)
    }

    /** Threads the strangers through the seeds at fixed intervals. */
    private fun interleave(
        seeds: List<ScoredPick>,
        strangers: List<ScoredPick>,
        stride: Int,
    ): List<ScoredPick> {
        if (strangers.isEmpty()) return seeds
        val out = ArrayList<ScoredPick>(seeds.size + strangers.size)
        var seedIndex = 0
        var strangerIndex = 0
        while (seedIndex < seeds.size || strangerIndex < strangers.size) {
            repeat(stride) { if (seedIndex < seeds.size) out.add(seeds[seedIndex++]) }
            if (strangerIndex < strangers.size) out.add(strangers[strangerIndex++])
        }
        return out
    }

    /** Below this, the mood keeps to its seeds and strangers stay out. */
    private const val SPREAD_STRANGER_FLOOR = 0.4f

    /**
     * Mood momentum from the pick's own words — the title and artist singing
     * the mood's language climbs the score a little. Small by design: the
     * words are a nudge, the pools are the argument.
     */
    private fun moodMomentum(song: Song, mood: StudioMood): Float {
        val text = (song.title + " " + song.artist).lowercase()
        return if (mood.keywords.any { text.contains(it) }) 0.15f else 0f
    }
}
