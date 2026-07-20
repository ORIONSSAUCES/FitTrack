package com.brunoapp.fittrack.core.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.Locale

/** Date helpers for history statistics. Pure and unit-tested. */
object DateUtils {

    private val spanishLocale = Locale("es")
    private val weekFields = WeekFields.of(DayOfWeek.MONDAY, 4)

    fun parseInstantOrNull(iso: String): Instant? =
        runCatching { Instant.parse(iso) }.getOrNull()

    /** True when both instants fall in the same ISO week (Monday start). */
    fun isSameWeek(a: Instant, b: Instant, zone: ZoneId = ZoneId.systemDefault()): Boolean {
        val dateA = a.atZone(zone).toLocalDate()
        val dateB = b.atZone(zone).toLocalDate()
        return dateA.get(weekFields.weekBasedYear()) == dateB.get(weekFields.weekBasedYear()) &&
            dateA.get(weekFields.weekOfWeekBasedYear()) == dateB.get(weekFields.weekOfWeekBasedYear())
    }

    /** True when both instants fall in the same calendar month. */
    fun isSameMonth(a: Instant, b: Instant, zone: ZoneId = ZoneId.systemDefault()): Boolean {
        val dateA = a.atZone(zone).toLocalDate()
        val dateB = b.atZone(zone).toLocalDate()
        return dateA.year == dateB.year && dateA.month == dateB.month
    }

    /** "vie 18 jul · 14:32" style formatting for session cards. */
    fun formatSessionDate(iso: String, zone: ZoneId = ZoneId.systemDefault()): String {
        val instant = parseInstantOrNull(iso) ?: return ""
        val formatter = DateTimeFormatter.ofPattern("EEE d MMM · HH:mm", spanishLocale)
        return instant.atZone(zone).format(formatter)
    }

    /** "18/07/2026" short date. */
    fun formatShortDate(iso: String, zone: ZoneId = ZoneId.systemDefault()): String {
        val instant = parseInstantOrNull(iso) ?: return ""
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", spanishLocale)
        return instant.atZone(zone).format(formatter)
    }

    /** Duration in minutes between two ISO instants, or null. */
    fun durationMinutes(startIso: String, endIso: String?): Long? {
        val start = parseInstantOrNull(startIso) ?: return null
        val end = endIso?.let { parseInstantOrNull(it) } ?: return null
        return java.time.Duration.between(start, end).toMinutes()
    }
}
