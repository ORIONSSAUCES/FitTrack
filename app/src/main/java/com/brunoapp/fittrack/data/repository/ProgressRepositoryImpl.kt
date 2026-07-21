package com.brunoapp.fittrack.data.repository

import android.content.Context
import android.net.Uri
import com.brunoapp.fittrack.data.database.dao.ProgressDao
import com.brunoapp.fittrack.data.database.entity.BodyMeasurementEntity
import com.brunoapp.fittrack.data.database.entity.ProgressPhotoEntity
import com.brunoapp.fittrack.data.database.entity.WeightEntryEntity
import com.brunoapp.fittrack.domain.model.BodyMeasurement
import com.brunoapp.fittrack.domain.model.ProgressPhoto
import com.brunoapp.fittrack.domain.model.WeightEntry
import com.brunoapp.fittrack.domain.repository.ProgressRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class ProgressRepositoryImpl @Inject constructor(
    private val dao: ProgressDao,
    @ApplicationContext private val context: Context
) : ProgressRepository {

    // ── Weight ──

    override fun observeWeights(): Flow<List<WeightEntry>> =
        dao.observeWeights().map { list ->
            list.map {
                WeightEntry(it.id, it.date, it.time, it.weightKg, it.notes)
            }
        }

    override suspend fun saveWeight(entry: WeightEntry) {
        dao.upsertWeight(
            WeightEntryEntity(
                id = entry.id,
                date = entry.date,
                time = entry.time,
                weightKg = entry.weightKg,
                notes = entry.notes
            )
        )
    }

    override suspend fun deleteWeight(id: Long) = dao.deleteWeight(id)

    // ── Measurements ──

    override fun observeMeasurements(): Flow<List<BodyMeasurement>> =
        dao.observeMeasurements().map { list ->
            list.map {
                BodyMeasurement(
                    it.id, it.date, it.waistCm, it.abdomenCm, it.chestCm, it.hipsCm,
                    it.neckCm, it.leftArmCm, it.rightArmCm, it.leftThighCm,
                    it.rightThighCm, it.bodyFatPct, it.notes
                )
            }
        }

    override suspend fun saveMeasurement(entry: BodyMeasurement) {
        dao.upsertMeasurement(
            BodyMeasurementEntity(
                id = entry.id,
                date = entry.date,
                waistCm = entry.waistCm,
                abdomenCm = entry.abdomenCm,
                chestCm = entry.chestCm,
                hipsCm = entry.hipsCm,
                neckCm = entry.neckCm,
                leftArmCm = entry.leftArmCm,
                rightArmCm = entry.rightArmCm,
                leftThighCm = entry.leftThighCm,
                rightThighCm = entry.rightThighCm,
                bodyFatPct = entry.bodyFatPct,
                notes = entry.notes
            )
        )
    }

    override suspend fun deleteMeasurement(id: Long) = dao.deleteMeasurement(id)

    // ── Photos ──

    override fun observePhotos(): Flow<List<ProgressPhoto>> =
        dao.observePhotos().map { list ->
            list.map {
                ProgressPhoto(
                    it.id, it.date, it.weightKg,
                    it.frontPhotoPath, it.sidePhotoPath, it.backPhotoPath, it.notes
                )
            }
        }

    override suspend fun savePhotoEntry(
        date: String,
        weightKg: Double?,
        frontUri: Uri?,
        sideUri: Uri?,
        backUri: Uri?,
        notes: String
    ) = withContext(Dispatchers.IO) {
        val timestamp = System.currentTimeMillis()
        dao.insertPhoto(
            ProgressPhotoEntity(
                date = date,
                weightKg = weightKg,
                frontPhotoPath = frontUri?.let { copyToPrivate(it, "${timestamp}_front.jpg") },
                sidePhotoPath = sideUri?.let { copyToPrivate(it, "${timestamp}_side.jpg") },
                backPhotoPath = backUri?.let { copyToPrivate(it, "${timestamp}_back.jpg") },
                notes = notes
            )
        )
        Unit
    }

    override suspend fun deletePhotoEntry(id: Long) = withContext(Dispatchers.IO) {
        dao.getPhoto(id)?.let { photo ->
            listOfNotNull(photo.frontPhotoPath, photo.sidePhotoPath, photo.backPhotoPath)
                .forEach { path -> runCatching { File(path).delete() } }
        }
        dao.deletePhoto(id)
    }

    /** Copies a content Uri into app-private storage; photos never leave the device. */
    private fun copyToPrivate(uri: Uri, fileName: String): String? =
        runCatching {
            val dir = File(context.filesDir, "progress_photos").apply { mkdirs() }
            val dest = File(dir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                dest.outputStream().use { output -> input.copyTo(output) }
            }
            dest.absolutePath
        }.getOrNull()
}
