package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {

    @Query("SELECT * FROM personal_record WHERE exerciseId = :exerciseId ORDER BY date DESC")
    fun observeForExercise(exerciseId: Long): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_record WHERE exerciseId = :exerciseId ORDER BY estimated1rm DESC LIMIT 1")
    suspend fun getBestForExercise(exerciseId: Long): PersonalRecordEntity?

    @Insert
    suspend fun insert(record: PersonalRecordEntity): Long
}
