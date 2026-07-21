package com.brunoapp.fittrack.domain.model

data class WeightEntry(
    val id: Long = 0,
    val date: String,              // YYYY-MM-DD
    val time: String = "",
    val weightKg: Double,
    val notes: String = ""
)

data class BodyMeasurement(
    val id: Long = 0,
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

data class ProgressPhoto(
    val id: Long = 0,
    val date: String,
    val weightKg: Double? = null,
    val frontPhotoPath: String? = null,
    val sidePhotoPath: String? = null,
    val backPhotoPath: String? = null,
    val notes: String = ""
)
