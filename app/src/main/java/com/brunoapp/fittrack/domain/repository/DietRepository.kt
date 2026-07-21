package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.domain.model.DietPlan
import com.brunoapp.fittrack.domain.model.MacroGoals
import kotlinx.coroutines.flow.Flow

interface DietRepository {
    fun observePlans(): Flow<List<DietPlan>>          // without day detail
    fun observeActivePlan(): Flow<DietPlan?>          // full detail with macros
    suspend fun createPlan(name: String): Long        // creates 7 empty days, activates if first
    suspend fun deletePlan(id: Long)
    suspend fun setActivePlan(id: Long)
    suspend fun updateGoals(planId: Long, training: MacroGoals, rest: MacroGoals)
    suspend fun setTrainingDay(dayId: Long, isTraining: Boolean)
    suspend fun addMeal(dayId: Long, name: String, order: Int)
    suspend fun renameMeal(mealId: Long, name: String)
    suspend fun deleteMeal(mealId: Long)
    suspend fun addFoodItem(mealId: Long, foodId: Long, quantity: Double)
    suspend fun addRecipeItem(mealId: Long, recipeId: Long, servings: Double)
    suspend fun updateItemQuantity(itemId: Long, quantity: Double)
    suspend fun deleteItem(itemId: Long)
}
