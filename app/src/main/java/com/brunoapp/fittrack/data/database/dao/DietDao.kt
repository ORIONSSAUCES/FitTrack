package com.brunoapp.fittrack.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity
import com.brunoapp.fittrack.data.database.relation.PlanWithDays
import kotlinx.coroutines.flow.Flow

@Dao
interface DietDao {

    // ── Plans ──

    @Query("SELECT COUNT(*) FROM diet_plan")
    suspend fun countPlans(): Int

    @Query("SELECT * FROM diet_plan ORDER BY name COLLATE NOCASE")
    fun observePlans(): Flow<List<DietPlanEntity>>

    @Transaction
    @Query("SELECT * FROM diet_plan WHERE isActive = 1 LIMIT 1")
    fun observeActivePlan(): Flow<PlanWithDays?>

    @Transaction
    @Query("SELECT * FROM diet_plan WHERE isActive = 1 LIMIT 1")
    suspend fun getActivePlanOnce(): PlanWithDays?

    @Transaction
    @Query("SELECT * FROM diet_plan WHERE id = :id")
    suspend fun getPlan(id: Long): PlanWithDays?

    @Insert
    suspend fun insertPlan(plan: DietPlanEntity): Long

    @Update
    suspend fun updatePlan(plan: DietPlanEntity)

    @Query("SELECT * FROM diet_plan WHERE id = :id")
    suspend fun getPlanEntity(id: Long): DietPlanEntity?

    @Query("DELETE FROM diet_plan WHERE id = :id")
    suspend fun deletePlan(id: Long)

    @Query("UPDATE diet_plan SET isActive = 0")
    suspend fun deactivateAll()

    @Query("UPDATE diet_plan SET isActive = 1 WHERE id = :id")
    suspend fun activate(id: Long)

    @Transaction
    suspend fun setActivePlan(id: Long) {
        deactivateAll()
        activate(id)
    }

    // ── Days ──

    @Insert
    suspend fun insertDay(day: DietPlanDayEntity): Long

    @Query("UPDATE diet_plan_day SET isTrainingDay = :isTraining WHERE id = :dayId")
    suspend fun setTrainingDay(dayId: Long, isTraining: Boolean)

    // ── Meals ──

    @Insert
    suspend fun insertMeal(meal: PlannedMealEntity): Long

    @Query("UPDATE planned_meal SET name = :name WHERE id = :mealId")
    suspend fun renameMeal(mealId: Long, name: String)

    @Query("DELETE FROM planned_meal WHERE id = :mealId")
    suspend fun deleteMeal(mealId: Long)

    // ── Items ──

    @Insert
    suspend fun insertItem(item: PlannedMealItemEntity): Long

    @Query("UPDATE planned_meal_item SET quantity = :quantity WHERE id = :itemId")
    suspend fun updateItemQuantity(itemId: Long, quantity: Double)

    @Query("DELETE FROM planned_meal_item WHERE id = :itemId")
    suspend fun deleteItem(itemId: Long)
}
