package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutExerciseId")]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutExerciseId: Long,
    val setNumber: Int,
    val setType: String = "NORMAL",
    val targetRepsMin: Int = 8,
    val targetRepsMax: Int = 12,
    val weightKg: Double? = null,
    val reps: Int? = null,
    val rir: Int? = null,
    val isCompleted: Boolean = false,
    val isPersonalRecord: Boolean = false,
    val previousWeightKg: Double? = null,  // from last finished session, for display
    val previousReps: Int? = null,
    val completedAt: String? = null
)
