package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.entity.RecipeEntity
import com.brunoapp.fittrack.data.database.entity.RecipeIngredientEntity
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
import com.brunoapp.fittrack.domain.model.RecipeIngredient
import com.brunoapp.fittrack.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(
    private val dao: FoodDao
) : FoodRepository {

    override fun observeFoods(): Flow<List<FoodItem>> =
        dao.observeAllFoods().map { list -> list.map { it.toDomain() } }

    override suspend fun getFood(id: Long): FoodItem? = dao.getFood(id)?.toDomain()

    override suspend fun saveFood(food: FoodItem): Long =
        dao.upsertFood(
            FoodItemEntity(
                id = food.id,
                name = food.name,
                brand = food.brand,
                caloriesPer100 = food.caloriesPer100,
                proteinPer100 = food.proteinPer100,
                carbsPer100 = food.carbsPer100,
                fatPer100 = food.fatPer100,
                fiberPer100 = food.fiberPer100,
                servingSize = food.servingSize,
                servingUnit = food.servingUnit,
                notes = food.notes,
                isCustom = food.isCustom,
                isFavorite = food.isFavorite
            )
        )

    override suspend fun deleteFood(id: Long) = dao.deleteFood(id)

    override suspend fun setFoodFavorite(id: Long, favorite: Boolean) =
        dao.setFoodFavorite(id, favorite)

    override fun observeRecipes(): Flow<List<Recipe>> =
        combine(dao.observeAllRecipes(), dao.observeAllFoods()) { recipes, foods ->
            val foodById = foods.associateBy { it.id }
            recipes.map { relation ->
                Recipe(
                    id = relation.recipe.id,
                    name = relation.recipe.name,
                    description = relation.recipe.description,
                    servings = relation.recipe.servings,
                    ingredients = relation.ingredients.map { ingredient ->
                        val food = foodById[ingredient.foodItemId]
                        RecipeIngredient(
                            id = ingredient.id,
                            foodItemId = ingredient.foodItemId,
                            foodName = food?.name.orEmpty(),
                            quantity = ingredient.quantity,
                            unit = food?.servingUnit ?: "g"
                        )
                    }
                )
            }
        }

    override suspend fun getRecipe(id: Long): Recipe? {
        val relation = dao.getRecipe(id) ?: return null
        return Recipe(
            id = relation.recipe.id,
            name = relation.recipe.name,
            description = relation.recipe.description,
            servings = relation.recipe.servings,
            ingredients = relation.ingredients.map { ingredient ->
                val food = dao.getFood(ingredient.foodItemId)
                RecipeIngredient(
                    id = ingredient.id,
                    foodItemId = ingredient.foodItemId,
                    foodName = food?.name.orEmpty(),
                    quantity = ingredient.quantity,
                    unit = food?.servingUnit ?: "g"
                )
            }
        )
    }

    override suspend fun saveRecipe(recipe: Recipe): Long =
        dao.saveFullRecipe(
            recipe = RecipeEntity(
                id = recipe.id,
                name = recipe.name,
                description = recipe.description,
                servings = recipe.servings,
                createdAt = Instant.now().toString()
            ),
            ingredients = recipe.ingredients.map { ingredient ->
                RecipeIngredientEntity(
                    recipeId = recipe.id,
                    foodItemId = ingredient.foodItemId,
                    quantity = ingredient.quantity
                )
            }
        )

    override suspend fun deleteRecipe(id: Long) = dao.deleteRecipe(id)

    private fun FoodItemEntity.toDomain() = FoodItem(
        id = id,
        name = name,
        brand = brand,
        caloriesPer100 = caloriesPer100,
        proteinPer100 = proteinPer100,
        carbsPer100 = carbsPer100,
        fatPer100 = fatPer100,
        fiberPer100 = fiberPer100,
        servingSize = servingSize,
        servingUnit = servingUnit,
        notes = notes,
        isCustom = isCustom,
        isFavorite = isFavorite
    )
}
