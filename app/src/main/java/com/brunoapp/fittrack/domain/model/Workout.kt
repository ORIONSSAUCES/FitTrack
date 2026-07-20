package com.brunoapp.fittrack.domain.model

import com.brunoapp.fittrack.core.constants.SetType

data class WorkoutSession(
    val id: Long = 0,
    val routineId: Long? = null,
    val name: String,
    val startTime: String,
    val endTime: String? = null,
    val totalVolumeKg: Double = 0.0,
    val notes: String = "",
    val isActive: Boolean = false,
    val exercises: List<WorkoutExercise> = emptyList()
)

data class WorkoutExercise(
    val id: Long = 0,
    val exerciseId: Long,
    val exerciseName: String = "",
    val position: Int = 0,
    val restSeconds: Int = 120,
    val notes: String = "",
    val sets: List<WorkoutSet> = emptyList()
)

data class WorkoutSet(
    val id: Long = 0,
    val setNumber: Int = 1,
    val type: SetType = SetType.NORMAL,
    val targetRepsMin: Int = 8,
    val targetRepsMax: Int = 12,
    val weightKg: Double? = null,
    val reps: Int? = null,
    val rir: Int? = null,
    val isCompleted: Boolean = false,
    val isPersonalRecord: Boolean = false,
    val previousWeightKg: Double? = null,
    val previousReps: Int? = null
)

/** Result of completing a set: whether it is a new estimated-1RM record. */
data class SetCompletionResult(val isNewRecord: Boolean)

/** Summary shown when a workout is finished. */
data class WorkoutSummary(
    val durationMinutes: Long,
    val exercisesDone: Int,
    val setsCompleted: Int,
    val totalVolumeKg: Double,
    val newRecords: Int
)

/** One completed set in an exercise's history, with its session date. */
data class ExerciseSetHistory(
    val sessionId: Long,
    val date: String,          // ISO instant
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rir: Int?,
    val estimated1rm: Double,
    val isPersonalRecord: Boolean
)
