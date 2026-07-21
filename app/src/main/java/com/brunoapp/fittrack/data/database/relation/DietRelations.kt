package com.brunoapp.fittrack.data.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.brunoapp.fittrack.data.database.entity.DietPlanDayEntity
import com.brunoapp.fittrack.data.database.entity.DietPlanEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealEntity
import com.brunoapp.fittrack.data.database.entity.PlannedMealItemEntity

data class MealWithItems(
    @Embedded val meal: PlannedMealEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "plannedMealId"
    )
    val items: List<PlannedMealItemEntity>
)

data class DayWithMeals(
    @Embedded val day: DietPlanDayEntity,
    @Relation(
        entity = PlannedMealEntity::class,
        parentColumn = "id",
        entityColumn = "dietPlanDayId"
    )
    val meals: List<MealWithItems>
)

data class PlanWithDays(
    @Embedded val plan: DietPlanEntity,
    @Relation(
        entity = DietPlanDayEntity::class,
        parentColumn = "id",
        entityColumn = "dietPlanId"
    )
    val days: List<DayWithMeals>
)
