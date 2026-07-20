package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.ExerciseSetHistory
import com.brunoapp.fittrack.domain.model.SetCompletionResult
import com.brunoapp.fittrack.domain.model.WorkoutSession
import com.brunoapp.fittrack.domain.model.WorkoutSummary
import kotlinx.coroutines.flow.Flow

interface WorkoutRepository {
    fun observeActiveSession(): Flow<WorkoutSession?>
    suspend fun hasActiveSession(): Boolean
    suspend fun startFromRoutine(routineId: Long): Long?
    suspend fun completeSet(setId: Long, weightKg: Double, reps: Int, rir: Int?): SetCompletionResult
    suspend fun uncompleteSet(setId: Long)
    suspend fun addSet(workoutExerciseId: Long)
    suspend fun removeSet(setId: Long)
    suspend fun finishSession(): WorkoutSummary?
    suspend fun discardSession()
    suspend fun saveRestTimer(endTimeMs: Long?, totalSeconds: Int?)
    suspend fun getRestTimer(): Pair<Long, Int>?

    // ── History ──
    fun observeFinishedSessions(): Flow<List<WorkoutSession>>
    fun observeSession(id: Long): Flow<WorkoutSession?>
    suspend fun deleteSession(id: Long)
    suspend fun updateSessionNotes(id: Long, notes: String)
    fun observeExerciseHistory(exerciseId: Long): Flow<List<ExerciseSetHistory>>
}
