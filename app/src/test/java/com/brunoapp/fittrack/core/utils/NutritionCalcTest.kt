package com.brunoapp.fittrack.core.utils

import com.brunoapp.fittrack.domain.model.FoodItem
import org.junit.Assert.assertEquals
import org.junit.Test

class NutritionCalcTest {

    private val chicken = FoodItem(
        id = 1, name = "Pechuga de pollo",
        caloriesPer100 = 110.0, proteinPer100 = 23.0,
        carbsPer100 = 0.0, fatPer100 = 1.5, fiberPer100 = 0.0
    )
    private val rice = FoodItem(
        id = 2, name = "Arroz",
        caloriesPer100 = 360.0, proteinPer100 = 7.0,
        carbsPer100 = 79.0, fatPer100 = 0.6, fiberPer100 = 1.3
    )

    @Test
    fun `macros scale linearly with quantity`() {
        val macros = NutritionCalc.macrosFor(chicken, 250.0)
        assertEquals(275.0, macros.calories, 0.01)
        assertEquals(57.5, macros.protein, 0.01)
        assertEquals(3.75, macros.fat, 0.01)
    }

    @Test
    fun `zero or negative quantity gives empty macros`() {
        assertEquals(0.0, NutritionCalc.macrosFor(chicken, 0.0).calories, 0.001)
        assertEquals(0.0, NutritionCalc.macrosFor(chicken, -50.0).calories, 0.001)
    }

    @Test
    fun `total sums multiple foods`() {
        val total = NutritionCalc.total(listOf(chicken to 250.0, rice to 70.0))
        // 275 + 252 = 527
        assertEquals(527.0, total.calories, 0.01)
        // 57.5 + 4.9 = 62.4
        assertEquals(62.4, total.protein, 0.01)
    }

    @Test
    fun `per serving divides by servings`() {
        val total = NutritionCalc.total(listOf(rice to 200.0))
        val perServing = NutritionCalc.perServing(total, 2)
        assertEquals(360.0, perServing.calories, 0.01)
    }

    @Test
    fun `per serving with invalid servings returns total`() {
        val total = NutritionCalc.total(listOf(rice to 100.0))
        assertEquals(total.calories, NutritionCalc.perServing(total, 0).calories, 0.001)
    }

    @Test
    fun `macro summary plus operator`() {
        val a = NutritionCalc.macrosFor(chicken, 100.0)
        val b = NutritionCalc.macrosFor(rice, 100.0)
        val sum = a + b
        assertEquals(470.0, sum.calories, 0.01)
        assertEquals(30.0, sum.protein, 0.01)
    }
}
