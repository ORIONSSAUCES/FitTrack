package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.entity.FoodItemEntity

/**
 * Base food library from Bruno's actual diet plan.
 * Macros per 100 g (or 100 ml). Values are standard references;
 * every food is editable to match local labels.
 */
object FoodSeed {

    fun all(): List<FoodItemEntity> = listOf(
        // ── Proteins ──
        FoodItemEntity(
            name = "Pechuga de pollo", brand = "",
            caloriesPer100 = 110.0, proteinPer100 = 23.0, carbsPer100 = 0.0,
            fatPer100 = 1.5, fiberPer100 = 0.0,
            servingSize = 250.0, servingUnit = "g", notes = "Pesar en crudo"
        ),
        FoodItemEntity(
            name = "Huevo entero", brand = "",
            caloriesPer100 = 143.0, proteinPer100 = 12.6, carbsPer100 = 1.1,
            fatPer100 = 9.5, fiberPer100 = 0.0,
            servingSize = 55.0, servingUnit = "g", notes = "1 unidad ≈ 55 g"
        ),
        FoodItemEntity(
            name = "Atún al agua (escurrido)", brand = "",
            caloriesPer100 = 116.0, proteinPer100 = 26.0, carbsPer100 = 0.0,
            fatPer100 = 1.0, fiberPer100 = 0.0,
            servingSize = 160.0, servingUnit = "g", notes = "Pesar escurrido. Preferir atún claro"
        ),
        FoodItemEntity(
            name = "Sardinas (escurridas)", brand = "",
            caloriesPer100 = 208.0, proteinPer100 = 25.0, carbsPer100 = 0.0,
            fatPer100 = 11.5, fiberPer100 = 0.0,
            servingSize = 165.0, servingUnit = "g",
            notes = "Pesar escurridas. Más grasa que el atún: ese día sin aceite extra. Las espinas blandas aportan calcio"
        ),
        FoodItemEntity(
            name = "Proteína en polvo (whey)", brand = "",
            caloriesPer100 = 375.0, proteinPer100 = 80.0, carbsPer100 = 8.0,
            fatPer100 = 5.0, fiberPer100 = 0.0,
            servingSize = 30.0, servingUnit = "g", notes = "1 scoop ≈ 30 g"
        ),
        FoodItemEntity(
            name = "Yogur proteico", brand = "Trébol",
            caloriesPer100 = 60.0, proteinPer100 = 8.0, carbsPer100 = 6.0,
            fatPer100 = 0.5, fiberPer100 = 0.0,
            servingSize = 200.0, servingUnit = "g", notes = "Ajustar según etiqueta del envase"
        ),

        // ── Carbs ──
        FoodItemEntity(
            name = "Avena", brand = "",
            caloriesPer100 = 372.0, proteinPer100 = 13.5, carbsPer100 = 58.0,
            fatPer100 = 7.0, fiberPer100 = 10.0,
            servingSize = 60.0, servingUnit = "g", notes = "Pesar en crudo"
        ),
        FoodItemEntity(
            name = "Arroz blanco", brand = "",
            caloriesPer100 = 360.0, proteinPer100 = 7.0, carbsPer100 = 79.0,
            fatPer100 = 0.6, fiberPer100 = 1.3,
            servingSize = 70.0, servingUnit = "g", notes = "Pesar en crudo"
        ),
        FoodItemEntity(
            name = "Papa cocida", brand = "",
            caloriesPer100 = 87.0, proteinPer100 = 1.9, carbsPer100 = 20.0,
            fatPer100 = 0.1, fiberPer100 = 1.8,
            servingSize = 300.0, servingUnit = "g", notes = "Pesar cocida"
        ),
        FoodItemEntity(
            name = "Batata cocida", brand = "",
            caloriesPer100 = 90.0, proteinPer100 = 2.0, carbsPer100 = 21.0,
            fatPer100 = 0.2, fiberPer100 = 3.3,
            servingSize = 300.0, servingUnit = "g", notes = "Pesar cocida"
        ),
        FoodItemEntity(
            name = "Porotos cocidos", brand = "",
            caloriesPer100 = 127.0, proteinPer100 = 8.7, carbsPer100 = 22.8,
            fatPer100 = 0.5, fiberPer100 = 6.4,
            servingSize = 150.0, servingUnit = "g", notes = "Pesar cocidos"
        ),
        FoodItemEntity(
            name = "Lentejas cocidas", brand = "",
            caloriesPer100 = 116.0, proteinPer100 = 9.0, carbsPer100 = 20.0,
            fatPer100 = 0.4, fiberPer100 = 7.9,
            servingSize = 150.0, servingUnit = "g", notes = "Pesar cocidas"
        ),
        FoodItemEntity(
            name = "Banana", brand = "",
            caloriesPer100 = 89.0, proteinPer100 = 1.1, carbsPer100 = 22.8,
            fatPer100 = 0.3, fiberPer100 = 2.6,
            servingSize = 120.0, servingUnit = "g", notes = "1 unidad mediana ≈ 120 g"
        ),
        FoodItemEntity(
            name = "Manzana", brand = "",
            caloriesPer100 = 52.0, proteinPer100 = 0.3, carbsPer100 = 14.0,
            fatPer100 = 0.2, fiberPer100 = 2.4,
            servingSize = 150.0, servingUnit = "g", notes = "1 unidad mediana ≈ 150 g"
        ),

        // ── Dairy / liquids ──
        FoodItemEntity(
            name = "Leche entera", brand = "",
            caloriesPer100 = 61.0, proteinPer100 = 3.1, carbsPer100 = 4.6,
            fatPer100 = 3.2, fiberPer100 = 0.0,
            servingSize = 200.0, servingUnit = "ml", notes = ""
        ),

        // ── Vegetables ──
        FoodItemEntity(
            name = "Tomate", brand = "",
            caloriesPer100 = 18.0, proteinPer100 = 0.9, carbsPer100 = 3.9,
            fatPer100 = 0.2, fiberPer100 = 1.2,
            servingSize = 200.0, servingUnit = "g", notes = ""
        ),
        FoodItemEntity(
            name = "Pepino", brand = "",
            caloriesPer100 = 15.0, proteinPer100 = 0.7, carbsPer100 = 3.6,
            fatPer100 = 0.1, fiberPer100 = 0.5,
            servingSize = 150.0, servingUnit = "g", notes = ""
        ),

        // ── Condiments / supplements ──
        FoodItemEntity(
            name = "Mayonesa Light", brand = "Hellmann's",
            caloriesPer100 = 233.0, proteinPer100 = 0.7, carbsPer100 = 6.0,
            fatPer100 = 22.0, fiberPer100 = 0.0,
            servingSize = 15.0, servingUnit = "g",
            notes = "PESAR siempre: del envase directo se duplica fácil. ~35 kcal por 15 g. Verificar etiqueta local"
        ),
        FoodItemEntity(
            name = "Cacao amargo", brand = "",
            caloriesPer100 = 228.0, proteinPer100 = 19.6, carbsPer100 = 57.9,
            fatPer100 = 13.7, fiberPer100 = 33.0,
            servingSize = 10.0, servingUnit = "g", notes = ""
        ),
        FoodItemEntity(
            name = "Creatina monohidrato", brand = "",
            caloriesPer100 = 0.0, proteinPer100 = 0.0, carbsPer100 = 0.0,
            fatPer100 = 0.0, fiberPer100 = 0.0,
            servingSize = 5.0, servingUnit = "g", notes = "5 g diarios, cualquier momento del día"
        )
    )
}
