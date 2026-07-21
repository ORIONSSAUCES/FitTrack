package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity
import com.brunoapp.fittrack.data.database.relation.RoutineWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Transaction
    @Query("SELECT * FROM routine ORDER BY dayOfWeek IS NULL, dayOfWeek, name COLLATE NOCASE")
    fun observeAllWithExercises(): Flow<List<RoutineWithExercises>>

    @Transaction
    @Query("SELECT * FROM routine WHERE id = :id")
    suspend fun getWithExercises(id: Long): RoutineWithExercises?

    @Query("SELECT COUNT(*) FROM routine")
    suspend fun countRoutines(): Int

    @Insert
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Query("DELETE FROM routine WHERE id = :id")
    suspend fun deleteRoutine(id: Long)

    @Insert
    suspend fun insertRoutineExercise(exercise: RoutineExerciseEntity): Long

    @Insert
    suspend fun insertSetTemplates(sets: List<RoutineSetTemplateEntity>)

    @Query("DELETE FROM routine_exercise WHERE routineId = :routineId")
    suspend fun deleteExercisesForRoutine(routineId: Long)

    /**
     * Replaces the full content of a routine atomically.
     * Set templates cascade-delete with their routine_exercise rows.
     */
    @Transaction
    suspend fun saveFullRoutine(
        routine: RoutineEntity,
        exercises: List<Pair<RoutineExerciseEntity, List<RoutineSetTemplateEntity>>>
    ): Long {
        val routineId: Long
        if (routine.id == 0L) {
            routineId = insertRoutine(routine)
        } else {
            updateRoutine(routine)
            routineId = routine.id
            deleteExercisesForRoutine(routineId)
        }
        exercises.forEachIndexed { index, (exercise, sets) ->
            val exerciseRowId = insertRoutineExercise(
                exercise.copy(id = 0, routineId = routineId, position = index)
            )
            insertSetTemplates(
                sets.mapIndexed { setIndex, set ->
                    set.copy(id = 0, routineExerciseId = exerciseRowId, setNumber = setIndex + 1)
                }
            )
        }
        return routineId
    }
}
