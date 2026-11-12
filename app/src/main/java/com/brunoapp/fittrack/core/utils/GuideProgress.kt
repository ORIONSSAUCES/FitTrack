package com.brunoapp.fittrack.core.utils

import java.time.LocalDate

/** Weight-progress math for the nutrition guide. Unit-tested. */
object GuideProgress {

    enum class Pace { NO_DATA, TOO_SLOW, IDEAL, TOO_FAST, GAINING }

    /** 0..1 progress from start to target (clamped). */
    fun fraction(start: Double, current: Double, target: Double): Float {
        if (start <= target) return 1f
        return ((start - current) / (start - target)).toFloat().coerceIn(0f, 1f)
    }

    /**
     * Weekly loss (kg/week, positive = losing) comparing the average of the
     * last 7 days with the average of the 7 days before. Null without data in both windows.
     */
    fun weeklyLoss(entries: List<Pair<LocalDate, Double>>, today: LocalDate): Double? {
        val recent = entries.filter { it.first > today.minusDays(7) && it.first <= today }
        val previous = entries.filter { it.first > today.minusDays(14) && it.first <= today.minusDays(7) }
        if (recent.isEmpty() || previous.isEmpty()) return null
        return previous.map { it.second }.average() - recent.map { it.second }.average()
    }

    /** Ideal range from the guide: 0.6–1.1 kg/week (1.3 = upper alarm). */
    fun pace(weeklyLoss: Double?): Pace = when {
        weeklyLoss == null -> Pace.NO_DATA
        weeklyLoss < 0 -> Pace.GAINING
        weeklyLoss < 0.4 -> Pace.TOO_SLOW
        weeklyLoss > 1.3 -> Pace.TOO_FAST
        else -> Pace.IDEAL
    }

    /** Estimated weeks left at the current pace; null when not losing. */
    fun weeksLeft(current: Double, target: Double, weeklyLoss: Double?): Int? {
        if (weeklyLoss == null || weeklyLoss <= 0.1) return null
        val remaining = current - target
        if (remaining <= 0) return 0
        return kotlin.math.ceil(remaining / weeklyLoss).toInt()
    }
}
