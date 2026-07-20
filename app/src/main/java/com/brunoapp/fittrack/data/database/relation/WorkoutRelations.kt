package com.brunoapp.fittrack.data.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.brunoapp.fittrack.data.database.entity.WorkoutExerciseEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSessionEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSetEntity

data class WorkoutExerciseWithSets(
    @Embedded val workoutExercise: WorkoutExerciseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<WorkoutSetEntity>
)

data class WorkoutSessionWithExercises(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        entity = WorkoutExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "workoutSessionId"
    )
    val exercises: List<WorkoutExerciseWithSets>
)

/** Row for exercise history queries: a completed set plus its session date. */
data class ExerciseSetHistoryRow(
    @Embedded val set: WorkoutSetEntity,
    val sessionDate: String,
    val sessionId: Long
)
