package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds the base exercise library when the table is empty.
 * Runs at app start; safe to call multiple times.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    suspend fun seedIfNeeded() {
        if (exerciseDao.count() == 0) {
            exerciseDao.upsertAll(ExerciseSeed.all())
        }
    }
}
