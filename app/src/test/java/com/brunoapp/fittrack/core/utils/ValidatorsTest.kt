package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {

    // ── Height ──

    @Test
    fun `empty height is valid and null`() {
        val result = Validators.parseHeight("")
        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }

    @Test
    fun `valid height parses correctly`() {
        assertEquals(178.0, Validators.parseHeight("178").getOrNull()!!, 0.001)
    }

    @Test
    fun `height accepts comma as decimal separator`() {
        assertEquals(178.5, Validators.parseHeight("178,5").getOrNull()!!, 0.001)
    }

    @Test
    fun `height out of range fails`() {
        assertTrue(Validators.parseHeight("90").isFailure)
        assertTrue(Validators.parseHeight("260").isFailure)
    }

    @Test
    fun `non-numeric height fails`() {
        assertTrue(Validators.parseHeight("abc").isFailure)
    }

    // ── Body weight ──

    @Test
    fun `valid body weight parses`() {
        assertEquals(82.3, Validators.parseBodyWeight("82.3").getOrNull()!!, 0.001)
    }

    @Test
    fun `body weight out of range fails`() {
        assertTrue(Validators.parseBodyWeight("20").isFailure)
        assertTrue(Validators.parseBodyWeight("500").isFailure)
    }

    // ── Lift weight ──

    @Test
    fun `zero lift weight is valid for bodyweight exercises`() {
        assertEquals(0.0, Validators.parseLiftWeight("0").getOrNull()!!, 0.001)
    }

    @Test
    fun `negative lift weight fails`() {
        assertTrue(Validators.parseLiftWeight("-10").isFailure)
    }

    // ── Reps ──

    @Test
    fun `valid reps parse`() {
        assertEquals(12, Validators.parseReps("12").getOrNull())
    }

    @Test
    fun `zero reps fail`() {
        assertTrue(Validators.parseReps("0").isFailure)
    }

    @Test
    fun `decimal reps fail`() {
        assertTrue(Validators.parseReps("8.5").isFailure)
    }
}
