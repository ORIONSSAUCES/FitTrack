package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.brunoapp.fittrack.data.database.entity.BodyMeasurementEntity
import com.brunoapp.fittrack.data.database.entity.ProgressPhotoEntity
import com.brunoapp.fittrack.data.database.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {

    // ── Weight ──

    @Query("SELECT * FROM weight_entry ORDER BY date DESC, time DESC")
    fun observeWeights(): Flow<List<WeightEntryEntity>>

    @Upsert
    suspend fun upsertWeight(entry: WeightEntryEntity)

    @Query("DELETE FROM weight_entry WHERE id = :id")
    suspend fun deleteWeight(id: Long)

    // ── Measurements ──

    @Query("SELECT * FROM body_measurement ORDER BY date DESC")
    fun observeMeasurements(): Flow<List<BodyMeasurementEntity>>

    @Upsert
    suspend fun upsertMeasurement(entry: BodyMeasurementEntity)

    @Query("DELETE FROM body_measurement WHERE id = :id")
    suspend fun deleteMeasurement(id: Long)

    // ── Photos ──

    @Query("SELECT * FROM progress_photo ORDER BY date DESC")
    fun observePhotos(): Flow<List<ProgressPhotoEntity>>

    @Insert
    suspend fun insertPhoto(entry: ProgressPhotoEntity): Long

    @Query("SELECT * FROM progress_photo WHERE id = :id")
    suspend fun getPhoto(id: Long): ProgressPhotoEntity?

    @Query("DELETE FROM progress_photo WHERE id = :id")
    suspend fun deletePhoto(id: Long)
}
