package com.brunoapp.fittrack.domain.repository

import com.brunoapp.fittrack.core.constants.AdherenceLevel
import com.brunoapp.fittrack.domain.model.DailyLog
import kotlinx.coroutines.flow.Flow

interface DailyLogRepository {
    fun observeLog(date: String): Flow<DailyLog?>
    fun observeLogsBetween(startDate: String, endDate: String): Flow<List<DailyLog>>

    /** Creates the day's log copying meals+items from the active plan. */
    suspend fun startDayFromPlan(date: String, dayOfWeek: Int): Long?

    /** Creates an empty log with the six default meal slots. */
    suspend fun startEmptyDay(date: String): Long

    suspend fun setMealCompleted(mealId: Long, completed: Boolean)
    suspend fun setAdherence(logId: Long, adherence: AdherenceLevel)
    suspend fun setTrainingDay(logId: Long, isTraining: Boolean)
    suspend fun addMeal(logId: Long, name: String, order: Int)
    suspend fun deleteMeal(mealId: Long)
    suspend fun addFoodEntry(mealId: Long, foodId: Long, quantity: Double)
    suspend fun addRecipeEntry(mealId: Long, recipeId: Long, servings: Double)
    suspend fun updateEntryQuantity(entryId: Long, quantity: Double)
    suspend fun deleteEntry(entryId: Long)
}
