package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercise ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercise WHERE id = :id")
    fun observeById(id: Long): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercise WHERE id = :id")
    suspend fun getById(id: Long): ExerciseEntity?

    @Query("SELECT COUNT(*) FROM exercise")
    suspend fun count(): Int

    @Query("SELECT name FROM exercise")
    suspend fun getAllNamesOnce(): List<String>

    @Upsert
    suspend fun upsert(exercise: ExerciseEntity): Long

    @Upsert
    suspend fun upsertAll(exercises: List<ExerciseEntity>)

    @Delete
    suspend fun delete(exercise: ExerciseEntity)

    @Query("UPDATE exercise SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE exercise SET personalNotes = :notes WHERE id = :id")
    suspend fun updateNotes(id: Long, notes: String)

    @Query("UPDATE exercise SET imagePath = :imagePath WHERE name = :name AND imagePath IS NULL")
    suspend fun setImageByName(name: String, imagePath: String)

    @Query("SELECT COUNT(*) FROM exercise WHERE imagePath IS NOT NULL AND isCustom = 0")
    suspend fun countWithImages(): Int
}
