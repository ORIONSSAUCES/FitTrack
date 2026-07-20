package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_session",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("routineId"), Index("isActive")]
)
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val routineId: Long? = null,
    val name: String,
    val startTime: String,             // ISO 8601 instant
    val endTime: String? = null,
    val totalVolumeKg: Double = 0.0,
    val notes: String = "",
    val isActive: Boolean = false
)
