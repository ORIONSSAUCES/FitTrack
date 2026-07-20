package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.constants.Objective
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.entity.ProfileEntity
import com.brunoapp.fittrack.domain.model.Profile
import com.brunoapp.fittrack.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao
) : ProfileRepository {

    override fun observeProfile(): Flow<Profile?> =
        dao.observeProfile().map { it?.toDomain() }

    override suspend fun getProfile(): Profile? = dao.getProfile()?.toDomain()

    override suspend fun saveProfile(profile: Profile) {
        val existing = dao.getProfile()
        dao.upsert(
            ProfileEntity(
                id = 1,
                name = profile.name,
                heightCm = profile.heightCm,
                weightInitialKg = profile.weightInitialKg,
                weightGoalKg = profile.weightGoalKg,
                objective = profile.objective.name,
                defaultRestSeconds = profile.defaultRestSeconds,
                weeklyCheckDay = profile.weeklyCheckDay,
                createdAt = existing?.createdAt ?: Instant.now().toString()
            )
        )
    }

    private fun ProfileEntity.toDomain() = Profile(
        name = name,
        heightCm = heightCm,
        weightInitialKg = weightInitialKg,
        weightGoalKg = weightGoalKg,
        objective = runCatching { Objective.valueOf(objective) }
            .getOrDefault(Objective.MAINTAIN),
        defaultRestSeconds = defaultRestSeconds,
        weeklyCheckDay = weeklyCheckDay
    )
}
