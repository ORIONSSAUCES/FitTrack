package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CalculationsTest {

    // ── Epley 1RM ──

    @Test
    fun `1RM with single rep returns the weight itself`() {
        assertEquals(100.0, Calculations.estimateOneRepMax(100.0, 1), 0.001)
    }

    @Test
    fun `1RM with 10 reps uses Epley formula`() {
        // 100 * (1 + 10/30) = 133.33
        assertEquals(133.333, Calculations.estimateOneRepMax(100.0, 10), 0.01)
    }

    @Test
    fun `1RM with invalid input returns zero`() {
        assertEquals(0.0, Calculations.estimateOneRepMax(0.0, 10), 0.001)
        assertEquals(0.0, Calculations.estimateOneRepMax(100.0, 0), 0.001)
        assertEquals(0.0, Calculations.estimateOneRepMax(-50.0, 5), 0.001)
    }

    // ── Volume ──

    @Test
    fun `set volume multiplies weight by reps`() {
        assertEquals(800.0, Calculations.setVolume(80.0, 10), 0.001)
    }

    @Test
    fun `set volume with invalid input returns zero`() {
        assertEquals(0.0, Calculations.setVolume(-10.0, 5), 0.001)
        assertEquals(0.0, Calculations.setVolume(50.0, 0), 0.001)
    }

    // ── Macros ──

    @Test
    fun `calories from macros uses 4-4-9 rule`() {
        // 150*4 + 300*4 + 80*9 = 600 + 1200 + 720 = 2520
        assertEquals(2520, Calculations.caloriesFromMacros(150.0, 300.0, 80.0))
    }

    // ── Goal percentage ──

    @Test
    fun `percentage of goal clamps at 100`() {
        assertEquals(100, Calculations.percentageOfGoal(2500.0, 2000.0))
    }

    @Test
    fun `percentage of goal with zero goal returns zero`() {
        assertEquals(0, Calculations.percentageOfGoal(500.0, 0.0))
    }

    @Test
    fun `percentage of goal computes correctly`() {
        assertEquals(75, Calculations.percentageOfGoal(1500.0, 2000.0))
    }

    // ── Weekly average ──

    @Test
    fun `weekly average of empty list is null`() {
        assertNull(Calculations.weeklyAverage(emptyList()))
    }

    @Test
    fun `weekly average computes mean`() {
        val avg = Calculations.weeklyAverage(listOf(80.0, 80.5, 81.0))
        assertEquals(80.5, avg!!, 0.001)
    }
}
