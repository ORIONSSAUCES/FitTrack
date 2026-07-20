package com.brunoapp.fittrack.core.utils

import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.domain.model.Exercise

/** Pure filtering logic for the exercise library. Unit-tested. */
object ExerciseFilter {

    fun apply(
        exercises: List<Exercise>,
        query: String = "",
        muscleGroup: MuscleGroup? = null,
        equipment: Equipment? = null,
        favoritesOnly: Boolean = false
    ): List<Exercise> {
        val normalizedQuery = query.trim().lowercase()
        return exercises.filter { exercise ->
            val matchesQuery = normalizedQuery.isEmpty() ||
                exercise.name.lowercase().contains(normalizedQuery)
            val matchesMuscle = muscleGroup == null ||
                exercise.muscleGroup == muscleGroup ||
                exercise.secondaryMuscles.contains(muscleGroup)
            val matchesEquipment = equipment == null || exercise.equipment == equipment
            val matchesFavorite = !favoritesOnly || exercise.isFavorite
            matchesQuery && matchesMuscle && matchesEquipment && matchesFavorite
        }
    }
}
