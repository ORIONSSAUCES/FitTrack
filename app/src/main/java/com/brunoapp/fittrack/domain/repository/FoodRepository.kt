package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    fun observeFoods(): Flow<List<FoodItem>>
    suspend fun getFood(id: Long): FoodItem?
    suspend fun saveFood(food: FoodItem): Long
    suspend fun deleteFood(id: Long)
    suspend fun setFoodFavorite(id: Long, favorite: Boolean)

    fun observeRecipes(): Flow<List<Recipe>>
    suspend fun getRecipe(id: Long): Recipe?
    suspend fun saveRecipe(recipe: Recipe): Long
    suspend fun deleteRecipe(id: Long)
}
