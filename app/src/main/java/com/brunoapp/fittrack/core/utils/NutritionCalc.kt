package com.brunoapp.fittrack.core.utils

import com.brunoapp.fittrack.domain.model.FoodItem
import com.brunoapp.fittrack.domain.model.MacroSummary

/** Pure nutrition math. Unit-tested. */
object NutritionCalc {

    /** Macros for [quantity] (g/ml) of a food whose values are per 100. */
    fun macrosFor(food: FoodItem, quantity: Double): MacroSummary {
        if (quantity <= 0.0) return MacroSummary()
        val factor = quantity / 100.0
        return MacroSummary(
            calories = food.caloriesPer100 * factor,
            protein = food.proteinPer100 * factor,
            carbs = food.carbsPer100 * factor,
            fat = food.fatPer100 * factor,
            fiber = food.fiberPer100 * factor
        )
    }

    /** Total macros of a list of (food, quantity) pairs. */
    fun total(items: List<Pair<FoodItem, Double>>): MacroSummary =
        items.fold(MacroSummary()) { acc, (food, quantity) ->
            acc + macrosFor(food, quantity)
        }

    /** Per-serving macros of a recipe total. */
    fun perServing(total: MacroSummary, servings: Int): MacroSummary {
        if (servings <= 0) return total
        return MacroSummary(
            calories = total.calories / servings,
            protein = total.protein / servings,
            carbs = total.carbs / servings,
            fat = total.fat / servings,
            fiber = total.fiber / servings
        )
    }
}
