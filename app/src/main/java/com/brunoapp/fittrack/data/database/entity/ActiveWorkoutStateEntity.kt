package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * Single-row table (id = 1) tracking the active workout and its rest timer,
 * so both survive process death.
 */
@Serializable
@Entity(tableName = "active_workout_state")
data class ActiveWorkoutStateEntity(
    @PrimaryKey val id: Int = 1,
    val workoutSessionId: Long,
    val restEndTimeMs: Long? = null,
    val restTotalSeconds: Int? = null,
    val lastUpdated: String = ""
)
