package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.constants.AdherenceLevel
import com.brunoapp.fittrack.core.utils.NutritionCalc
import com.brunoapp.fittrack.data.database.dao.DailyLogDao
import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.entity.DailyFoodEntryEntity
import com.brunoapp.fittrack.data.database.entity.DailyLogEntity
import com.brunoapp.fittrack.data.database.entity.DailyMealEntity
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.relation.DailyLogWithMeals
import com.brunoapp.fittrack.data.database.relation.RecipeWithIngredients
import com.brunoapp.fittrack.domain.model.DailyLog
import com.brunoapp.fittrack.domain.model.DailyMeal
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.MacroSummary
import com.brunoapp.fittrack.domain.model.PlannedItem
import com.brunoapp.fittrack.domain.repository.DailyLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.Instant
import javax.inject.Inject

class DailyLogRepositoryImpl @Inject constructor(
    private val dailyLogDao: DailyLogDao,
    private val dietDao: DietDao,
    private val foodDao: FoodDao
) : DailyLogRepository {

    override fun observeLog(date: String): Flow<DailyLog?> =
        combine(
            dailyLogDao.observeLogByDate(date),
            foodDao.observeAllFoods(),
            foodDao.observeAllRecipes()
        ) { log, foods, recipes ->
            log?.toDomain(
                foodById = foods.associateBy { it.id },
                recipeById = recipes.associateBy { it.recipe.id }
            )
        }

    override fun observeLogsBetween(startDate: String, endDate: String): Flow<List<DailyLog>> =
        combine(
            dailyLogDao.observeLogsBetween(startDate, endDate),
            foodDao.observeAllFoods(),
            foodDao.observeAllRecipes()
        ) { logs, foods, recipes ->
            val foodById = foods.associateBy { it.id }
            val recipeById = recipes.associateBy { it.recipe.id }
            logs.map { it.toDomain(foodById, recipeById) }
        }

    override suspend fun startDayFromPlan(date: String, dayOfWeek: Int): Long? {
        if (dailyLogDao.getLogByDate(date) != null) return null
        val plan = dietDao.getActivePlanOnce() ?: return null
        val planDay = plan.days.firstOrNull { it.day.dayOfWeek == dayOfWeek } ?: return null

        val logId = dailyLogDao.insertLog(
            DailyLogEntity(
                date = date,
                dietPlanId = plan.plan.id,
                isTrainingDay = planDay.day.isTrainingDay
            )
        )

        planDay.meals
            .sortedBy { it.meal.mealOrder }
            .forEach { mealRelation ->
                val mealId = dailyLogDao.insertMeal(
                    DailyMealEntity(
                        dailyLogId = logId,
                        name = mealRelation.meal.name,
                        mealOrder = mealRelation.meal.mealOrder
                    )
                )
                mealRelation.items.forEach { item ->
                    dailyLogDao.insertEntry(
                        DailyFoodEntryEntity(
                            dailyMealId = mealId,
                            foodItemId = item.foodItemId,
                            recipeId = item.recipeId,
                            quantity = item.quantity,
                            loggedAt = Instant.now().toString()
                        )
                    )
                }
            }
        return logId
    }

    override suspend fun startEmptyDay(date: String): Long {
        dailyLogDao.getLogByDate(date)?.let { return it.log.id }
        val logId = dailyLogDao.insertLog(DailyLogEntity(date = date))
        listOf(
            "Desayuno", "Media mañana", "Almuerzo", "Merienda", "Cena", "Post-entreno"
        ).forEachIndexed { index, name ->
            dailyLogDao.insertMeal(
                DailyMealEntity(dailyLogId = logId, name = name, mealOrder = index)
            )
        }
        return logId
    }

    override suspend fun setMealCompleted(mealId: Long, completed: Boolean) =
        dailyLogDao.setMealCompleted(mealId, completed)

    override suspend fun setAdherence(logId: Long, adherence: AdherenceLevel) =
        dailyLogDao.setAdherence(logId, adherence.name)

    override suspend fun setTrainingDay(logId: Long, isTraining: Boolean) =
        dailyLogDao.setTrainingDay(logId, isTraining)

    override suspend fun addMeal(logId: Long, name: String, order: Int) {
        dailyLogDao.insertMeal(DailyMealEntity(dailyLogId = logId, name = name, mealOrder = order))
    }

    override suspend fun deleteMeal(mealId: Long) = dailyLogDao.deleteMeal(mealId)

    override suspend fun addFoodEntry(mealId: Long, foodId: Long, quantity: Double) {
        dailyLogDao.insertEntry(
            DailyFoodEntryEntity(
                dailyMealId = mealId,
                foodItemId = foodId,
                quantity = quantity,
                loggedAt = Instant.now().toString()
            )
        )
    }

    override suspend fun addRecipeEntry(mealId: Long, recipeId: Long, servings: Double) {
        dailyLogDao.insertEntry(
            DailyFoodEntryEntity(
                dailyMealId = mealId,
                recipeId = recipeId,
                quantity = servings,
                loggedAt = Instant.now().toString()
            )
        )
    }

    override suspend fun updateEntryQuantity(entryId: Long, quantity: Double) =
        dailyLogDao.updateEntryQuantity(entryId, quantity)

    override suspend fun deleteEntry(entryId: Long) = dailyLogDao.deleteEntry(entryId)

    // ── Mapping (same macro logic as the diet plan) ──

    private fun DailyLogWithMeals.toDomain(
        foodById: Map<Long, FoodItemEntity>,
        recipeById: Map<Long, RecipeWithIngredients>
    ) = DailyLog(
        id = log.id,
        date = log.date,
        isTrainingDay = log.isTrainingDay,
        adherence = runCatching { AdherenceLevel.valueOf(log.adherence) }
            .getOrDefault(AdherenceLevel.NOT_SET),
        notes = log.notes,
        meals = meals
            .sortedBy { it.meal.mealOrder }
            .map { mealRelation ->
                DailyMeal(
                    id = mealRelation.meal.id,
                    name = mealRelation.meal.name,
                    order = mealRelation.meal.mealOrder,
                    isCompleted = mealRelation.meal.isCompleted,
                    entries = mealRelation.entries.map { entry ->
                        entryToDomain(entry.id, entry.foodItemId, entry.recipeId,
                            entry.quantity, foodById, recipeById)
                    }
                )
            }
    )

    private fun entryToDomain(
        id: Long,
        foodItemId: Long?,
        recipeId: Long?,
        quantity: Double,
        foodById: Map<Long, FoodItemEntity>,
        recipeById: Map<Long, RecipeWithIngredients>
    ): PlannedItem {
        foodItemId?.let { foodId ->
            val food = foodById[foodId]
            val domainFood = food?.let {
                FoodItem(
                    id = it.id, name = it.name,
                    caloriesPer100 = it.caloriesPer100, proteinPer100 = it.proteinPer100,
                    carbsPer100 = it.carbsPer100, fatPer100 = it.fatPer100,
                    fiberPer100 = it.fiberPer100
                )
            }
            return PlannedItem(
                id = id,
                foodItemId = foodId,
                name = food?.name.orEmpty(),
                quantity = quantity,
                unit = food?.servingUnit ?: "g",
                macros = domainFood?.let { NutritionCalc.macrosFor(it, quantity) }
                    ?: MacroSummary()
            )
        }
        recipeId?.let { rId ->
            val recipe = recipeById[rId]
            val recipeTotal = recipe?.let { relation ->
                NutritionCalc.total(
                    relation.ingredients.mapNotNull { ingredient ->
                        foodById[ingredient.foodItemId]?.let { foodEntity ->
                            FoodItem(
                                id = foodEntity.id, name = foodEntity.name,
                                caloriesPer100 = foodEntity.caloriesPer100,
                                proteinPer100 = foodEntity.proteinPer100,
                                carbsPer100 = foodEntity.carbsPer100,
                                fatPer100 = foodEntity.fatPer100,
                                fiberPer100 = foodEntity.fiberPer100
                            ) to ingredient.quantity
                        }
                    }
                )
            } ?: MacroSummary()
            val servings = recipe?.recipe?.servings ?: 1
            val perServing = NutritionCalc.perServing(recipeTotal, servings)
            return PlannedItem(
                id = id,
                recipeId = rId,
                name = recipe?.recipe?.name.orEmpty(),
                quantity = quantity,
                unit = "porción",
                macros = MacroSummary(
                    calories = perServing.calories * quantity,
                    protein = perServing.protein * quantity,
                    carbs = perServing.carbs * quantity,
                    fat = perServing.fat * quantity,
                    fiber = perServing.fiber * quantity
                )
            )
        }
        return PlannedItem(id = id, quantity = quantity)
    }
}
