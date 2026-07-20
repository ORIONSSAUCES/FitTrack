package com.brunoapp.fittrack.core.utils

import kotlin.math.roundToInt

/**
 * Pure domain calculations used across the app.
 * All formulas documented with their source.
 */
object Calculations {

    /**
     * Estimated one-rep max using the Epley formula:
     * 1RM = weight * (1 + reps / 30)
     *
     * For reps == 1 the weight itself is returned.
     * Returns 0.0 for invalid input.
     */
    fun estimateOneRepMax(weightKg: Double, reps: Int): Double {
        if (weightKg <= 0.0 || reps <= 0) return 0.0
        if (reps == 1) return weightKg
        return weightKg * (1 + reps / 30.0)
    }

    /** Total volume for a set: weight x reps. Warmup sets are excluded by callers. */
    fun setVolume(weightKg: Double, reps: Int): Double {
        if (weightKg <= 0.0 || reps <= 0) return 0.0
        return weightKg * reps
    }

    /** Calories from macros: protein 4 kcal/g, carbs 4 kcal/g, fat 9 kcal/g. */
    fun caloriesFromMacros(proteinG: Double, carbsG: Double, fatG: Double): Int {
        val total = proteinG * 4 + carbsG * 4 + fatG * 9
        return total.coerceAtLeast(0.0).roundToInt()
    }

    /** Percentage (0-100) of progress toward a goal, clamped. */
    fun percentageOfGoal(current: Double, goal: Double): Int {
        if (goal <= 0.0) return 0
        return ((current / goal) * 100).roundToInt().coerceIn(0, 100)
    }

    /** Weekly average from a list of weight entries. Returns null when empty. */
    fun weeklyAverage(weights: List<Double>): Double? {
        if (weights.isEmpty()) return null
        return weights.average()
    }
}
