package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.core.utils.Calculations
import com.brunoapp.fittrack.data.database.dao.ActiveWorkoutStateDao
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.PersonalRecordDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.dao.WorkoutDao
import com.brunoapp.fittrack.data.database.entity.ActiveWorkoutStateEntity
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutExerciseEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSessionEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSetEntity
import com.brunoapp.fittrack.data.database.relation.WorkoutSessionWithExercises
import com.brunoapp.fittrack.domain.model.ExerciseSetHistory
import com.brunoapp.fittrack.domain.model.SetCompletionResult
import com.brunoapp.fittrack.domain.model.WorkoutExercise
import com.brunoapp.fittrack.domain.model.WorkoutSession
import com.brunoapp.fittrack.domain.model.WorkoutSet
import com.brunoapp.fittrack.domain.model.WorkoutSummary
import com.brunoapp.fittrack.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.Instant
import javax.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val routineDao: RoutineDao,
    private val exerciseDao: ExerciseDao,
    private val personalRecordDao: PersonalRecordDao,
    private val stateDao: ActiveWorkoutStateDao
) : WorkoutRepository {

    override fun observeActiveSession(): Flow<WorkoutSession?> =
        combine(
            workoutDao.observeActiveSession(),
            exerciseDao.observeAll()
        ) { relation, exercises ->
            val names = exercises.associate { it.id to it.name }
            relation?.toDomain { id -> names[id].orEmpty() }
        }

    override suspend fun hasActiveSession(): Boolean =
        workoutDao.getActiveSession() != null

    override suspend fun startFromRoutine(routineId: Long): Long? {
        if (hasActiveSession()) return null
        val routine = routineDao.getWithExercises(routineId) ?: return null
        val now = Instant.now().toString()

        val sessionId = workoutDao.insertSession(
            WorkoutSessionEntity(
                routineId = routineId,
                name = routine.routine.name,
                startTime = now,
                isActive = true
            )
        )

        routine.exercises
            .sortedBy { it.routineExercise.position }
            .forEach { relation ->
                val exerciseId = relation.routineExercise.exerciseId
                val previousSets = workoutDao.getPreviousSetsForExercise(exerciseId)
                val previousBySetNumber = previousSets.associateBy { it.setNumber }

                val workoutExerciseId = workoutDao.insertWorkoutExercise(
                    WorkoutExerciseEntity(
                        workoutSessionId = sessionId,
                        exerciseId = exerciseId,
                        position = relation.routineExercise.position,
                        restSeconds = relation.routineExercise.restSeconds,
                        notes = relation.routineExercise.notes
                    )
                )

                relation.sets.sortedBy { it.setNumber }.forEach { template ->
                    val previous = previousBySetNumber[template.setNumber]
                    workoutDao.insertSet(
                        WorkoutSetEntity(
                            workoutExerciseId = workoutExerciseId,
                            setNumber = template.setNumber,
                            setType = template.setType,
                            targetRepsMin = template.repsMin,
                            targetRepsMax = template.repsMax,
                            weightKg = previous?.weightKg ?: template.targetWeightKg,
                            previousWeightKg = previous?.weightKg,
                            previousReps = previous?.reps
                        )
                    )
                }
            }

        stateDao.upsert(
            ActiveWorkoutStateEntity(
                workoutSessionId = sessionId,
                lastUpdated = now
            )
        )
        return sessionId
    }

    override suspend fun completeSet(
        setId: Long,
        weightKg: Double,
        reps: Int,
        rir: Int?
    ): SetCompletionResult {
        val set = workoutDao.getSet(setId) ?: return SetCompletionResult(false)

        // A record only counts on real working sets
        val estimated1rm = Calculations.estimateOneRepMax(weightKg, reps)
        val best = getBestRecordForSet(setId)
        val isRecord = set.setType != SetType.WARMUP.name &&
            weightKg > 0 && reps > 0 &&
            estimated1rm > (best ?: 0.0)

        workoutDao.updateSet(
            set.copy(
                weightKg = weightKg,
                reps = reps,
                rir = rir,
                isCompleted = true,
                isPersonalRecord = isRecord,
                completedAt = Instant.now().toString()
            )
        )
        return SetCompletionResult(isRecord)
    }

    private suspend fun getBestRecordForSet(setId: Long): Double? {
        val session = workoutDao.getActiveSession() ?: return null
        val exerciseId = session.exercises
            .firstOrNull { relation -> relation.sets.any { it.id == setId } }
            ?.workoutExercise?.exerciseId ?: return null
        return personalRecordDao.getBestForExercise(exerciseId)?.estimated1rm
    }

    override suspend fun uncompleteSet(setId: Long) {
        val set = workoutDao.getSet(setId) ?: return
        workoutDao.updateSet(
            set.copy(isCompleted = false, isPersonalRecord = false, completedAt = null)
        )
    }

    override suspend fun addSet(workoutExerciseId: Long) {
        val session = workoutDao.getActiveSession() ?: return
        val exercise = session.exercises
            .firstOrNull { it.workoutExercise.id == workoutExerciseId } ?: return
        val last = exercise.sets.maxByOrNull { it.setNumber }
        workoutDao.insertSet(
            WorkoutSetEntity(
                workoutExerciseId = workoutExerciseId,
                setNumber = (last?.setNumber ?: 0) + 1,
                setType = last?.setType ?: SetType.NORMAL.name,
                targetRepsMin = last?.targetRepsMin ?: 8,
                targetRepsMax = last?.targetRepsMax ?: 12,
                weightKg = last?.weightKg
            )
        )
    }

    override suspend fun removeSet(setId: Long) {
        workoutDao.deleteSet(setId)
    }

    override suspend fun finishSession(): WorkoutSummary? {
        val session = workoutDao.getActiveSession() ?: return null
        val now = Instant.now()
        val completedSets = session.exercises.flatMap { it.sets }.filter { it.isCompleted }

        val volume = completedSets
            .filter { it.setType != SetType.WARMUP.name }
            .sumOf { (it.weightKg ?: 0.0) * (it.reps ?: 0) }

        // Persist personal records flagged during the session
        var recordCount = 0
        session.exercises.forEach { relation ->
            relation.sets
                .filter { it.isCompleted && it.isPersonalRecord }
                .forEach { set ->
                    val weight = set.weightKg ?: return@forEach
                    val reps = set.reps ?: return@forEach
                    personalRecordDao.insert(
                        PersonalRecordEntity(
                            exerciseId = relation.workoutExercise.exerciseId,
                            weightKg = weight,
                            reps = reps,
                            estimated1rm = Calculations.estimateOneRepMax(weight, reps),
                            date = now.toString(),
                            workoutSessionId = session.session.id
                        )
                    )
                    recordCount++
                }
        }

        // Update routine templates with the heaviest completed weight per exercise
        session.session.routineId?.let { routineId ->
            session.exercises.forEach { relation ->
                val maxWeight = relation.sets
                    .filter { it.isCompleted && it.setType != SetType.WARMUP.name }
                    .mapNotNull { it.weightKg }
                    .maxOrNull()
                if (maxWeight != null) {
                    workoutDao.updateRoutineTargetWeight(
                        routineId = routineId,
                        exerciseId = relation.workoutExercise.exerciseId,
                        weightKg = maxWeight
                    )
                }
            }
        }

        workoutDao.updateSession(
            session.session.copy(
                endTime = now.toString(),
                totalVolumeKg = volume,
                isActive = false
            )
        )
        stateDao.clear()

        val duration = runCatching {
            Duration.between(Instant.parse(session.session.startTime), now).toMinutes()
        }.getOrDefault(0)

        return WorkoutSummary(
            durationMinutes = duration,
            exercisesDone = session.exercises.count { relation ->
                relation.sets.any { it.isCompleted }
            },
            setsCompleted = completedSets.size,
            totalVolumeKg = volume,
            newRecords = recordCount
        )
    }

    override suspend fun discardSession() {
        workoutDao.getActiveSession()?.let { workoutDao.deleteSession(it.session.id) }
        stateDao.clear()
    }

    override suspend fun saveRestTimer(endTimeMs: Long?, totalSeconds: Int?) {
        val state = stateDao.getState() ?: return
        stateDao.upsert(
            state.copy(
                restEndTimeMs = endTimeMs,
                restTotalSeconds = totalSeconds,
                lastUpdated = Instant.now().toString()
            )
        )
    }

    override suspend fun getRestTimer(): Pair<Long, Int>? {
        val state = stateDao.getState() ?: return null
        val end = state.restEndTimeMs ?: return null
        val total = state.restTotalSeconds ?: return null
        return if (end > System.currentTimeMillis()) end to total else null
    }

    // ── History ──

    override fun observeFinishedSessions(): Flow<List<WorkoutSession>> =
        combine(
            workoutDao.observeFinishedSessions(),
            exerciseDao.observeAll()
        ) { sessions, exercises ->
            val names = exercises.associate { it.id to it.name }
            sessions.map { relation -> relation.toDomain { id -> names[id].orEmpty() } }
        }

    override fun observeSession(id: Long): Flow<WorkoutSession?> =
        combine(
            workoutDao.observeSession(id),
            exerciseDao.observeAll()
        ) { relation, exercises ->
            val names = exercises.associate { it.id to it.name }
            relation?.toDomain { exerciseId -> names[exerciseId].orEmpty() }
        }

    override suspend fun deleteSession(id: Long) = workoutDao.deleteSession(id)

    override suspend fun updateSessionNotes(id: Long, notes: String) =
        workoutDao.updateSessionNotes(id, notes)

    override fun observeExerciseHistory(exerciseId: Long): Flow<List<ExerciseSetHistory>> =
        workoutDao.observeExerciseHistory(exerciseId).map { rows ->
            rows.mapNotNull { row ->
                val weight = row.set.weightKg ?: return@mapNotNull null
                val reps = row.set.reps ?: return@mapNotNull null
                ExerciseSetHistory(
                    sessionId = row.sessionId,
                    date = row.sessionDate,
                    setNumber = row.set.setNumber,
                    weightKg = weight,
                    reps = reps,
                    rir = row.set.rir,
                    estimated1rm = Calculations.estimateOneRepMax(weight, reps),
                    isPersonalRecord = row.set.isPersonalRecord
                )
            }
        }

    private inline fun WorkoutSessionWithExercises.toDomain(
        exerciseName: (Long) -> String
    ) = WorkoutSession(
        id = session.id,
        routineId = session.routineId,
        name = session.name,
        startTime = session.startTime,
        endTime = session.endTime,
        totalVolumeKg = session.totalVolumeKg,
        notes = session.notes,
        isActive = session.isActive,
        exercises = exercises
            .sortedBy { it.workoutExercise.position }
            .map { relation ->
                WorkoutExercise(
                    id = relation.workoutExercise.id,
                    exerciseId = relation.workoutExercise.exerciseId,
                    exerciseName = exerciseName(relation.workoutExercise.exerciseId),
                    position = relation.workoutExercise.position,
                    restSeconds = relation.workoutExercise.restSeconds,
                    notes = relation.workoutExercise.notes,
                    sets = relation.sets
                        .sortedBy { it.setNumber }
                        .map { set ->
                            WorkoutSet(
                                id = set.id,
                                setNumber = set.setNumber,
                                type = runCatching { SetType.valueOf(set.setType) }
                                    .getOrDefault(SetType.NORMAL),
                                targetRepsMin = set.targetRepsMin,
                                targetRepsMax = set.targetRepsMax,
                                weightKg = set.weightKg,
                                reps = set.reps,
                                rir = set.rir,
                                isCompleted = set.isCompleted,
                                isPersonalRecord = set.isPersonalRecord,
                                previousWeightKg = set.previousWeightKg,
                                previousReps = set.previousReps
                            )
                        }
                )
            }
    )
}
