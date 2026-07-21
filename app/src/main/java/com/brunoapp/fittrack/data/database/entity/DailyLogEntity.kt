package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_log",
    foreignKeys = [
        ForeignKey(
            entity = DietPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["dietPlanId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index(value = ["date"], unique = true), Index("dietPlanId")]
)
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,                  // YYYY-MM-DD
    val dietPlanId: Long? = null,
    val isTrainingDay: Boolean = true,
    val adherence: String = "NOT_SET", // AdherenceLevel enum name
    val notes: String = ""
)

@Entity(
    tableName = "daily_meal",
    foreignKeys = [
        ForeignKey(
            entity = DailyLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dailyLogId")]
)
data class DailyMealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyLogId: Long,
    val name: String,
    val mealOrder: Int = 0,
    val isCompleted: Boolean = false
)

@Entity(
    tableName = "daily_food_entry",
    foreignKeys = [
        ForeignKey(
            entity = DailyMealEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyMealId"],
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
    indices = [Index("dailyMealId"), Index("foodItemId"), Index("recipeId")]
)
data class DailyFoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dailyMealId: Long,
    val foodItemId: Long? = null,
    val recipeId: Long? = null,
    val quantity: Double,
    val loggedAt: String = ""
)
