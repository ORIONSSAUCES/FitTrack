package com.brunoapp.fittrack.domain.model

import com.brunoapp.fittrack.core.constants.AdherenceLevel

data class DailyLog(
    val id: Long = 0,
    val date: String,                 // YYYY-MM-DD
    val isTrainingDay: Boolean = true,
    val adherence: AdherenceLevel = AdherenceLevel.NOT_SET,
    val notes: String = "",
    val meals: List<DailyMeal> = emptyList()
) {
    val totals: MacroSummary
        get() = meals.fold(MacroSummary()) { acc, meal -> acc + meal.totals }

    val completedMeals: Int get() = meals.count { it.isCompleted }
    val totalMeals: Int get() = meals.size
}

data class DailyMeal(
    val id: Long = 0,
    val name: String,
    val order: Int = 0,
    val isCompleted: Boolean = false,
    val entries: List<PlannedItem> = emptyList()
) {
    val totals: MacroSummary
        get() = entries.fold(MacroSummary()) { acc, entry -> acc + entry.macros }
}
