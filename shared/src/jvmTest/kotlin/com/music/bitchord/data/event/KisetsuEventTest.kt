package com.music.bitchord.data.event

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.Date

/**
 * The event is a date switch, so its boundaries are the whole behaviour: the
 * app must be dressed on 31 October and back to normal on 1 November without
 * anything being cleared first.
 */
class KisetsuEventTest {

    private fun date(year: Int, month: Int, day: Int): Date =
        Calendar.getInstance().apply {
            clear()
            set(year, month, day, 12, 0, 0)
        }.time

    @Test
    fun october_is_halloween() {
        val phase = KisetsuEvent.phaseAt(date(2026, Calendar.OCTOBER, 1))
        assertTrue(phase.halloween)
        assertFalse(phase.halloweenDay)
        assertEquals("🎃", phase.emoji)
    }

    @Test
    fun halloween_day_is_the_thirty_first() {
        val phase = KisetsuEvent.phaseAt(date(2026, Calendar.OCTOBER, 31))
        assertTrue(phase.halloween)
        assertTrue(phase.halloweenDay)
        assertEquals("🎃", phase.emoji)
    }

    @Test
    fun november_first_is_back_to_normal() {
        val phase = KisetsuEvent.phaseAt(date(2026, Calendar.NOVEMBER, 1))
        assertFalse(phase.halloween)
        assertFalse(phase.halloweenDay)
        assertEquals("🌸", phase.emoji)
    }

    @Test
    fun november_switches_picks_to_rock_and_roll() {
        val october = KisetsuEvent.phaseAt(date(2026, Calendar.OCTOBER, 31))
        val november = KisetsuEvent.phaseAt(date(2026, Calendar.NOVEMBER, 1))
        assertTrue(october.halloween)
        assertFalse(november.halloween)
    }
}
