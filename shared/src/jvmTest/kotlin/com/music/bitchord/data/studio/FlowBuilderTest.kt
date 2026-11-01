package com.music.bitchord.data.studio

import com.music.bitchord.data.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * The Studio's flow builder is all scoring and no network, so its behaviour
 * can be pinned: seeds lead, strangers only where the mood admits them, and
 * a strict mood keeps to what the listener already has.
 */
class FlowBuilderTest {

    private fun song(id: String, title: String, artist: String) = Song(
        videoId = id,
        title = title,
        artist = artist,
        thumbnailUrl = null,
    )

    @Test
    fun seeds_open_the_list_and_strangers_arrive_late() {
        val favorites = listOf(
            song("f1", "Sunrise", "Alpha"),
            song("f2", "Highway", "Beta"),
            song("f3", "Quiet", "Gamma"),
        )
        val picks = FlowBuilder.build(
            mood = StudioMood.CHILL,
            favorites = favorites,
            history = emptyList(),
            quickPicks = listOf(
                song("s1", "Some Other Band Anthem", "Stranger"),
                song("s2", "Stranger也很Lofi", "Stranger2"),
                song("s3", "Whatever", "Stranger3"),
            ),
            length = 10,
        )
        assertTrue(picks.isNotEmpty())
        val reasons = picks.map { it.reasons.firstOrNull() }
        // Every seed is in, and no stranger displaces the top of the draft.
        assertTrue(reasons.first() == "In your library")
        favorites.forEach { favorite ->
            assertTrue(picks.any { it.song.videoId == favorite.videoId })
        }
    }

    @Test
    fun a_strict_mood_leaves_no_room_for_strangers() {
        val favorites = listOf(
            song("f1", "Ambient Study", "Alpha"),
            song("f2", "Focus Groove", "Beta"),
        )
        val picks = FlowBuilder.build(
            mood = StudioMood.FOCUS,
            favorites = favorites,
            history = emptyList(),
            quickPicks = listOf(song("s1", "X", "Stranger")),
            length = 10,
        )
        assertEquals(listOf("f1", "f2"), picks.map { it.song.videoId })
    }

    @Test
    fun empty_taste_builds_an_empty_draft() {
        val picks = FlowBuilder.build(
            mood = StudioMood.PARTY,
            favorites = emptyList(),
            history = emptyList(),
            quickPicks = emptyList(),
        )
        assertTrue(picks.isEmpty())
    }

    @Test
    fun length_is_honoured_and_no_track_repeats() {
        val favorites = (1..30).map { song("f$it", "Track $it", "Artist $it") }
        val picks = FlowBuilder.build(
            mood = StudioMood.PARTY,
            favorites = favorites,
            history = emptyList(),
            quickPicks = emptyList(),
            length = 24,
        )
        assertEquals(24, picks.size)
        assertEquals(24, picks.map { it.song.videoId }.toSet().size)
    }

    @Test
    fun recency_does_not_evict_a_library_seed_carrying_the_same_track() {
        // History re-heard of a liked track: the library reason wins, because
        // seeds resolve first.
        val shared = song("f1", "Echoes", "Alpha")
        val picks = FlowBuilder.build(
            mood = StudioMood.NIGHT,
            favorites = listOf(shared),
            history = listOf(shared, song("h1", "Midnight Drive", "Beta")),
            quickPicks = emptyList(),
        )
        val echo = picks.first { it.song.videoId == "f1" }
        assertEquals("In your library", echo.reasons.first())
    }

    @Test
    fun mood_words_lift_a_pick() {
        val ordinary = song("o1", "Song One", "Alpha")
        val onMood = song("m1", "Rain on autumn", "Beta")
        val picks = FlowBuilder.build(
            mood = StudioMood.CHILL,
            favorites = listOf(ordinary, onMood),
            history = emptyList(),
            quickPicks = emptyList(),
        )
        // Same pool, same seed status; the mood's own words break the tie up.
        assertEquals("m1", picks.first().song.videoId)
    }
}
