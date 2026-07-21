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

/**
 * v4 → v5: adds food library and recipes.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `food_item` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `brand` TEXT NOT NULL,
                `caloriesPer100` REAL NOT NULL,
                `proteinPer100` REAL NOT NULL,
                `carbsPer100` REAL NOT NULL,
                `fatPer100` REAL NOT NULL,
                `fiberPer100` REAL NOT NULL,
                `servingSize` REAL NOT NULL,
                `servingUnit` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                `isCustom` INTEGER NOT NULL,
                `isFavorite` INTEGER NOT NULL,
                `lastUsed` TEXT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recipe` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `servings` INTEGER NOT NULL,
                `createdAt` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `recipe_ingredient` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `recipeId` INTEGER NOT NULL,
                `foodItemId` INTEGER NOT NULL,
                `quantity` REAL NOT NULL,
                FOREIGN KEY(`recipeId`) REFERENCES `recipe`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`foodItemId`) REFERENCES `food_item`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredient_recipeId` ON `recipe_ingredient` (`recipeId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_recipe_ingredient_foodItemId` ON `recipe_ingredient` (`foodItemId`)")
    }
}

/**
 * v5 → v6: adds weekly diet plans.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `diet_plan` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `name` TEXT NOT NULL,
                `description` TEXT NOT NULL,
                `isActive` INTEGER NOT NULL,
                `caloriesTraining` INTEGER NOT NULL,
                `proteinTraining` REAL NOT NULL,
                `carbsTraining` REAL NOT NULL,
                `fatTraining` REAL NOT NULL,
                `fiberTraining` REAL NOT NULL,
                `waterTrainingMl` INTEGER NOT NULL,
                `caloriesRest` INTEGER NOT NULL,
                `proteinRest` REAL NOT NULL,
                `carbsRest` REAL NOT NULL,
                `fatRest` REAL NOT NULL,
                `fiberRest` REAL NOT NULL,
                `waterRestMl` INTEGER NOT NULL,
                `createdAt` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `diet_plan_day` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dietPlanId` INTEGER NOT NULL,
                `dayOfWeek` INTEGER NOT NULL,
                `isTrainingDay` INTEGER NOT NULL,
                FOREIGN KEY(`dietPlanId`) REFERENCES `diet_plan`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_diet_plan_day_dietPlanId` ON `diet_plan_day` (`dietPlanId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `planned_meal` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dietPlanDayId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `mealOrder` INTEGER NOT NULL,
                `notes` TEXT NOT NULL,
                FOREIGN KEY(`dietPlanDayId`) REFERENCES `diet_plan_day`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_meal_dietPlanDayId` ON `planned_meal` (`dietPlanDayId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `planned_meal_item` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `plannedMealId` INTEGER NOT NULL,
                `foodItemId` INTEGER,
                `recipeId` INTEGER,
                `quantity` REAL NOT NULL,
                FOREIGN KEY(`plannedMealId`) REFERENCES `planned_meal`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`foodItemId`) REFERENCES `food_item`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`recipeId`) REFERENCES `recipe`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_meal_item_plannedMealId` ON `planned_meal_item` (`plannedMealId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_meal_item_foodItemId` ON `planned_meal_item` (`foodItemId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_planned_meal_item_recipeId` ON `planned_meal_item` (`recipeId`)")
    }
}

/**
 * v6 → v7: adds daily nutrition logs.
 */
val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `daily_log` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `date` TEXT NOT NULL,
                `dietPlanId` INTEGER,
                `isTrainingDay` INTEGER NOT NULL,
                `adherence` TEXT NOT NULL,
                `notes` TEXT NOT NULL,
                FOREIGN KEY(`dietPlanId`) REFERENCES `diet_plan`(`id`)
                    ON UPDATE NO ACTION ON DELETE SET NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_daily_log_date` ON `daily_log` (`date`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_log_dietPlanId` ON `daily_log` (`dietPlanId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `daily_meal` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dailyLogId` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `mealOrder` INTEGER NOT NULL,
                `isCompleted` INTEGER NOT NULL,
                FOREIGN KEY(`dailyLogId`) REFERENCES `daily_log`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_meal_dailyLogId` ON `daily_meal` (`dailyLogId`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `daily_food_entry` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `dailyMealId` INTEGER NOT NULL,
                `foodItemId` INTEGER,
                `recipeId` INTEGER,
                `quantity` REAL NOT NULL,
                `loggedAt` TEXT NOT NULL,
                FOREIGN KEY(`dailyMealId`) REFERENCES `daily_meal`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`foodItemId`) REFERENCES `food_item`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE,
                FOREIGN KEY(`recipeId`) REFERENCES `recipe`(`id`)
                    ON UPDATE NO ACTION ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_food_entry_dailyMealId` ON `daily_food_entry` (`dailyMealId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_food_entry_foodItemId` ON `daily_food_entry` (`foodItemId`)")
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_daily_food_entry_recipeId` ON `daily_food_entry` (`recipeId`)")
    }
}

/**
 * v7 → v8: exercise images + weight, measurements and progress photos.
 */
val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `exercise` ADD COLUMN `imagePath` TEXT")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `weight_entry` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `date` TEXT NOT NULL,
                `time` TEXT NOT NULL,
                `weightKg` REAL NOT NULL,
                `notes` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_weight_entry_date` ON `weight_entry` (`date`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `body_measurement` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `date` TEXT NOT NULL,
                `waistCm` REAL, `abdomenCm` REAL, `chestCm` REAL, `hipsCm` REAL,
                `neckCm` REAL, `leftArmCm` REAL, `rightArmCm` REAL,
                `leftThighCm` REAL, `rightThighCm` REAL, `bodyFatPct` REAL,
                `notes` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_body_measurement_date` ON `body_measurement` (`date`)")
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `progress_photo` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `date` TEXT NOT NULL,
                `weightKg` REAL,
                `frontPhotoPath` TEXT, `sidePhotoPath` TEXT, `backPhotoPath` TEXT,
                `notes` TEXT NOT NULL
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_progress_photo_date` ON `progress_photo` (`date`)")
    }
}
