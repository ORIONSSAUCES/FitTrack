package com.brunoapp.fittrack.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A food with macros per 100 g (or 100 ml for liquids).
 * [servingSize] is the usual portion in [servingUnit] used to prefill quantities.
 */
@Entity(tableName = "food_item")
data class FoodItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val brand: String = "",
    val caloriesPer100: Double = 0.0,
    val proteinPer100: Double = 0.0,
    val carbsPer100: Double = 0.0,
    val fatPer100: Double = 0.0,
    val fiberPer100: Double = 0.0,
    val servingSize: Double = 100.0,
    val servingUnit: String = "g",     // "g" | "ml"
    val notes: String = "",            // e.g. "pesar en crudo", "escurrido"
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false,
    val lastUsed: String? = null
)
