package com.brunoapp.fittrack.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class DateUtilsTest {

    private val zone = ZoneId.of("America/Argentina/Buenos_Aires")

    @Test
    fun `same week detects days in one ISO week`() {
        // Monday and Friday of the same week (July 2026: Mon 13th, Fri 17th)
        val monday = Instant.parse("2026-07-13T10:00:00Z")
        val friday = Instant.parse("2026-07-17T10:00:00Z")
        assertTrue(DateUtils.isSameWeek(monday, friday, zone))
    }

    @Test
    fun `sunday and next monday are different weeks`() {
        val sunday = Instant.parse("2026-07-19T10:00:00Z")
        val nextMonday = Instant.parse("2026-07-20T10:00:00Z")
        assertFalse(DateUtils.isSameWeek(sunday, nextMonday, zone))
    }

    @Test
    fun `same month detection`() {
        val first = Instant.parse("2026-07-01T10:00:00Z")
        val last = Instant.parse("2026-07-30T10:00:00Z")
        assertTrue(DateUtils.isSameMonth(first, last, zone))
        val august = Instant.parse("2026-08-01T10:00:00Z")
        assertFalse(DateUtils.isSameMonth(first, august, zone))
    }

    @Test
    fun `invalid iso returns null instant`() {
        assertNull(DateUtils.parseInstantOrNull("not-a-date"))
        assertNotNull(DateUtils.parseInstantOrNull("2026-07-20T10:00:00Z"))
    }

    @Test
    fun `duration minutes between instants`() {
        val start = "2026-07-20T10:00:00Z"
        val end = "2026-07-20T11:15:00Z"
        assertEquals(75L, DateUtils.durationMinutes(start, end))
    }

    @Test
    fun `duration is null without end time`() {
        assertNull(DateUtils.durationMinutes("2026-07-20T10:00:00Z", null))
    }

    @Test
    fun `short date format`() {
        assertEquals("20/07/2026", DateUtils.formatShortDate("2026-07-20T15:00:00Z", ZoneId.of("UTC")))
    }
}
