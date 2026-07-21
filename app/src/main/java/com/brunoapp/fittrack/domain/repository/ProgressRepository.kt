package com.brunoapp.fittrack.domain.repository

import android.net.Uri
import com.brunoapp.fittrack.domain.model.BodyMeasurement
import com.brunoapp.fittrack.domain.model.ProgressPhoto
import com.brunoapp.fittrack.domain.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun observeWeights(): Flow<List<WeightEntry>>
    suspend fun saveWeight(entry: WeightEntry)
    suspend fun deleteWeight(id: Long)

    fun observeMeasurements(): Flow<List<BodyMeasurement>>
    suspend fun saveMeasurement(entry: BodyMeasurement)
    suspend fun deleteMeasurement(id: Long)

    fun observePhotos(): Flow<List<ProgressPhoto>>

    /** Copies the picked images into private storage and inserts the entry. */
    suspend fun savePhotoEntry(
        date: String,
        weightKg: Double?,
        frontUri: Uri?,
        sideUri: Uri?,
        backUri: Uri?,
        notes: String
    )

    /** Deletes the entry and its image files. */
    suspend fun deletePhotoEntry(id: Long)
}
