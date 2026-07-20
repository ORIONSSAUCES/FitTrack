package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.brunoapp.fittrack.data.database.entity.ActiveWorkoutStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActiveWorkoutStateDao {

    @Query("SELECT * FROM active_workout_state WHERE id = 1")
    fun observeState(): Flow<ActiveWorkoutStateEntity?>

    @Query("SELECT * FROM active_workout_state WHERE id = 1")
    suspend fun getState(): ActiveWorkoutStateEntity?

    @Upsert
    suspend fun upsert(state: ActiveWorkoutStateEntity)

    @Query("DELETE FROM active_workout_state")
    suspend fun clear()
}
