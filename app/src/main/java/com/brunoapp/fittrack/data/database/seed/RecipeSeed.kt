package com.brunoapp.fittrack.data.database.seed

/**
 * Bruno's breakfast recipes. Ingredients reference foods by NAME;
 * the seeder resolves ids after foods are inserted.
 */
object RecipeSeed {

    data class SeedRecipe(
        val name: String,
        val description: String,
        val ingredients: List<Pair<String, Double>>  // food name → quantity (g/ml)
    )

    fun all(): List<SeedRecipe> = listOf(
        SeedRecipe(
            name = "Waffles de banana",
            description = "Licuar todo y hacer en wafflera o sartén. Canela, vainilla y edulcorante al gusto.",
            ingredients = listOf(
                "Avena" to 60.0,
                "Huevo entero" to 165.0,
                "Banana" to 120.0,
                "Leche entera" to 200.0
            )
        ),
        SeedRecipe(
            name = "Waffles de cacao",
            description = "Igual que los de banana pero con cacao amargo. Edulcorante al gusto.",
            ingredients = listOf(
                "Avena" to 60.0,
                "Huevo entero" to 165.0,
                "Leche entera" to 200.0,
                "Cacao amargo" to 10.0,
                "Banana" to 120.0
            )
        ),
        SeedRecipe(
            name = "Avena overnight",
            description = "Mezclar todo y dejar en la heladera durante la noche. Canela al gusto.",
            ingredients = listOf(
                "Avena" to 70.0,
                "Leche entera" to 300.0,
                "Yogur proteico" to 200.0,
                "Banana" to 120.0
            )
        ),
        SeedRecipe(
            name = "Postre de huevo y cacao",
            description = "Licuar muy bien (huevos duros) y refrigerar. Edulcorante y vainilla al gusto.",
            ingredients = listOf(
                "Huevo entero" to 165.0,
                "Leche entera" to 250.0,
                "Avena" to 40.0,
                "Cacao amargo" to 10.0,
                "Banana" to 120.0
            )
        )
    )
}
