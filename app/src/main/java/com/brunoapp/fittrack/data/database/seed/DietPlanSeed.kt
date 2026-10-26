package com.brunoapp.fittrack.data.database.seed

/**
 * Economic daily diet (based on Bruno's structure, adjusted to his volume goal).
 * Foods in g/ml. Daily totals ≈ 2,450–2,530 kcal · 205–217 g protein ·
 * ~228 g carbs · 72–85 g fat.
 */
object DietPlanSeed {

    const val PLAN_NAME = "Dieta económica — Volumen"
    const val PLAN_DESCRIPTION =
        "Pollo y carne en crudo; arroz COCIDO; atún y sardinas escurridos. " +
            "Sardinas 5 días y atún 2 (con la carne, para no sumar grasa). " +
            "Medir el aceite con cuchara. Si el peso no sube en 2-3 semanas: +1 banana o +50 g de arroz cocido."

    // Goals aligned with what the plan actually delivers
    const val CALORIES = 2500
    const val PROTEIN = 205.0
    const val CARBS = 230.0
    const val FAT = 80.0
    const val FIBER = 25.0
    const val WATER_ML = 3000

    data class SeedItem(val name: String, val quantity: Double, val isRecipe: Boolean = false)
    data class SeedMeal(val name: String, val items: List<SeedItem>)
    data class SeedDay(val dayOfWeek: Int, val isTraining: Boolean, val meals: List<SeedMeal>)

    private fun breakfast() = SeedMeal("Desayuno", listOf(
        SeedItem("Huevo entero", 220.0),       // 4 huevos
        SeedItem("Leche entera", 240.0),       // 1 taza
        SeedItem("Avena", 70.0)
    ))

    private fun lunch(beef: Boolean, chard: Boolean) = SeedMeal("Almuerzo", listOf(
        if (beef) SeedItem("Carne vacuna magra", 200.0) else SeedItem("Pechuga de pollo", 225.0),
        SeedItem("Arroz blanco cocido", 250.0),
        if (chard) SeedItem("Acelga", 150.0) else SeedItem("Repollo", 150.0),
        SeedItem("Zanahoria", 100.0),
        SeedItem("Aceite vegetal", 10.0)
    ))

    private fun shake(training: Boolean) = SeedMeal(
        if (training) "Merienda (pre/post entreno)" else "Merienda",
        listOf(
            SeedItem("Leche entera", 200.0),
            SeedItem("Avena", 40.0),
            SeedItem("Banana", 120.0),
            SeedItem("Proteína en polvo (whey)", 30.0),
            SeedItem("Creatina monohidrato", 5.0)
        )
    )

    private fun snack() = SeedMeal("Media tarde", listOf(
        SeedItem("Yogur proteico", 200.0)
    ))

    private fun dinner(tuna: Boolean) = SeedMeal("Cena", listOf(
        if (tuna) SeedItem("Atún al agua (escurrido)", 160.0)        // 2 latas
        else SeedItem("Sardinas (escurridas)", 125.0),               // ~1,5 latas
        SeedItem("Huevo entero", 110.0),                             // 2 huevos
        SeedItem("Tomate", 150.0),
        SeedItem("Pepino", 100.0)
    ))

    private fun day(dow: Int, training: Boolean, beef: Boolean, chard: Boolean) = SeedDay(
        dayOfWeek = dow,
        isTraining = training,
        meals = listOf(
            breakfast(),
            lunch(beef = beef, chard = chard),
            shake(training),
            snack(),
            dinner(tuna = beef)
        )
    )

    fun days(): List<SeedDay> = listOf(
        day(0, training = true, beef = false, chard = false),  // Lunes
        day(1, training = true, beef = false, chard = true),   // Martes
        day(2, training = true, beef = true, chard = false),   // Miércoles: carne + atún
        day(3, training = true, beef = false, chard = true),   // Jueves
        day(4, training = true, beef = false, chard = false),  // Viernes
        day(5, training = false, beef = true, chard = true),   // Sábado: carne + atún
        day(6, training = false, beef = false, chard = false)  // Domingo
    )
}
