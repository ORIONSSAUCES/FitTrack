package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.brunoapp.fittrack.data.database.entity.WorkoutExerciseEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSessionEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSetEntity
import com.brunoapp.fittrack.data.database.relation.ExerciseSetHistoryRow
import com.brunoapp.fittrack.data.database.relation.WorkoutSessionWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // ── Sessions ──

    @Transaction
    @Query("SELECT * FROM workout_session WHERE isActive = 1 LIMIT 1")
    fun observeActiveSession(): Flow<WorkoutSessionWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_session WHERE isActive = 1 LIMIT 1")
    suspend fun getActiveSession(): WorkoutSessionWithExercises?

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    suspend fun getSession(id: Long): WorkoutSessionWithExercises?

    @Transaction
    @Query("SELECT * FROM workout_session WHERE id = :id")
    fun observeSession(id: Long): Flow<WorkoutSessionWithExercises?>

    @Transaction
    @Query("SELECT * FROM workout_session WHERE isActive = 0 ORDER BY startTime DESC")
    fun observeFinishedSessions(): Flow<List<WorkoutSessionWithExercises>>

    @Query("UPDATE workout_session SET notes = :notes WHERE id = :id")
    suspend fun updateSessionNotes(id: Long, notes: String)

    /** Completed sets for one exercise across all finished sessions, oldest first. */
    @Query(
        """
        SELECT ws.*, s.startTime AS sessionDate, s.id AS sessionId
        FROM workout_set ws
        JOIN workout_exercise we ON ws.workoutExerciseId = we.id
        JOIN workout_session s ON we.workoutSessionId = s.id
        WHERE we.exerciseId = :exerciseId
          AND s.isActive = 0
          AND ws.isCompleted = 1
        ORDER BY s.startTime ASC, ws.setNumber ASC
        """
    )
    fun observeExerciseHistory(exerciseId: Long): Flow<List<ExerciseSetHistoryRow>>

    @Insert
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("DELETE FROM workout_session WHERE id = :id")
    suspend fun deleteSession(id: Long)

    // ── Exercises ──

    @Insert
    suspend fun insertWorkoutExercise(exercise: WorkoutExerciseEntity): Long

    // ── Sets ──

    @Insert
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Update
    suspend fun updateSet(set: WorkoutSetEntity)

    @Query("DELETE FROM workout_set WHERE id = :id")
    suspend fun deleteSet(id: Long)

    @Query("SELECT * FROM workout_set WHERE id = :id")
    suspend fun getSet(id: Long): WorkoutSetEntity?

    /**
     * Completed sets of the most recent FINISHED session that included
     * the given exercise, ordered by set number. Used to show
     * "previous workout" values.
     */
    @Query(
        """
        SELECT ws.* FROM workout_set ws
        JOIN workout_exercise we ON ws.workoutExerciseId = we.id
        JOIN workout_session s ON we.workoutSessionId = s.id
        WHERE we.exerciseId = :exerciseId
          AND s.isActive = 0
          AND ws.isCompleted = 1
          AND s.id = (
              SELECT s2.id FROM workout_session s2
              JOIN workout_exercise we2 ON we2.workoutSessionId = s2.id
              WHERE we2.exerciseId = :exerciseId AND s2.isActive = 0
              ORDER BY s2.startTime DESC LIMIT 1
          )
        ORDER BY ws.setNumber
        """
    )
    suspend fun getPreviousSetsForExercise(exerciseId: Long): List<WorkoutSetEntity>

    /** Updates stored target weight on routine templates after finishing a workout. */
    @Query(
        """
        UPDATE routine_set_template SET targetWeightKg = :weightKg
        WHERE routineExerciseId IN (
            SELECT id FROM routine_exercise
            WHERE routineId = :routineId AND exerciseId = :exerciseId
        )
        """
    )
    suspend fun updateRoutineTargetWeight(routineId: Long, exerciseId: Long, weightKg: Double)
}
