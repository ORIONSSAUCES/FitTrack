package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Single-row table holding the user profile.
 * Always stored with id = 1.
 */
@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "",
    val birthDate: String? = null,          // ISO 8601 date
    val heightCm: Double? = null,
    val weightInitialKg: Double? = null,
    val weightGoalKg: Double? = null,
    val objective: String = "MAINTAIN",     // Objective enum name
    val defaultRestSeconds: Int = 120,
    val weeklyCheckDay: Int = 0,            // 0 = Monday
    val createdAt: String = ""
)
