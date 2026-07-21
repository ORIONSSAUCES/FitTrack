package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.brunoapp.fittrack.data.database.entity.ActiveWorkoutStateEntity
import com.brunoapp.fittrack.data.database.entity.BodyMeasurementEntity
import com.brunoapp.fittrack.data.database.entity.DailyFoodEntryEntity
import com.brunoapp.fittrack.data.database.entity.DailyLogEntity
import com.brunoapp.fittrack.data.database.entity.DailyMealEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.entity.PersonalRecordEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.entity.ProfileEntity
import com.brunoapp.fittrack.data.database.entity.ProgressPhotoEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity
import com.brunoapp.fittrack.data.database.entity.WeightEntryEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutExerciseEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSessionEntity
import com.brunoapp.fittrack.data.database.entity.WorkoutSetEntity

/** Raw table access for export/import. */
@Dao
interface BackupDao {

    // ── Dumps ──
    @Query("SELECT * FROM profile") suspend fun allProfiles(): List<ProfileEntity>
    @Query("SELECT * FROM exercise") suspend fun allExercises(): List<ExerciseEntity>
    @Query("SELECT * FROM personal_record") suspend fun allRecords(): List<PersonalRecordEntity>
    @Query("SELECT * FROM routine") suspend fun allRoutines(): List<RoutineEntity>
    @Query("SELECT * FROM routine_exercise") suspend fun allRoutineExercises(): List<RoutineExerciseEntity>
    @Query("SELECT * FROM routine_set_template") suspend fun allSetTemplates(): List<RoutineSetTemplateEntity>
    @Query("SELECT * FROM workout_session") suspend fun allSessions(): List<WorkoutSessionEntity>
    @Query("SELECT * FROM workout_exercise") suspend fun allWorkoutExercises(): List<WorkoutExerciseEntity>
    @Query("SELECT * FROM workout_set") suspend fun allWorkoutSets(): List<WorkoutSetEntity>
    @Query("SELECT * FROM food_item") suspend fun allFoods(): List<FoodItemEntity>
    @Query("SELECT * FROM recipe") suspend fun allRecipes(): List<RecipeEntity>
    @Query("SELECT * FROM recipe_ingredient") suspend fun allIngredients(): List<RecipeIngredientEntity>
    @Query("SELECT * FROM diet_plan") suspend fun allDietPlans(): List<DietPlanEntity>
    @Query("SELECT * FROM diet_plan_day") suspend fun allDietDays(): List<DietPlanDayEntity>
    @Query("SELECT * FROM planned_meal") suspend fun allPlannedMeals(): List<PlannedMealEntity>
    @Query("SELECT * FROM planned_meal_item") suspend fun allPlannedItems(): List<PlannedMealItemEntity>
    @Query("SELECT * FROM daily_log") suspend fun allDailyLogs(): List<DailyLogEntity>
    @Query("SELECT * FROM daily_meal") suspend fun allDailyMeals(): List<DailyMealEntity>
    @Query("SELECT * FROM daily_food_entry") suspend fun allDailyEntries(): List<DailyFoodEntryEntity>
    @Query("SELECT * FROM weight_entry") suspend fun allWeights(): List<WeightEntryEntity>
    @Query("SELECT * FROM body_measurement") suspend fun allMeasurements(): List<BodyMeasurementEntity>
    @Query("SELECT * FROM progress_photo") suspend fun allPhotos(): List<ProgressPhotoEntity>

    // ── Clears (children first) ──
    @Query("DELETE FROM active_workout_state") suspend fun clearActiveState()
    @Query("DELETE FROM daily_food_entry") suspend fun clearDailyEntries()
    @Query("DELETE FROM daily_meal") suspend fun clearDailyMeals()
    @Query("DELETE FROM daily_log") suspend fun clearDailyLogs()
    @Query("DELETE FROM planned_meal_item") suspend fun clearPlannedItems()
    @Query("DELETE FROM planned_meal") suspend fun clearPlannedMeals()
    @Query("DELETE FROM diet_plan_day") suspend fun clearDietDays()
    @Query("DELETE FROM diet_plan") suspend fun clearDietPlans()
    @Query("DELETE FROM recipe_ingredient") suspend fun clearIngredients()
    @Query("DELETE FROM recipe") suspend fun clearRecipes()
    @Query("DELETE FROM food_item") suspend fun clearFoods()
    @Query("DELETE FROM workout_set") suspend fun clearWorkoutSets()
    @Query("DELETE FROM workout_exercise") suspend fun clearWorkoutExercises()
    @Query("DELETE FROM workout_session") suspend fun clearSessions()
    @Query("DELETE FROM routine_set_template") suspend fun clearSetTemplates()
    @Query("DELETE FROM routine_exercise") suspend fun clearRoutineExercises()
    @Query("DELETE FROM routine") suspend fun clearRoutines()
    @Query("DELETE FROM personal_record") suspend fun clearRecords()
    @Query("DELETE FROM exercise") suspend fun clearExercises()
    @Query("DELETE FROM profile") suspend fun clearProfiles()
    @Query("DELETE FROM weight_entry") suspend fun clearWeights()
    @Query("DELETE FROM body_measurement") suspend fun clearMeasurements()
    @Query("DELETE FROM progress_photo") suspend fun clearPhotos()

    // ── Inserts (parents first) ──
    @Insert suspend fun insertProfiles(items: List<ProfileEntity>)
    @Insert suspend fun insertExercises(items: List<ExerciseEntity>)
    @Insert suspend fun insertRecords(items: List<PersonalRecordEntity>)
    @Insert suspend fun insertRoutines(items: List<RoutineEntity>)
    @Insert suspend fun insertRoutineExercises(items: List<RoutineExerciseEntity>)
    @Insert suspend fun insertSetTemplates(items: List<RoutineSetTemplateEntity>)
    @Insert suspend fun insertSessions(items: List<WorkoutSessionEntity>)
    @Insert suspend fun insertWorkoutExercises(items: List<WorkoutExerciseEntity>)
    @Insert suspend fun insertWorkoutSets(items: List<WorkoutSetEntity>)
    @Insert suspend fun insertFoods(items: List<FoodItemEntity>)
    @Insert suspend fun insertRecipes(items: List<RecipeEntity>)
    @Insert suspend fun insertIngredients(items: List<RecipeIngredientEntity>)
    @Insert suspend fun insertDietPlans(items: List<DietPlanEntity>)
    @Insert suspend fun insertDietDays(items: List<DietPlanDayEntity>)
    @Insert suspend fun insertPlannedMeals(items: List<PlannedMealEntity>)
    @Insert suspend fun insertPlannedItems(items: List<PlannedMealItemEntity>)
    @Insert suspend fun insertDailyLogs(items: List<DailyLogEntity>)
    @Insert suspend fun insertDailyMeals(items: List<DailyMealEntity>)
    @Insert suspend fun insertDailyEntries(items: List<DailyFoodEntryEntity>)
    @Insert suspend fun insertWeights(items: List<WeightEntryEntity>)
    @Insert suspend fun insertMeasurements(items: List<BodyMeasurementEntity>)
    @Insert suspend fun insertPhotos(items: List<ProgressPhotoEntity>)
}
