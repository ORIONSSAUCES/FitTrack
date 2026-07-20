package com.brunoapp.fittrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.PersonalRecordDao
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import com.brunoapp.fittrack.data.database.entity.ProfileEntity
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity

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
 *  3 — + routine, routine_exercise, routine_set_template
 */
@Database(
    entities = [
        ProfileEntity::class,
        ExerciseEntity::class,
        PersonalRecordEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        RoutineSetTemplateEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun routineDao(): RoutineDao

    companion object {
        const val DATABASE_NAME = "fittrack.db"
    }
}
