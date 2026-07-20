package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<Profile?>
    suspend fun getProfile(): Profile?
    suspend fun saveProfile(profile: Profile)
}
