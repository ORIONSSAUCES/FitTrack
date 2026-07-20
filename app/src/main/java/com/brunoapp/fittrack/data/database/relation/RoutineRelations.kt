package com.brunoapp.fittrack.data.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity

data class RoutineExerciseWithSets(
    @Embedded val routineExercise: RoutineExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "routineExerciseId"
    )
    val sets: List<RoutineSetTemplateEntity>
)

data class RoutineWithExercises(
    @Embedded val routine: RoutineEntity,
    @Relation(
        entity = RoutineExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "routineId"
    )
    val exercises: List<RoutineExerciseWithSets>
)
