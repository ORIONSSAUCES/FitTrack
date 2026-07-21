package com.brunoapp.fittrack.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class DietPlanTest {

    private fun item(calories: Double, protein: Double) = PlannedItem(
        name = "test", quantity = 100.0,
        macros = MacroSummary(calories = calories, protein = protein)
    )

    @Test
    fun `meal totals sum item macros`() {
        val meal = PlannedMeal(
            name = "Almuerzo",
            items = listOf(item(275.0, 57.5), item(252.0, 4.9))
        )
        assertEquals(527.0, meal.totals.calories, 0.01)
        assertEquals(62.4, meal.totals.protein, 0.01)
    }

    @Test
    fun `day totals sum meal totals`() {
        val day = DietDay(
            dayOfWeek = 0,
            meals = listOf(
                PlannedMeal(name = "Desayuno", items = listOf(item(700.0, 40.0))),
                PlannedMeal(name = "Almuerzo", items = listOf(item(800.0, 60.0))),
                PlannedMeal(name = "Cena", items = listOf(item(600.0, 50.0)))
            )
        )
        assertEquals(2100.0, day.totals.calories, 0.01)
        assertEquals(150.0, day.totals.protein, 0.01)
    }

    @Test
    fun `empty day has zero totals`() {
        val day = DietDay(dayOfWeek = 0)
        assertEquals(0.0, day.totals.calories, 0.001)
    }
}
