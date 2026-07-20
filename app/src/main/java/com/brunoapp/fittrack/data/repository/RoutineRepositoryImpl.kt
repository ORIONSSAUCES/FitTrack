package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.core.constants.SetType
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity
import com.brunoapp.fittrack.data.database.relation.RoutineWithExercises
import com.brunoapp.fittrack.domain.model.Routine
import com.brunoapp.fittrack.domain.model.RoutineExercise
import com.brunoapp.fittrack.domain.model.SetTemplate
import com.brunoapp.fittrack.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: ExerciseDao
) : RoutineRepository {

    override fun observeAll(): Flow<List<Routine>> =
        combine(
            routineDao.observeAllWithExercises(),
            exerciseDao.observeAll()
        ) { routines, exercises ->
            val exerciseById = exercises.associateBy { it.id }
            routines.map { it.toDomain { id -> exerciseById[id]?.name.orEmpty() to
                (exerciseById[id]?.muscleGroup ?: "") } }
        }

    override suspend fun getById(id: Long): Routine? {
        val relation = routineDao.getWithExercises(id) ?: return null
        return relation.toDomain { exerciseId ->
            val exercise = exerciseDao.getById(exerciseId)
            exercise?.name.orEmpty() to (exercise?.muscleGroup ?: "")
        }
    }

    override suspend fun save(routine: Routine): Long {
        val now = Instant.now().toString()
        val existing = if (routine.id != 0L) routineDao.getWithExercises(routine.id) else null
        return routineDao.saveFullRoutine(
            routine = RoutineEntity(
                id = routine.id,
                name = routine.name,
                description = routine.description,
                dayOfWeek = routine.dayOfWeek,
                createdAt = existing?.routine?.createdAt ?: now,
                updatedAt = now
            ),
            exercises = routine.exercises.map { exercise ->
                RoutineExerciseEntity(
                    routineId = routine.id,
                    exerciseId = exercise.exerciseId,
                    position = exercise.position,
                    restSeconds = exercise.restSeconds,
                    notes = exercise.notes
                ) to exercise.sets.map { set ->
                    RoutineSetTemplateEntity(
                        routineExerciseId = 0,
                        setNumber = set.setNumber,
                        setType = set.type.name,
                        repsMin = set.repsMin,
                        repsMax = set.repsMax,
                        targetWeightKg = set.targetWeightKg,
                        targetRir = set.targetRir
                    )
                }
            }
        )
    }

    override suspend fun delete(id: Long) = routineDao.deleteRoutine(id)

    override suspend fun duplicate(id: Long): Long? {
        val original = getById(id) ?: return null
        return save(
            original.copy(
                id = 0,
                name = "${original.name} (copia)",
                dayOfWeek = null
            )
        )
    }

    private inline fun RoutineWithExercises.toDomain(
        exerciseInfo: (Long) -> Pair<String, String>
    ) = Routine(
        id = routine.id,
        name = routine.name,
        description = routine.description,
        dayOfWeek = routine.dayOfWeek,
        exercises = exercises
            .sortedBy { it.routineExercise.position }
            .map { relation ->
                val (name, muscleGroupName) = exerciseInfo(relation.routineExercise.exerciseId)
                RoutineExercise(
                    id = relation.routineExercise.id,
                    exerciseId = relation.routineExercise.exerciseId,
                    exerciseName = name,
                    muscleGroupName = runCatching {
                        MuscleGroup.valueOf(muscleGroupName).displayName
                    }.getOrDefault(muscleGroupName),
                    position = relation.routineExercise.position,
                    restSeconds = relation.routineExercise.restSeconds,
                    notes = relation.routineExercise.notes,
                    sets = relation.sets
                        .sortedBy { it.setNumber }
                        .map { set ->
                            SetTemplate(
                                id = set.id,
                                setNumber = set.setNumber,
                                type = runCatching { SetType.valueOf(set.setType) }
                                    .getOrDefault(SetType.NORMAL),
                                repsMin = set.repsMin,
                                repsMax = set.repsMax,
                                targetWeightKg = set.targetWeightKg,
                                targetRir = set.targetRir
                            )
                        }
                )
            }
    )
}
