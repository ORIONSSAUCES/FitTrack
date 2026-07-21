package com.brunoapp.fittrack.domain.model

data class MacroGoals(
    val calories: Int = 2700,
    val protein: Double = 200.0,
    val carbs: Double = 280.0,
    val fat: Double = 75.0,
    val fiber: Double = 30.0,
    val waterMl: Int = 3000
)

data class DietPlan(
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val isActive: Boolean = false,
    val goalsTraining: MacroGoals = MacroGoals(),
    val goalsRest: MacroGoals = MacroGoals(calories = 2550, protein = 195.0, carbs = 250.0),
    val days: List<DietDay> = emptyList()
)

data class DietDay(
    val id: Long = 0,
    val dayOfWeek: Int,
    val isTrainingDay: Boolean = true,
    val meals: List<PlannedMeal> = emptyList()
) {
    val totals: MacroSummary
        get() = meals.fold(MacroSummary()) { acc, meal -> acc + meal.totals }
}

data class PlannedMeal(
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val notes: String = "",
    val items: List<PlannedItem> = emptyList()
) {
    val totals: MacroSummary
        get() = items.fold(MacroSummary()) { acc, item -> acc + item.macros }
}

data class PlannedItem(
    val id: Long = 0,
    val foodItemId: Long? = null,
    val recipeId: Long? = null,
    val name: String = "",
    val quantity: Double = 0.0,
    val unit: String = "g",           // "g", "ml" or "porción"
    val macros: MacroSummary = MacroSummary()
)
