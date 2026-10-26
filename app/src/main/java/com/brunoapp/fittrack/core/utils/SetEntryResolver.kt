package com.brunoapp.fittrack.core.utils

/**
 * Resolves the values to save when the user confirms a set.
 * A blank field falls back to its visible placeholder (previous set weight /
 * suggested reps), so a set can be confirmed with a single tap.
 * Unit-tested.
 */
object SetEntryResolver {

    data class Resolved(val weightKg: Double?, val reps: Int?) {
        val isValid: Boolean get() = weightKg != null && reps != null
    }

    fun resolve(
        weightText: String,
        repsText: String,
        fallbackWeightKg: Double?,
        fallbackReps: Int?
    ): Resolved {
        val weight = if (weightText.isBlank()) fallbackWeightKg
        else Validators.parseLiftWeight(weightText).getOrNull()
        val reps = if (repsText.isBlank()) fallbackReps
        else Validators.parseReps(repsText).getOrNull()
        return Resolved(weight, reps)
    }

    /** True when today's set beats the previous one on estimated 1RM (Epley). */
    fun isImprovement(
        weightKg: Double?,
        reps: Int?,
        previousWeightKg: Double?,
        previousReps: Int?
    ): Boolean {
        if (weightKg == null || reps == null || previousWeightKg == null || previousReps == null) {
            return false
        }
        return Calculations.estimateOneRepMax(weightKg, reps) >
            Calculations.estimateOneRepMax(previousWeightKg, previousReps)
    }
}
