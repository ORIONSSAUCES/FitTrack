package com.brunoapp.fittrack.data.database.seed

/**
 * Bruno's real weekly diet. Items reference foods/recipes by NAME;
 * the seeder resolves ids. Quantities: foods in g/ml, recipes in servings.
 *
 * Daily targets: 2,550–2,700 kcal · 190–210 g protein · 70–80 g fat ·
 * 250–290 g carbs · ≥30 g fiber · ~3 L water.
 */
object DietPlanSeed {

    const val PLAN_NAME = "Dieta base — Volumen"
    const val PLAN_DESCRIPTION =
        "Pollo, arroz y avena en crudo; atún y sardinas escurridos; legumbres y papa cocidas. " +
        "Atún 2x/semana, sardinas 2x/semana. Pesar SIEMPRE la mayonesa."

    data class SeedItem(val name: String, val quantity: Double, val isRecipe: Boolean = false)
    data class SeedMeal(val name: String, val items: List<SeedItem>)
    data class SeedDay(val dayOfWeek: Int, val isTraining: Boolean, val meals: List<SeedMeal>)

    private fun ensaladaBase(mayo: Double = 15.0) = listOf(
        SeedItem("Tomate", 200.0),
        SeedItem("Pepino", 150.0),
        SeedItem("Mayonesa Light", mayo)
    )

    fun days(): List<SeedDay> = listOf(
        // ── LUNES ──
        SeedDay(0, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Waffles de banana", 1.0, isRecipe = true)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Yogur proteico", 200.0),
                SeedItem("Manzana", 150.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Arroz blanco", 70.0),
                SeedItem("Porotos cocidos", 150.0)
            ) + ensaladaBase()),
            SeedMeal("Pre-entreno", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Banana", 120.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Pechuga de pollo", 200.0),
                SeedItem("Papa cocida", 300.0)
            ) + ensaladaBase())
        )),

        // ── MARTES ──
        SeedDay(1, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Avena overnight", 1.0, isRecipe = true)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Huevo entero", 165.0),
                SeedItem("Manzana", 150.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Atún al agua (escurrido)", 160.0),
                SeedItem("Arroz blanco", 70.0),
                SeedItem("Lentejas cocidas", 200.0)
            ) + ensaladaBase()),
            SeedMeal("Pre-entreno", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Papa cocida", 325.0)
            ) + ensaladaBase())
        )),

        // ── MIÉRCOLES ──
        SeedDay(2, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Postre de huevo y cacao", 1.0, isRecipe = true)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Yogur proteico", 200.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Arroz blanco", 70.0),
                SeedItem("Porotos cocidos", 150.0)
            ) + ensaladaBase()),
            SeedMeal("Pre-entreno", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Manzana", 150.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Sardinas (escurridas)", 165.0),
                SeedItem("Papa cocida", 300.0),
                SeedItem("Lentejas cocidas", 150.0)
            ) + ensaladaBase(mayo = 10.0))
        )),

        // ── JUEVES ──
        SeedDay(3, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Waffles de cacao", 1.0, isRecipe = true)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Yogur proteico", 200.0),
                SeedItem("Manzana", 150.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Arroz blanco", 80.0),
                SeedItem("Lentejas cocidas", 150.0)
            ) + ensaladaBase()),
            SeedMeal("Pre-entreno", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Banana", 120.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Pechuga de pollo", 150.0),
                SeedItem("Huevo entero", 110.0),
                SeedItem("Papa cocida", 250.0)
            ) + ensaladaBase())
        )),

        // ── VIERNES ──
        SeedDay(4, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Avena", 60.0),
                SeedItem("Leche entera", 250.0),
                SeedItem("Huevo entero", 165.0),
                SeedItem("Banana", 120.0)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Yogur proteico", 200.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Sardinas (escurridas)", 165.0),
                SeedItem("Arroz blanco", 70.0),
                SeedItem("Porotos cocidos", 150.0)
            ) + ensaladaBase(mayo = 10.0)),
            SeedMeal("Pre-entreno", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Manzana", 150.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Papa cocida", 300.0)
            ) + ensaladaBase())
        )),

        // ── SÁBADO ──
        SeedDay(5, true, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Waffles de banana", 1.0, isRecipe = true)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Yogur proteico", 200.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Pechuga de pollo", 250.0),
                SeedItem("Arroz blanco", 80.0),
                SeedItem("Porotos cocidos", 150.0)
            ) + ensaladaBase()),
            SeedMeal("Merienda", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Manzana", 150.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Atún al agua (escurrido)", 160.0),
                SeedItem("Huevo entero", 110.0),
                SeedItem("Papa cocida", 300.0)
            ) + ensaladaBase())
        )),

        // ── DOMINGO (descanso) ──
        SeedDay(6, false, listOf(
            SeedMeal("Desayuno", listOf(
                SeedItem("Huevo entero", 110.0),
                SeedItem("Yogur proteico", 200.0),
                SeedItem("Leche entera", 200.0),
                SeedItem("Avena", 40.0),
                SeedItem("Cacao amargo", 10.0),
                SeedItem("Banana", 120.0)
            )),
            SeedMeal("Media mañana", listOf(
                SeedItem("Manzana", 150.0)
            )),
            SeedMeal("Almuerzo", listOf(
                SeedItem("Pechuga de pollo", 275.0),
                SeedItem("Arroz blanco", 80.0)
            ) + ensaladaBase()),
            SeedMeal("Merienda", listOf(
                SeedItem("Proteína en polvo (whey)", 30.0),
                SeedItem("Creatina monohidrato", 5.0)
            )),
            SeedMeal("Cena", listOf(
                SeedItem("Pechuga de pollo", 200.0),
                SeedItem("Lentejas cocidas", 150.0),
                SeedItem("Papa cocida", 250.0),
                SeedItem("Tomate", 200.0),
                SeedItem("Pepino", 150.0)
            ))
        ))
    )
}
