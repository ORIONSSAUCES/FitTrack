package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SetEntryResolverTest {

    @Test
    fun `typed values win over placeholders`() {
        val r = SetEntryResolver.resolve("42.5", "9", 40.0, 8)
        assertEquals(42.5, r.weightKg!!, 0.001)
        assertEquals(9, r.reps)
    }

    @Test
    fun `blank fields use placeholders`() {
        val r = SetEntryResolver.resolve("", " ", 40.0, 8)
        assertEquals(40.0, r.weightKg!!, 0.001)
        assertEquals(8, r.reps)
        assertTrue(r.isValid)
    }

    @Test
    fun `comma decimal is accepted`() {
        assertEquals(27.5, SetEntryResolver.resolve("27,5", "8", null, null).weightKg!!, 0.001)
    }

    @Test
    fun `blank without placeholder is invalid`() {
        val r = SetEntryResolver.resolve("", "", null, null)
        assertNull(r.weightKg)
        assertFalse(r.isValid)
    }

    @Test
    fun `invalid typed reps is invalid even with placeholder`() {
        assertFalse(SetEntryResolver.resolve("40", "0", 40.0, 8).isValid)
    }

    @Test
    fun `improvement compares estimated 1RM`() {
        assertTrue(SetEntryResolver.isImprovement(40.0, 8, 40.0, 7))
        assertTrue(SetEntryResolver.isImprovement(42.5, 7, 40.0, 7))
        assertFalse(SetEntryResolver.isImprovement(40.0, 7, 40.0, 7))
        assertFalse(SetEntryResolver.isImprovement(40.0, 8, null, null))
    }
}
