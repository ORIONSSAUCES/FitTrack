package com.brunoapp.fittrack.data.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 → v2: adds exercise library and personal records.
 * SQL must match exactly what Room generates for the entities.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `exercise` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `muscleGroup` TEXT NOT NULL,
                `secondaryMuscles` TEXT NOT NULL,
                `equipment` TEXT NOT NULL,
                `instructions` TEXT NOT NULL,
                `personalNotes` TEXT NOT NULL,
                `isCustom` INTEGER NOT NULL,
                `isFavorite` INTEGER NOT NULL,
                `createdAt` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `personal_record` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `exerciseId` INTEGER NOT NULL,
                `weightKg` REAL NOT NULL,
                `reps` INTEGER NOT NULL,
                `estimated1rm` REAL NOT NULL,
                `date` TEXT NOT NULL,
                `workoutSessionId` INTEGER,
                FOREIGN KEY(`exerciseId`) REFERENCES `exercise`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_personal_record_exerciseId` ON `personal_record` (`exerciseId`)"
        )
    }
}

/**
 * v2 → v3: adds routines with exercises and set templates.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `routine` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `dayOfWeek` INTEGER,
                `createdAt` TEXT NOT NULL,
                `updatedAt` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `routine_exercise` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `routineId` INTEGER NOT NULL,
                `exerciseId` INTEGER NOT NULL,
                `position` INTEGER NOT NULL,
                `restSeconds` INTEGER NOT NULL,
                `notes` TEXT NOT NULL,
                FOREIGN KEY(`routineId`) REFERENCES `routine`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`exerciseId`) REFERENCES `exercise`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_exercise_routineId` ON `routine_exercise` (`routineId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_exercise_exerciseId` ON `routine_exercise` (`exerciseId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `routine_set_template` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `routineExerciseId` INTEGER NOT NULL,
                `setNumber` INTEGER NOT NULL,
                `setType` TEXT NOT NULL,
                `repsMin` INTEGER NOT NULL,
                `repsMax` INTEGER NOT NULL,
                `targetWeightKg` REAL,
                `targetRir` INTEGER,
                FOREIGN KEY(`routineExerciseId`) REFERENCES `routine_exercise`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_routine_set_template_routineExerciseId` ON `routine_set_template` (`routineExerciseId`)")
    }
}

/**
 * v3 → v4: adds workout sessions, exercises, sets and active state.
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_session` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `routineId` INTEGER,
                `name` TEXT NOT NULL,
                `startTime` TEXT NOT NULL,
                `endTime` TEXT,
                `totalVolumeKg` REAL NOT NULL,
                `notes` TEXT NOT NULL,
                `isActive` INTEGER NOT NULL,
                FOREIGN KEY(`routineId`) REFERENCES `routine`(`id`)
                    ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_session_routineId` ON `workout_session` (`routineId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_session_isActive` ON `workout_session` (`isActive`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_exercise` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `workoutSessionId` INTEGER NOT NULL,
                `exerciseId` INTEGER NOT NULL,
                `position` INTEGER NOT NULL,
                `restSeconds` INTEGER NOT NULL,
                `notes` TEXT NOT NULL,
                FOREIGN KEY(`workoutSessionId`) REFERENCES `workout_session`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`exerciseId`) REFERENCES `exercise`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercise_workoutSessionId` ON `workout_exercise` (`workoutSessionId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_exercise_exerciseId` ON `workout_exercise` (`exerciseId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `workout_set` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `workoutExerciseId` INTEGER NOT NULL,
                `setNumber` INTEGER NOT NULL,
                `setType` TEXT NOT NULL,
                `targetRepsMin` INTEGER NOT NULL,
                `targetRepsMax` INTEGER NOT NULL,
                `weightKg` REAL,
                `reps` INTEGER,
                `rir` INTEGER,
                `isCompleted` INTEGER NOT NULL,
                `isPersonalRecord` INTEGER NOT NULL,
                `previousWeightKg` REAL,
                `previousReps` INTEGER,
                `completedAt` TEXT,
                FOREIGN KEY(`workoutExerciseId`) REFERENCES `workout_exercise`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_workout_set_workoutExerciseId` ON `workout_set` (`workoutExerciseId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `active_workout_state` (
                `id` INTEGER PRIMARY KEY NOT NULL,
                `workoutSessionId` INTEGER NOT NULL,
                `restEndTimeMs` INTEGER,
                `restTotalSeconds` INTEGER,
                `lastUpdated` TEXT NOT NULL
            )
            """.trimIndent()
        )
    }
}
