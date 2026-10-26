package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.dao.RoutineDao
import com.brunoapp.fittrack.data.database.entity.RoutineEntity
import com.brunoapp.fittrack.data.database.entity.RoutineExerciseEntity
import com.brunoapp.fittrack.data.database.entity.RoutineSetTemplateEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
import com.brunoapp.fittrack.data.datastore.PlanPreset
import com.brunoapp.fittrack.data.datastore.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Seeds base content when tables are empty.
 * Runs at app start; safe to call multiple times.
 */
@Singleton
class DatabaseSeeder @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val foodDao: FoodDao,
    private val dietDao: DietDao,
    private val routineDao: RoutineDao,
    private val userPreferences: UserPreferences
) {
    private val mutex = Mutex()

    /**
     * Shared library content is always seeded. Personal content (routines,
     * diet) depends on the plan preset chosen on first launch. Installs that
     * already have data are treated as the GYM preset.
     */
    suspend fun seedIfNeeded() = mutex.withLock {
        seedLibrary()

        var preset = userPreferences.planPreset.first()
        if (preset == null &&
            (routineDao.countRoutines() > 0 || dietDao.countPlans() > 0)
        ) {
            preset = PlanPreset.GYM
            userPreferences.setPlanPreset(PlanPreset.GYM)
        }

        when (preset) {
            PlanPreset.GYM -> {
                if (dietDao.countPlans() == 0) seedDietPlan()
                // Adds plan routines missing by exact name; never deletes user routines
                seedMissingRoutines(RoutineSeed.all())
            }
            PlanPreset.HOME -> seedMissingRoutines(HomeRoutineSeed.all())
            else -> Unit
        }
    }

    private suspend fun seedLibrary() {
        if (exerciseDao.count() == 0) {
            exerciseDao.upsertAll(ExerciseSeed.all())
        }
        // Assign bundled images to the base Spanish exercises (idempotent)
        ExerciseImageMap.byExerciseName.forEach { (name, imagePath) ->
            exerciseDao.setImageByName(name, imagePath)
        }
        // Assign effectiveness tiers (idempotent)
        ExerciseTierMap.byExerciseName.forEach { (name, tier) ->
            exerciseDao.setTierByName(name, tier)
        }
        // Import the extended English catalog once
        if (exerciseDao.countWithImages() < ExerciseImageCatalog.all().size) {
            val existingNames = exerciseDao.getAllNamesOnce().toSet()
            val newOnes = ExerciseImageCatalog.all().filter { it.name !in existingNames }
            if (newOnes.isNotEmpty()) exerciseDao.upsertAll(newOnes)
        }
        if (foodDao.countFoods() == 0) {
            foodDao.upsertFoods(FoodSeed.all())
        }
        if (foodDao.countRecipes() == 0) {
            seedRecipes()
        }
        // At-home exercises (inserted by name if missing)
        val names = exerciseDao.getAllNamesOnce().toSet()
        val homeMissing = HomeExerciseSeed.all().filter { it.name !in names }
        if (homeMissing.isNotEmpty()) exerciseDao.upsertAll(homeMissing)
    }

    private suspend fun seedMissingRoutines(plan: List<RoutineSeed.SeedRoutine>) {
        val existingNames = routineDao.getAllRoutineNames().toSet()
        val alreadySeeded = userPreferences.seededRoutines.first()
        // Seed each plan routine only once: if the user deletes it, it stays deleted
        val missing = plan.filter { it.name !in existingNames && it.name !in alreadySeeded }
        if (missing.isNotEmpty()) {
            seedRoutines(missing)
        }
        userPreferences.addSeededRoutines(plan.map { it.name }.toSet())
    }

    private suspend fun seedRoutines(routines: List<RoutineSeed.SeedRoutine>) {
        val idByName = exerciseDao.getAllIdNamePairs().associate { it.name to it.id }
        val now = Instant.now().toString()

        routines.forEach { seed ->
            val resolved = seed.exercises.mapNotNull { exercise ->
                idByName[exercise.name]?.let { id -> exercise to id }
            }
            if (resolved.size != seed.exercises.size) return@forEach

            routineDao.saveFullRoutine(
                routine = RoutineEntity(
                    name = seed.name,
                    description = seed.description,
                    dayOfWeek = seed.dayOfWeek,
                    createdAt = now,
                    updatedAt = now
                ),
                exercises = resolved.mapIndexed { index, (exercise, exerciseId) ->
                    RoutineExerciseEntity(
                        routineId = 0,
                        exerciseId = exerciseId,
                        position = index,
                        restSeconds = exercise.restSeconds,
                        notes = exercise.notes
                    ) to exercise.sets.mapIndexed { setIndex, set ->
                        RoutineSetTemplateEntity(
                            routineExerciseId = 0,
                            setNumber = setIndex + 1,
                            setType = "NORMAL",
                            repsMin = set.repsMin,
                            repsMax = set.repsMax,
                            targetRir = set.rir
                        )
                    }
                }
            )
        }
    }

    private suspend fun seedDietPlan() {
        val foodIdByName = foodDao.getAllFoodsOnce().associate { it.name to it.id }
        val recipeIdByName = foodDao.getAllRecipesOnce().associate { it.name to it.id }

        val planId = dietDao.insertPlan(
            DietPlanEntity(
                name = DietPlanSeed.PLAN_NAME,
                description = DietPlanSeed.PLAN_DESCRIPTION,
                isActive = true,
                createdAt = Instant.now().toString()
            )
        )

        DietPlanSeed.days().forEach { seedDay ->
            val dayId = dietDao.insertDay(
                DietPlanDayEntity(
                    dietPlanId = planId,
                    dayOfWeek = seedDay.dayOfWeek,
                    isTrainingDay = seedDay.isTraining
                )
            )
            seedDay.meals.forEachIndexed { order, seedMeal ->
                val mealId = dietDao.insertMeal(
                    PlannedMealEntity(
                        dietPlanDayId = dayId,
                        name = seedMeal.name,
                        mealOrder = order
                    )
                )
                seedMeal.items.forEach { item ->
                    if (item.isRecipe) {
                        recipeIdByName[item.name]?.let { recipeId ->
                            dietDao.insertItem(
                                PlannedMealItemEntity(
                                    plannedMealId = mealId,
                                    recipeId = recipeId,
                                    quantity = item.quantity
                                )
                            )
                        }
                    } else {
                        foodIdByName[item.name]?.let { foodId ->
                            dietDao.insertItem(
                                PlannedMealItemEntity(
                                    plannedMealId = mealId,
                                    foodItemId = foodId,
                                    quantity = item.quantity
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun seedRecipes() {
        val foodIdByName = foodDao.getAllFoodsOnce().associate { it.name to it.id }

        RecipeSeed.all().forEach { seed ->
            val resolved = seed.ingredients.mapNotNull { (foodName, quantity) ->
                foodIdByName[foodName]?.let { it to quantity }
            }
            // Only seed the recipe if every ingredient was resolved
            if (resolved.size == seed.ingredients.size) {
                foodDao.saveFullRecipe(
                    recipe = RecipeEntity(
                        name = seed.name,
                        description = seed.description,
                        servings = 1,
                        createdAt = Instant.now().toString()
                    ),
                    ingredients = resolved.map { (foodId, quantity) ->
                        RecipeIngredientEntity(
                            recipeId = 0,
                            foodItemId = foodId,
                            quantity = quantity
                        )
                    }
                )
            }
        }
    }
}
