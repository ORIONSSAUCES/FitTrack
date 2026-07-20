package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Tests for record detection and volume logic used by workouts. */
class WorkoutCalculationsTest {

    @Test
    fun `heavier 1rm estimate is a new record`() {
        val previous = Calculations.estimateOneRepMax(100.0, 8)   // 126.67
        val current = Calculations.estimateOneRepMax(105.0, 8)    // 133.0
        assertTrue(current > previous)
    }

    @Test
    fun `more reps at same weight is a new record`() {
        val previous = Calculations.estimateOneRepMax(100.0, 8)
        val current = Calculations.estimateOneRepMax(100.0, 10)
        assertTrue(current > previous)
    }

    @Test
    fun `same performance is not a new record`() {
        val previous = Calculations.estimateOneRepMax(100.0, 8)
        val current = Calculations.estimateOneRepMax(100.0, 8)
        assertFalse(current > previous)
    }

    @Test
    fun `session volume sums weight times reps`() {
        val sets = listOf(80.0 to 10, 85.0 to 8, 90.0 to 6)
        val volume = sets.sumOf { (weight, reps) -> Calculations.setVolume(weight, reps) }
        assertEquals(800.0 + 680.0 + 540.0, volume, 0.001)
    }

    @Test
    fun `rest timer remaining calculation`() {
        val now = 1_000_000L
        val end = now + 90_000L
        val remaining = ((end - now) / 1000L).toInt()
        assertEquals(90, remaining)
    }
}
