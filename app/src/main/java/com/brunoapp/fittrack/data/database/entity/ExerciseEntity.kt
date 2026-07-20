package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val muscleGroup: String,               // MuscleGroup enum name
    val secondaryMuscles: String = "",     // comma-separated MuscleGroup names
    val equipment: String,                 // Equipment enum name
    val instructions: String = "",
    val personalNotes: String = "",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val createdAt: String = ""
)
