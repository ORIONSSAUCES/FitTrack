package com.brunoapp.fittrack.data.repository

import com.brunoapp.fittrack.core.utils.NutritionCalc
import com.brunoapp.fittrack.data.database.dao.DietDao
import com.brunoapp.fittrack.data.database.dao.FoodDao
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.FoodItemEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.relation.PlanWithDays
import com.brunoapp.fittrack.data.database.relation.RecipeWithIngredients
import com.brunoapp.fittrack.domain.model.DietDay
import com.brunoapp.fittrack.domain.model.DietPlan
import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.MacroGoals
import com.brunoapp.fittrack.domain.model.MacroSummary
import com.brunoapp.fittrack.domain.model.PlannedItem
import com.brunoapp.fittrack.domain.model.PlannedMeal
import com.brunoapp.fittrack.domain.repository.DietRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class DietRepositoryImpl @Inject constructor(
    private val dietDao: DietDao,
    private val foodDao: FoodDao
) : DietRepository {

    override fun observePlans(): Flow<List<DietPlan>> =
        dietDao.observePlans().map { plans ->
            plans.map { entity -> entity.toDomainMeta() }
        }

    override fun observeActivePlan(): Flow<DietPlan?> =
        combine(
            dietDao.observeActivePlan(),
            foodDao.observeAllFoods(),
            foodDao.observeAllRecipes()
        ) { plan, foods, recipes ->
            plan?.toDomainFull(
                foodById = foods.associateBy { it.id },
                recipeById = recipes.associateBy { it.recipe.id }
            )
        }

    override suspend fun createPlan(name: String): Long {
        val isFirst = dietDao.countPlans() == 0
        val planId = dietDao.insertPlan(
            DietPlanEntity(
                name = name,
                isActive = isFirst,
                createdAt = Instant.now().toString()
            )
        )
        (0..6).forEach { day ->
            dietDao.insertDay(
                DietPlanDayEntity(dietPlanId = planId, dayOfWeek = day, isTrainingDay = day != 6)
            )
        }
        return planId
    }

    override suspend fun deletePlan(id: Long) = dietDao.deletePlan(id)

    override suspend fun setActivePlan(id: Long) = dietDao.setActivePlan(id)

    override suspend fun updateGoals(planId: Long, training: MacroGoals, rest: MacroGoals) {
        val plan = dietDao.getPlanEntity(planId) ?: return
        dietDao.updatePlan(
            plan.copy(
                caloriesTraining = training.calories,
                proteinTraining = training.protein,
                carbsTraining = training.carbs,
                fatTraining = training.fat,
                fiberTraining = training.fiber,
                waterTrainingMl = training.waterMl,
                caloriesRest = rest.calories,
                proteinRest = rest.protein,
                carbsRest = rest.carbs,
                fatRest = rest.fat,
                fiberRest = rest.fiber,
                waterRestMl = rest.waterMl
            )
        )
    }

    override suspend fun setTrainingDay(dayId: Long, isTraining: Boolean) =
        dietDao.setTrainingDay(dayId, isTraining)

    override suspend fun addMeal(dayId: Long, name: String, order: Int) {
        dietDao.insertMeal(PlannedMealEntity(dietPlanDayId = dayId, name = name, mealOrder = order))
    }

    override suspend fun renameMeal(mealId: Long, name: String) =
        dietDao.renameMeal(mealId, name)

    override suspend fun deleteMeal(mealId: Long) = dietDao.deleteMeal(mealId)

    override suspend fun addFoodItem(mealId: Long, foodId: Long, quantity: Double) {
        dietDao.insertItem(
            PlannedMealItemEntity(plannedMealId = mealId, foodItemId = foodId, quantity = quantity)
        )
    }

    override suspend fun addRecipeItem(mealId: Long, recipeId: Long, servings: Double) {
        dietDao.insertItem(
            PlannedMealItemEntity(plannedMealId = mealId, recipeId = recipeId, quantity = servings)
        )
    }

    override suspend fun updateItemQuantity(itemId: Long, quantity: Double) =
        dietDao.updateItemQuantity(itemId, quantity)

    override suspend fun deleteItem(itemId: Long) = dietDao.deleteItem(itemId)

    // ── Mapping ──

    private fun DietPlanEntity.toDomainMeta() = DietPlan(
        id = id,
        name = name,
        description = description,
        isActive = isActive,
        goalsTraining = trainingGoals(),
        goalsRest = restGoals()
    )

    private fun PlanWithDays.toDomainFull(
        foodById: Map<Long, FoodItemEntity>,
        recipeById: Map<Long, RecipeWithIngredients>
    ) = DietPlan(
        id = plan.id,
        name = plan.name,
        description = plan.description,
        isActive = plan.isActive,
        goalsTraining = plan.trainingGoals(),
        goalsRest = plan.restGoals(),
        days = days
            .sortedBy { it.day.dayOfWeek }
            .map { dayRelation ->
                DietDay(
                    id = dayRelation.day.id,
                    dayOfWeek = dayRelation.day.dayOfWeek,
                    isTrainingDay = dayRelation.day.isTrainingDay,
                    meals = dayRelation.meals
                        .sortedBy { it.meal.mealOrder }
                        .map { mealRelation ->
                            PlannedMeal(
                                id = mealRelation.meal.id,
                                name = mealRelation.meal.name,
                                order = mealRelation.meal.mealOrder,
                                notes = mealRelation.meal.notes,
                                items = mealRelation.items.map { item ->
                                    item.toDomain(foodById, recipeById)
                                }
                            )
                        }
                )
            }
    )

    private fun PlannedMealItemEntity.toDomain(
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

    private fun DietPlanEntity.trainingGoals() = MacroGoals(
        calories = caloriesTraining, protein = proteinTraining, carbs = carbsTraining,
        fat = fatTraining, fiber = fiberTraining, waterMl = waterTrainingMl
    )

    private fun DietPlanEntity.restGoals() = MacroGoals(
        calories = caloriesRest, protein = proteinRest, carbs = carbsRest,
        fat = fatRest, fiber = fiberRest, waterMl = waterRestMl
    )
}
