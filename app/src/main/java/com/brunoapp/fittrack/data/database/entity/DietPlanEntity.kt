package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "diet_plan")
data class DietPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val isActive: Boolean = false,
    // Training-day goals
    val caloriesTraining: Int = 2700,
    val proteinTraining: Double = 200.0,
    val carbsTraining: Double = 280.0,
    val fatTraining: Double = 75.0,
    val fiberTraining: Double = 30.0,
    val waterTrainingMl: Int = 3000,
    // Rest-day goals
    val caloriesRest: Int = 2550,
    val proteinRest: Double = 195.0,
    val carbsRest: Double = 250.0,
    val fatRest: Double = 75.0,
    val fiberRest: Double = 30.0,
    val waterRestMl: Int = 3000,
    val createdAt: String = ""
)

@Serializable
@Entity(
    tableName = "diet_plan_day",
    foreignKeys = [
        ForeignKey(
            entity = DietPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["dietPlanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dietPlanId")]
)
data class DietPlanDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dietPlanId: Long,
    val dayOfWeek: Int,               // 0 = Monday … 6 = Sunday
    val isTrainingDay: Boolean = true
)

@Serializable
@Entity(
    tableName = "planned_meal",
    foreignKeys = [
        ForeignKey(
            entity = DietPlanDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dietPlanDayId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dietPlanDayId")]
)
data class PlannedMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dietPlanDayId: Long,
    val name: String,
    val mealOrder: Int = 0,
    val notes: String = ""
)

@Serializable
@Entity(
    tableName = "planned_meal_item",
    foreignKeys = [
        ForeignKey(
            entity = PlannedMealEntity::class,
            parentColumns = ["id"],
            childColumns = ["plannedMealId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = FoodItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["foodItemId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RecipeEntity::class,
            parentColumns = ["id"],
            childColumns = ["recipeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("plannedMealId"), Index("foodItemId"), Index("recipeId")]
)
data class PlannedMealItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val plannedMealId: Long,
    val foodItemId: Long? = null,     // exactly one of foodItemId/recipeId is set
    val recipeId: Long? = null,
    val quantity: Double              // g/ml for foods; number of servings for recipes
)
