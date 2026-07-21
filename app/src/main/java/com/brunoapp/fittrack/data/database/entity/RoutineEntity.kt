package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "routine")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val dayOfWeek: Int? = null,        // 0 = Monday … 6 = Sunday, null = unassigned
    val createdAt: String = "",
    val updatedAt: String = ""
)
