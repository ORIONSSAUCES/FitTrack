package com.brunoapp.fittrack.core.utils

import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.domain.model.Exercise
import org.junit.Assert.assertEquals
import org.junit.Test

class ExerciseFilterTest {

    private val benchPress = Exercise(
        id = 1, name = "Press de banca con barra",
        muscleGroup = MuscleGroup.CHEST,
        secondaryMuscles = listOf(MuscleGroup.TRICEPS),
        equipment = Equipment.BARBELL
    )
    private val squat = Exercise(
        id = 2, name = "Sentadilla con barra",
        muscleGroup = MuscleGroup.QUADS,
        secondaryMuscles = listOf(MuscleGroup.GLUTES),
        equipment = Equipment.BARBELL,
        isFavorite = true
    )
    private val cableFly = Exercise(
        id = 3, name = "Cruce de poleas",
        muscleGroup = MuscleGroup.CHEST,
        equipment = Equipment.CABLE
    )
    private val all = listOf(benchPress, squat, cableFly)

    @Test
    fun `no filters returns everything`() {
        assertEquals(3, ExerciseFilter.apply(all).size)
    }

    @Test
    fun `query matches case-insensitive substring`() {
        val result = ExerciseFilter.apply(all, query = "BANCA")
        assertEquals(listOf(benchPress), result)
    }

    @Test
    fun `muscle filter includes secondary muscles`() {
        val result = ExerciseFilter.apply(all, muscleGroup = MuscleGroup.TRICEPS)
        assertEquals(listOf(benchPress), result)
    }

    @Test
    fun `muscle filter matches primary muscle`() {
        val result = ExerciseFilter.apply(all, muscleGroup = MuscleGroup.CHEST)
        assertEquals(listOf(benchPress, cableFly), result)
    }

    @Test
    fun `equipment filter works`() {
        val result = ExerciseFilter.apply(all, equipment = Equipment.CABLE)
        assertEquals(listOf(cableFly), result)
    }

    @Test
    fun `favorites only filter works`() {
        val result = ExerciseFilter.apply(all, favoritesOnly = true)
        assertEquals(listOf(squat), result)
    }

    @Test
    fun `combined filters are AND`() {
        val result = ExerciseFilter.apply(
            all, query = "barra", muscleGroup = MuscleGroup.CHEST
        )
        assertEquals(listOf(benchPress), result)
    }

    @Test
    fun `no match returns empty list`() {
        val result = ExerciseFilter.apply(all, query = "inexistente")
        assertEquals(0, result.size)
    }
}
