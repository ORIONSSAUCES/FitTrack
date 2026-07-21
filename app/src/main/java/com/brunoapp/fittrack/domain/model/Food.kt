package com.brunoapp.fittrack.domain.model

data class FoodItem(
    val id: Long = 0,
    val name: String,
    val brand: String = "",
    val caloriesPer100: Double = 0.0,
    val proteinPer100: Double = 0.0,
    val carbsPer100: Double = 0.0,
    val fatPer100: Double = 0.0,
    val fiberPer100: Double = 0.0,
    val servingSize: Double = 100.0,
    val servingUnit: String = "g",
    val notes: String = "",
    val isCustom: Boolean = false,
    val isFavorite: Boolean = false
)

data class Recipe(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val servings: Int = 1,
    val ingredients: List<RecipeIngredient> = emptyList()
)

data class RecipeIngredient(
    val id: Long = 0,
    val foodItemId: Long,
    val foodName: String = "",
    val quantity: Double,
    val unit: String = "g"
)

/** Macro totals for any quantity of food, a recipe, or a full day. */
data class MacroSummary(
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbs: Double = 0.0,
    val fat: Double = 0.0,
    val fiber: Double = 0.0
) {
    operator fun plus(other: MacroSummary) = MacroSummary(
        calories = calories + other.calories,
        protein = protein + other.protein,
        carbs = carbs + other.carbs,
        fat = fat + other.fat,
        fiber = fiber + other.fiber
    )
}
