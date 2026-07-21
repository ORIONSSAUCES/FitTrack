package com.brunoapp.fittrack.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import com.brunoapp.fittrack.data.database.FitTrackDatabase
import com.brunoapp.fittrack.data.database.dao.BackupDao
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupData(
    val formatVersion: Int = 1,
    val exportedAt: String = "",
    val appDatabaseVersion: Int = 9,
    val profiles: List<ProfileEntity> = emptyList(),
    val exercises: List<ExerciseEntity> = emptyList(),
    val personalRecords: List<PersonalRecordEntity> = emptyList(),
    val routines: List<RoutineEntity> = emptyList(),
    val routineExercises: List<RoutineExerciseEntity> = emptyList(),
    val routineSetTemplates: List<RoutineSetTemplateEntity> = emptyList(),
    val workoutSessions: List<WorkoutSessionEntity> = emptyList(),
    val workoutExercises: List<WorkoutExerciseEntity> = emptyList(),
    val workoutSets: List<WorkoutSetEntity> = emptyList(),
    val foods: List<FoodItemEntity> = emptyList(),
    val recipes: List<RecipeEntity> = emptyList(),
    val recipeIngredients: List<RecipeIngredientEntity> = emptyList(),
    val dietPlans: List<DietPlanEntity> = emptyList(),
    val dietPlanDays: List<DietPlanDayEntity> = emptyList(),
    val plannedMeals: List<PlannedMealEntity> = emptyList(),
    val plannedMealItems: List<PlannedMealItemEntity> = emptyList(),
    val dailyLogs: List<DailyLogEntity> = emptyList(),
    val dailyMeals: List<DailyMealEntity> = emptyList(),
    val dailyFoodEntries: List<DailyFoodEntryEntity> = emptyList(),
    val weightEntries: List<WeightEntryEntity> = emptyList(),
    val bodyMeasurements: List<BodyMeasurementEntity> = emptyList(),
    val progressPhotos: List<ProgressPhotoEntity> = emptyList()
)

sealed class BackupResult {
    data object Success : BackupResult()
    data class Error(val message: String) : BackupResult()
}

/**
 * Exports/imports the full database as JSON through the system file picker.
 * NOTE: progress photo FILES are not embedded; only their records.
 */
@Singleton
class BackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: FitTrackDatabase,
    private val backupDao: BackupDao
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = false
    }

    suspend fun exportToUri(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        runCatching {
            val data = BackupData(
                exportedAt = Instant.now().toString(),
                profiles = backupDao.allProfiles(),
                exercises = backupDao.allExercises(),
                personalRecords = backupDao.allRecords(),
                routines = backupDao.allRoutines(),
                routineExercises = backupDao.allRoutineExercises(),
                routineSetTemplates = backupDao.allSetTemplates(),
                workoutSessions = backupDao.allSessions(),
                workoutExercises = backupDao.allWorkoutExercises(),
                workoutSets = backupDao.allWorkoutSets(),
                foods = backupDao.allFoods(),
                recipes = backupDao.allRecipes(),
                recipeIngredients = backupDao.allIngredients(),
                dietPlans = backupDao.allDietPlans(),
                dietPlanDays = backupDao.allDietDays(),
                plannedMeals = backupDao.allPlannedMeals(),
                plannedMealItems = backupDao.allPlannedItems(),
                dailyLogs = backupDao.allDailyLogs(),
                dailyMeals = backupDao.allDailyMeals(),
                dailyFoodEntries = backupDao.allDailyEntries(),
                weightEntries = backupDao.allWeights(),
                bodyMeasurements = backupDao.allMeasurements(),
                progressPhotos = backupDao.allPhotos()
            )
            context.contentResolver.openOutputStream(uri, "wt")?.use { output ->
                output.write(json.encodeToString(BackupData.serializer(), data).toByteArray())
            } ?: error("No se pudo abrir el archivo")
            BackupResult.Success
        }.getOrElse { BackupResult.Error(it.message ?: "Error desconocido") }
    }

    suspend fun importFromUri(uri: Uri): BackupResult = withContext(Dispatchers.IO) {
        runCatching {
            val text = context.contentResolver.openInputStream(uri)?.use {
                it.readBytes().decodeToString()
            } ?: error("No se pudo leer el archivo")

            val data = json.decodeFromString(BackupData.serializer(), text)
            if (data.formatVersion > 1) {
                return@runCatching BackupResult.Error(
                    "El archivo es de una versión más nueva de la app"
                )
            }

            database.withTransaction {
                // children → parents
                backupDao.clearActiveState()
                backupDao.clearDailyEntries()
                backupDao.clearDailyMeals()
                backupDao.clearDailyLogs()
                backupDao.clearPlannedItems()
                backupDao.clearPlannedMeals()
                backupDao.clearDietDays()
                backupDao.clearDietPlans()
                backupDao.clearIngredients()
                backupDao.clearRecipes()
                backupDao.clearWorkoutSets()
                backupDao.clearWorkoutExercises()
                backupDao.clearSessions()
                backupDao.clearSetTemplates()
                backupDao.clearRoutineExercises()
                backupDao.clearRoutines()
                backupDao.clearRecords()
                backupDao.clearFoods()
                backupDao.clearExercises()
                backupDao.clearProfiles()
                backupDao.clearWeights()
                backupDao.clearMeasurements()
                backupDao.clearPhotos()

                // parents → children
                backupDao.insertProfiles(data.profiles)
                backupDao.insertExercises(data.exercises)
                backupDao.insertRecords(data.personalRecords)
                backupDao.insertRoutines(data.routines)
                backupDao.insertRoutineExercises(data.routineExercises)
                backupDao.insertSetTemplates(data.routineSetTemplates)
                backupDao.insertSessions(
                    data.workoutSessions.map { it.copy(isActive = false) }
                )
                backupDao.insertWorkoutExercises(data.workoutExercises)
                backupDao.insertWorkoutSets(data.workoutSets)
                backupDao.insertFoods(data.foods)
                backupDao.insertRecipes(data.recipes)
                backupDao.insertIngredients(data.recipeIngredients)
                backupDao.insertDietPlans(data.dietPlans)
                backupDao.insertDietDays(data.dietPlanDays)
                backupDao.insertPlannedMeals(data.plannedMeals)
                backupDao.insertPlannedItems(data.plannedMealItems)
                backupDao.insertDailyLogs(data.dailyLogs)
                backupDao.insertDailyMeals(data.dailyMeals)
                backupDao.insertDailyEntries(data.dailyFoodEntries)
                backupDao.insertWeights(data.weightEntries)
                backupDao.insertMeasurements(data.bodyMeasurements)
                backupDao.insertPhotos(data.progressPhotos)
            }
            BackupResult.Success
        }.getOrElse { BackupResult.Error(it.message ?: "Archivo inválido") }
    }
}
