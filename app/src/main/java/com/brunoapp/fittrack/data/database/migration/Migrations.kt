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
