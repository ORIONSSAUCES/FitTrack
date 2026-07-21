package com.brunoapp.fittrack.core.utils

import kotlin.math.roundToInt

/** Meal-completion compliance percentages. Unit-tested. */
object ComplianceCalc {

    /** 0–100 percent of completed meals for one day. Null when there are no meals. */
    fun dailyPercent(completed: Int, total: Int): Int? {
        if (total <= 0) return null
        return ((completed.toDouble() / total) * 100).roundToInt().coerceIn(0, 100)
    }

    /** Average of daily percentages, ignoring days without meals. Null if no valid days. */
    fun weeklyPercent(days: List<Pair<Int, Int>>): Int? {
        val percents = days.mapNotNull { (completed, total) -> dailyPercent(completed, total) }
        if (percents.isEmpty()) return null
        return (percents.sum().toDouble() / percents.size).roundToInt()
    }
}
