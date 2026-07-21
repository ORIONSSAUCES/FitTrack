package com.brunoapp.fittrack.domain.model

import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup

data class Exercise(
    val id: Long = 0,
    val name: String,
    val muscleGroup: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: Equipment,
    val instructions: String = "",
    val personalNotes: String = "",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val imagePath: String? = null,
    val effectivenessTier: Int = 3
)
