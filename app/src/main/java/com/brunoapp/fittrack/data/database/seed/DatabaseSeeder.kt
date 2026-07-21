package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
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
    private val dietDao: DietDao
) {
    suspend fun seedIfNeeded() {
        if (exerciseDao.count() == 0) {
            exerciseDao.upsertAll(ExerciseSeed.all())
        }
        // Assign bundled images to the base Spanish exercises (idempotent)
        ExerciseImageMap.byExerciseName.forEach { (name, imagePath) ->
            exerciseDao.setImageByName(name, imagePath)
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
        if (dietDao.countPlans() == 0) {
            seedDietPlan()
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
