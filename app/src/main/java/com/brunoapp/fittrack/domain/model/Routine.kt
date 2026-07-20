package com.brunoapp.fittrack.domain.model

import com.brunoapp.fittrack.core.constants.SetType

data class Routine(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val dayOfWeek: Int? = null,
    val exercises: List<RoutineExercise> = emptyList()
)

data class RoutineExercise(
    val id: Long = 0,
    val exerciseId: Long,
    val exerciseName: String = "",
    val muscleGroupName: String = "",
    val position: Int = 0,
    val restSeconds: Int = 120,
    val notes: String = "",
    val sets: List<SetTemplate> = emptyList()
)

data class SetTemplate(
    val id: Long = 0,
    val setNumber: Int = 1,
    val type: SetType = SetType.NORMAL,
    val repsMin: Int = 8,
    val repsMax: Int = 12,
    val targetWeightKg: Double? = null,
    val targetRir: Int? = null
)
