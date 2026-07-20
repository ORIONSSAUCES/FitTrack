package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExerciseRepository {
    fun observeAll(): Flow<List<Exercise>>
    fun observeById(id: Long): Flow<Exercise?>
    suspend fun getById(id: Long): Exercise?
    suspend fun save(exercise: Exercise): Long
    suspend fun delete(exercise: Exercise)
    suspend fun setFavorite(id: Long, favorite: Boolean)
    suspend fun updateNotes(id: Long, notes: String)
}
