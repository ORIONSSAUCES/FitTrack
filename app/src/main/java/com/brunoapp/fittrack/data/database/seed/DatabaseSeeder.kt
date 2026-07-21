package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.dao.ExerciseDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
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
    private val foodDao: FoodDao
) {
    suspend fun seedIfNeeded() {
        if (exerciseDao.count() == 0) {
            exerciseDao.upsertAll(ExerciseSeed.all())
        }
        if (foodDao.countFoods() == 0) {
            foodDao.upsertFoods(FoodSeed.all())
        }
        if (foodDao.countRecipes() == 0) {
            seedRecipes()
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
