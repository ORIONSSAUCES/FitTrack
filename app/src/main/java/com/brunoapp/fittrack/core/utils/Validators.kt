package com.brunoapp.fittrack.core.utils

/**
 * Input parsing and validation for user-entered values.
 * All parsers accept both comma and dot as decimal separator.
 */
object Validators {

    private fun parseDecimal(input: String): Double? =
        input.trim().replace(',', '.').toDoubleOrNull()

    /** Height must be 100–250 cm. Empty input is valid (null). */
    fun parseHeight(input: String): Result<Double?> {
        if (input.isBlank()) return Result.success(null)
        val value = parseDecimal(input) ?: return Result.failure(NumberFormatException())
        return if (value in 100.0..250.0) Result.success(value)
        else Result.failure(IllegalArgumentException())
    }

    /** Body weight must be 30–400 kg. Empty input is valid (null). */
    fun parseBodyWeight(input: String): Result<Double?> {
        if (input.isBlank()) return Result.success(null)
        val value = parseDecimal(input) ?: return Result.failure(NumberFormatException())
        return if (value in 30.0..400.0) Result.success(value)
        else Result.failure(IllegalArgumentException())
    }

    /** Lifting weight for a set: 0–1000 kg (0 allowed for bodyweight exercises). */
    fun parseLiftWeight(input: String): Result<Double?> {
        if (input.isBlank()) return Result.success(null)
        val value = parseDecimal(input) ?: return Result.failure(NumberFormatException())
        return if (value in 0.0..1000.0) Result.success(value)
        else Result.failure(IllegalArgumentException())
    }

    /** Reps: 1–200. */
    fun parseReps(input: String): Result<Int?> {
        if (input.isBlank()) return Result.success(null)
        val value = input.trim().toIntOrNull() ?: return Result.failure(NumberFormatException())
        return if (value in 1..200) Result.success(value)
        else Result.failure(IllegalArgumentException())
    }
}
