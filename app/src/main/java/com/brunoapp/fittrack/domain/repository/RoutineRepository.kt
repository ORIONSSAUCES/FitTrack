package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.Routine
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {
    fun observeAll(): Flow<List<Routine>>
    suspend fun getById(id: Long): Routine?
    suspend fun save(routine: Routine): Long
    suspend fun delete(id: Long)
    suspend fun duplicate(id: Long): Long?
}
