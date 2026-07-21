package com.brunoapp.fittrack.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.brunoapp.fittrack.data.database.dao.ActiveWorkoutStateDao
import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.dao.PersonalRecordDao
import com.brunoapp.fittrack.data.database.dao.ProfileDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.dao.WorkoutDao
import com.brunoapp.fittrack.data.database.entity.ActiveWorkoutStateEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.entity.ProfileEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutExerciseEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSessionEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSetEntity

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
 *  4 — + workout_session, workout_exercise, workout_set, active_workout_state
 *  5 — + food_item, recipe, recipe_ingredient
 *  6 — + diet_plan, diet_plan_day, planned_meal, planned_meal_item
 */
@Database(
    entities = [
        ProfileEntity::class,
        ExerciseEntity::class,
        PersonalRecordEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        RoutineSetTemplateEntity::class,
        WorkoutSessionEntity::class,
        WorkoutExerciseEntity::class,
        WorkoutSetEntity::class,
        ActiveWorkoutStateEntity::class,
        FoodItemEntity::class,
        RecipeEntity::class,
        RecipeIngredientEntity::class,
        DietPlanEntity::class,
        DietPlanDayEntity::class,
        PlannedMealEntity::class,
        PlannedMealItemEntity::class
    ],
    version = 6,
    exportSchema = true
)
abstract class FitTrackDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun activeWorkoutStateDao(): ActiveWorkoutStateDao
    abstract fun foodDao(): FoodDao
    abstract fun dietDao(): DietDao

    companion object {
        const val DATABASE_NAME = "fittrack.db"
    }
}
