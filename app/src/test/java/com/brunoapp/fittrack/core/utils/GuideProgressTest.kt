package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class GuideProgressTest {

    private val today = LocalDate.of(2026, 11, 12)

    @Test
    fun `fraction from start to target`() {
        assertEquals(0f, GuideProgress.fraction(112.6, 112.6, 96.0), 0.001f)
        assertEquals(0.5f, GuideProgress.fraction(112.6, 104.3, 96.0), 0.001f)
        assertEquals(1f, GuideProgress.fraction(112.6, 95.0, 96.0), 0.001f)
    }

    @Test
    fun `weekly loss compares 7-day averages`() {
        val entries = listOf(
            today.minusDays(10) to 111.5, today.minusDays(8) to 111.3,   // previous week avg 111.4
            today.minusDays(3) to 110.6, today to 110.2                   // this week avg 110.4
        )
        assertEquals(1.0, GuideProgress.weeklyLoss(entries, today)!!, 0.001)
    }

    @Test
    fun `weekly loss needs data in both windows`() {
        assertNull(GuideProgress.weeklyLoss(listOf(today to 109.6), today))
    }

    @Test
    fun `pace classification`() {
        assertEquals(GuideProgress.Pace.NO_DATA, GuideProgress.pace(null))
        assertEquals(GuideProgress.Pace.GAINING, GuideProgress.pace(-0.3))
        assertEquals(GuideProgress.Pace.TOO_SLOW, GuideProgress.pace(0.3))
        assertEquals(GuideProgress.Pace.IDEAL, GuideProgress.pace(0.8))
        assertEquals(GuideProgress.Pace.TOO_FAST, GuideProgress.pace(1.5))
    }

    @Test
    fun `weeks left at current pace`() {
        assertEquals(14, GuideProgress.weeksLeft(109.6, 96.0, 1.0))
        assertEquals(0, GuideProgress.weeksLeft(95.5, 96.0, 0.8))
        assertNull(GuideProgress.weeksLeft(109.6, 96.0, 0.0))
    }
}
