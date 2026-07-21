package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ComplianceCalcTest {

    @Test
    fun `daily percent computes correctly`() {
        assertEquals(60, ComplianceCalc.dailyPercent(3, 5))
        assertEquals(100, ComplianceCalc.dailyPercent(5, 5))
        assertEquals(0, ComplianceCalc.dailyPercent(0, 5))
    }

    @Test
    fun `daily percent with no meals is null`() {
        assertNull(ComplianceCalc.dailyPercent(0, 0))
    }

    @Test
    fun `weekly percent averages daily values`() {
        val days = listOf(5 to 5, 3 to 5, 4 to 5) // 100, 60, 80
        assertEquals(80, ComplianceCalc.weeklyPercent(days))
    }

    @Test
    fun `weekly percent ignores days without meals`() {
        val days = listOf(5 to 5, 0 to 0) // 100, ignored
        assertEquals(100, ComplianceCalc.weeklyPercent(days))
    }

    @Test
    fun `weekly percent with no valid days is null`() {
        assertNull(ComplianceCalc.weeklyPercent(listOf(0 to 0)))
        assertNull(ComplianceCalc.weeklyPercent(emptyList()))
    }
}
