package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "weight_entry", indices = [Index("date")])
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,              // YYYY-MM-DD
    val time: String = "",         // HH:mm
    val weightKg: Double,
    val notes: String = ""
)

@Entity(tableName = "body_measurement", indices = [Index("date")])
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val waistCm: Double? = null,
    val abdomenCm: Double? = null,
    val chestCm: Double? = null,
    val hipsCm: Double? = null,
    val neckCm: Double? = null,
    val leftArmCm: Double? = null,
    val rightArmCm: Double? = null,
    val leftThighCm: Double? = null,
    val rightThighCm: Double? = null,
    val bodyFatPct: Double? = null,
    val notes: String = ""
)

@Entity(tableName = "progress_photo", indices = [Index("date")])
data class ProgressPhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val weightKg: Double? = null,
    val frontPhotoPath: String? = null,   // absolute path in app-private storage
    val sidePhotoPath: String? = null,
    val backPhotoPath: String? = null,
    val notes: String = ""
)
