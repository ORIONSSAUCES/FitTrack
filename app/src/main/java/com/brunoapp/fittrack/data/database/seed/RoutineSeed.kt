package com.brunoapp.fittrack.data.database.seed

/**
 * Bruno's PPL x2 weekly plan (evidence-based). Exercises referenced by the
 * Spanish seed names. Sets use RIR 2 on working sets and RIR 0 on the last one.
 */
object RoutineSeed {

    data class SeedSet(val repsMin: Int, val repsMax: Int, val rir: Int?)
    data class SeedExercise(
        val name: String,
        val restSeconds: Int,
        val sets: List<SeedSet>,
        val notes: String = ""
    )
    data class SeedRoutine(
        val name: String,
        val description: String,
        val dayOfWeek: Int,
        val exercises: List<SeedExercise>
    )

    private fun workingSets(count: Int, min: Int, max: Int): List<SeedSet> =
        List(count) { index ->
            SeedSet(min, max, if (index == count - 1) 0 else 2)
        }

    fun all(): List<SeedRoutine> = listOf(
        SeedRoutine(
            name = "Push A — Pecho",
            description = "Deltoides anterior y tríceps como secundarios. Última serie de cada ejercicio al fallo técnico.",
            dayOfWeek = 0,
            exercises = listOf(
                SeedExercise("Press de banca con barra", 180, workingSets(3, 6, 10),
                    "3 series de aproximación antes: 50%×12, 65%×6, 80%×3"),
                SeedExercise("Press inclinado con mancuernas", 150, workingSets(3, 8, 12),
                    "Banco a 30°"),
                SeedExercise("Peck Deck", 120, workingSets(3, 12, 15)),
                SeedExercise("Elevación lateral en polea", 90, workingSets(4, 15, 20)),
                SeedExercise("Pressón en polea con cuerda", 90, workingSets(3, 12, 15))
            )
        ),
        SeedRoutine(
            name = "Pull A — Espalda (anchura)",
            description = "Bíceps y deltoides posterior como secundarios.",
            dayOfWeek = 1,
            exercises = listOf(
                SeedExercise("Dominadas", 180, workingSets(3, 6, 10),
                    "Si no salen 6, usar jalón al pecho"),
                SeedExercise("Remo sentado en polea", 150, workingSets(3, 8, 12)),
                SeedExercise("Face Pull", 90, workingSets(3, 15, 20)),
                SeedExercise("Curl con barra EZ", 120, workingSets(3, 8, 12)),
                SeedExercise("Curl inclinado con mancuernas", 90, workingSets(3, 10, 15),
                    "Banco a 45°, estiramiento completo")
            )
        ),
        SeedRoutine(
            name = "Legs A — Cuádriceps",
            description = "Glúteos y gemelos como secundarios.",
            dayOfWeek = 2,
            exercises = listOf(
                SeedExercise("Sentadilla con barra", 180, workingSets(3, 6, 10),
                    "3 aproximaciones. Si el rack está ocupado: prensa"),
                SeedExercise("Hack Squat", 150, workingSets(3, 8, 12),
                    "Alternativa: Prensa 45°"),
                SeedExercise("Extensión de cuádriceps", 120, workingSets(3, 12, 15)),
                SeedExercise("Elevación de talones de pie", 90, workingSets(4, 10, 15),
                    "Rango completo, pausa abajo")
            )
        ),
        SeedRoutine(
            name = "Push B — Hombros",
            description = "Pecho superior y tríceps como secundarios.",
            dayOfWeek = 3,
            exercises = listOf(
                SeedExercise("Press militar con mancuernas", 180, workingSets(3, 8, 12),
                    "Sentado con respaldo"),
                SeedExercise("Press inclinado con mancuernas", 150, workingSets(3, 10, 15)),
                SeedExercise("Elevación lateral en polea", 90, workingSets(4, 15, 20),
                    "Unilateral"),
                SeedExercise("Extensión sobre cabeza en polea", 120, workingSets(3, 10, 15),
                    "Cabeza larga del tríceps"),
                SeedExercise("Fondos en paralelas", 150, workingSets(3, 8, 12))
            )
        ),
        SeedRoutine(
            name = "Pull B — Espalda (grosor)",
            description = "Bíceps y deltoides posterior como secundarios.",
            dayOfWeek = 4,
            exercises = listOf(
                SeedExercise("Remo con barra", 180, workingSets(3, 6, 10),
                    "Torso a 45°, barra al abdomen"),
                SeedExercise("Jalón agarre supino", 150, workingSets(3, 8, 12)),
                SeedExercise("Pájaros con mancuernas", 90, workingSets(3, 15, 20)),
                SeedExercise("Curl en polea baja", 90, workingSets(3, 10, 15)),
                SeedExercise("Curl martillo", 90, workingSets(3, 10, 15))
            )
        ),
        SeedRoutine(
            name = "Legs B — Cadena posterior",
            description = "Isquios, glúteos y sóleo. Opcional: si no vas el sábado, suma 1 serie de RDL y femoral al miércoles.",
            dayOfWeek = 5,
            exercises = listOf(
                SeedExercise("Peso muerto rumano", 180, workingSets(3, 8, 12),
                    "Cadera atrás, barra pegada a las piernas"),
                SeedExercise("Curl femoral tumbado", 120, workingSets(3, 10, 15)),
                SeedExercise("Hip Thrust con barra", 150, workingSets(3, 10, 15),
                    "Pausa de 1 s arriba"),
                SeedExercise("Elevación de talones sentado", 90, workingSets(4, 12, 15),
                    "Sóleo")
            )
        )
    )
}
