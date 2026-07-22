package com.brunoapp.fittrack.core.utils

/**
 * Double-progression assistant (reps → weight), the system from Bruno's plan:
 *  - Hit the TOP of the rep range last time → increase weight, reset to range bottom.
 *  - Otherwise → same weight, chase one more rep.
 * Unit-tested.
 */
object ProgressionCalc {

    const val DEFAULT_INCREMENT_KG = 2.5

    data class Suggestion(
        val weightKg: Double,
        val reps: Int,
        val isWeightIncrease: Boolean
    )

    /**
     * Suggestion for today's set given the previous performance on the same
     * set number. Returns null when there is no usable history.
     */
    fun suggest(
        previousWeightKg: Double?,
        previousReps: Int?,
        repsMin: Int,
        repsMax: Int,
        incrementKg: Double = DEFAULT_INCREMENT_KG
    ): Suggestion? {
        if (previousWeightKg == null || previousReps == null) return null
        if (previousWeightKg < 0 || previousReps <= 0) return null

        return if (previousReps >= repsMax) {
            Suggestion(
                weightKg = previousWeightKg + incrementKg,
                reps = repsMin,
                isWeightIncrease = true
            )
        } else {
            Suggestion(
                weightKg = previousWeightKg,
                reps = (previousReps + 1).coerceAtMost(repsMax),
                isWeightIncrease = false
            )
        }
    }
}
