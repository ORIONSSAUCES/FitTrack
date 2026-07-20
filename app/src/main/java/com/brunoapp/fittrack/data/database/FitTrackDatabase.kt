package com.brunoapp.fittrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.entity.ProfileEntity

/**
 * Main Room database.
 *
 * IMPORTANT: whenever the schema changes, bump [DATABASE_VERSION]
 * and register a Migration in di/DatabaseModule.kt. Never use
 * destructive migrations in production builds.
 */
@Database(
    entities = [
        ProfileEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao

    companion object {
        const val DATABASE_NAME = "fittrack.db"
    }
}
