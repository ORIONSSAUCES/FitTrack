package com.brunoapp.fittrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.PersonalRecordDao
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import com.brunoapp.fittrack.data.database.entity.ProfileEntity

/**
 * Main Room database.
 *
 * IMPORTANT: whenever the schema changes, bump the version
 * and register a Migration in di/DatabaseModule.kt. Never use
 * destructive migrations in production builds.
 *
 * Version history:
 *  1 — profile
 *  2 — + exercise, personal_record
 */
@Database(
    entities = [
        ProfileEntity::class,
        ExerciseEntity::class,
        PersonalRecordEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun personalRecordDao(): PersonalRecordDao

    companion object {
        const val DATABASE_NAME = "fittrack.db"
    }
}
