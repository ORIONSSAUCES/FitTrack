package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import com.brunoapp.fittrack.domain.model.Exercise
import com.brunoapp.fittrack.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ExerciseRepositoryImpl @Inject constructor(
    private val dao: ExerciseDao
) : ExerciseRepository {

    override fun observeAll(): Flow<List<Exercise>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Exercise?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Exercise? = dao.getById(id)?.toDomain()

    override suspend fun save(exercise: Exercise): Long {
        val existing = if (exercise.id != 0L) dao.getById(exercise.id) else null
        return dao.upsert(
            ExerciseEntity(
                id = exercise.id,
                name = exercise.name,
                muscleGroup = exercise.muscleGroup.name,
                secondaryMuscles = exercise.secondaryMuscles.joinToString(",") { it.name },
                equipment = exercise.equipment.name,
                instructions = exercise.instructions,
                personalNotes = exercise.personalNotes,
                isCustom = exercise.isCustom,
                isFavorite = exercise.isFavorite,
                createdAt = existing?.createdAt ?: Instant.now().toString()
            )
        )
    }

    override suspend fun delete(exercise: Exercise) {
        dao.getById(exercise.id)?.let { dao.delete(it) }
    }

    override suspend fun setFavorite(id: Long, favorite: Boolean) =
        dao.setFavorite(id, favorite)

    override suspend fun updateNotes(id: Long, notes: String) =
        dao.updateNotes(id, notes)

    companion object {
        fun ExerciseEntity.toDomain() = Exercise(
            id = id,
            name = name,
            muscleGroup = runCatching { MuscleGroup.valueOf(muscleGroup) }
                .getOrDefault(MuscleGroup.FULL_BODY),
            secondaryMuscles = secondaryMuscles.split(",")
                .filter { it.isNotBlank() }
                .mapNotNull { runCatching { MuscleGroup.valueOf(it.trim()) }.getOrNull() },
            equipment = runCatching { Equipment.valueOf(equipment) }
                .getOrDefault(Equipment.OTHER),
            instructions = instructions,
            personalNotes = personalNotes,
            isCustom = isCustom,
            isFavorite = isFavorite
        )
    }
}
