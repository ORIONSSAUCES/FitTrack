package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalcTest {

    @Test
    fun `hitting top of range suggests weight increase and range bottom`() {
        val s = ProgressionCalc.suggest(80.0, 12, 8, 12)!!
        assertEquals(82.5, s.weightKg, 0.001)
        assertEquals(8, s.reps)
        assertTrue(s.isWeightIncrease)
    }

    @Test
    fun `exceeding top of range also increases weight`() {
        val s = ProgressionCalc.suggest(80.0, 14, 8, 12)!!
        assertEquals(82.5, s.weightKg, 0.001)
        assertTrue(s.isWeightIncrease)
    }

    @Test
    fun `below top of range suggests one more rep at same weight`() {
        val s = ProgressionCalc.suggest(80.0, 9, 8, 12)!!
        assertEquals(80.0, s.weightKg, 0.001)
        assertEquals(10, s.reps)
        assertFalse(s.isWeightIncrease)
    }

    @Test
    fun `suggested reps never exceed range top`() {
        val s = ProgressionCalc.suggest(80.0, 11, 8, 12)!!
        assertEquals(12, s.reps)
    }

    @Test
    fun `no history means no suggestion`() {
        assertNull(ProgressionCalc.suggest(null, null, 8, 12))
        assertNull(ProgressionCalc.suggest(80.0, null, 8, 12))
        assertNull(ProgressionCalc.suggest(null, 10, 8, 12))
    }

    @Test
    fun `bodyweight zero kg still progresses reps`() {
        val s = ProgressionCalc.suggest(0.0, 8, 6, 12)!!
        assertEquals(0.0, s.weightKg, 0.001)
        assertEquals(9, s.reps)
    }

    @Test
    fun `custom increment respected`() {
        val s = ProgressionCalc.suggest(30.0, 12, 8, 12, incrementKg = 2.0)!!
        assertEquals(32.0, s.weightKg, 0.001)
    }
}
