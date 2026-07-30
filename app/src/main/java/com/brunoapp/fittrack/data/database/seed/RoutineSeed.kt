package com.brunoapp.fittrack.data.database.seed

/**
 * Bruno's 5-day split (from his calculated spreadsheet, July 2026).
 * Set counts exactly as specified; last working set at RIR 0.
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
            name = "Lunes — Pecho y Tríceps",
            description = "Última serie de cada ejercicio al fallo técnico (RIR 0).",
            dayOfWeek = 0,
            exercises = listOf(
                SeedExercise("Press inclinado con mancuernas", 150, workingSets(3, 8, 12),
                    "Banco a 30°"),
                SeedExercise("Press de banca con mancuernas", 150, workingSets(3, 8, 12)),
                SeedExercise("Peck Deck", 120, workingSets(3, 12, 15),
                    "Aperturas en máquina"),
                SeedExercise("Extensión con mancuerna sobre cabeza", 120, workingSets(3, 10, 15)),
                SeedExercise("Pressón en polea con cuerda", 90, workingSets(3, 12, 15))
            )
        ),
        SeedRoutine(
            name = "Martes — Piernas (Cuádriceps)",
            description = "Énfasis en cuádriceps. Última serie al fallo técnico.",
            dayOfWeek = 1,
            exercises = listOf(
                SeedExercise("Hack Squat", 180, workingSets(3, 8, 12)),
                SeedExercise("Prensa 45°", 150, workingSets(3, 8, 12)),
                SeedExercise("Curl femoral sentado", 120, workingSets(3, 10, 15)),
                SeedExercise("Extensión de cuádriceps", 120, workingSets(2, 12, 15)),
                SeedExercise("Elevación de talones sentado", 90, workingSets(3, 12, 15),
                    "Pausa abajo, rango completo")
            )
        ),
        SeedRoutine(
            name = "Miércoles — Espalda y Bíceps",
            description = "Última serie de cada ejercicio al fallo técnico.",
            dayOfWeek = 2,
            exercises = listOf(
                SeedExercise("Jalón al pecho", 150, workingSets(3, 8, 12)),
                SeedExercise("Remo sentado en polea", 150, workingSets(3, 8, 12),
                    "Agarre en V"),
                SeedExercise("Remo en máquina", 150, workingSets(3, 8, 12),
                    "Iso-lateral (Hammer)"),
                SeedExercise("Curl en polea baja", 90, workingSets(3, 10, 15)),
                SeedExercise("Curl martillo", 90, workingSets(3, 10, 15))
            )
        ),
        SeedRoutine(
            name = "Jueves — Piernas (Femoral)",
            description = "Énfasis en cadena posterior. Última serie al fallo técnico.",
            dayOfWeek = 3,
            exercises = listOf(
                SeedExercise("Peso muerto rumano con mancuernas", 180, workingSets(3, 8, 12),
                    "Cadera atrás, mancuernas pegadas a las piernas"),
                SeedExercise("Curl femoral tumbado", 120, workingSets(3, 10, 15)),
                SeedExercise("Prensa 45°", 150, workingSets(3, 8, 12),
                    "Pies ALTOS en la plataforma: énfasis en glúteos y femorales"),
                SeedExercise("Extensión de cuádriceps", 120, workingSets(3, 12, 15)),
                SeedExercise("Elevación de talones sentado", 90, workingSets(3, 12, 15))
            )
        ),
        SeedRoutine(
            name = "Viernes — Hombros y Extras",
            description = "Hombros + repaso de pecho y espalda. Última serie al fallo técnico.",
            dayOfWeek = 4,
            exercises = listOf(
                SeedExercise("Press militar con mancuernas", 180, workingSets(3, 8, 12),
                    "Sentado con respaldo"),
                SeedExercise("Elevación lateral en polea", 90, workingSets(3, 15, 20),
                    "A un brazo"),
                SeedExercise("Face Pull", 90, workingSets(3, 15, 20),
                    "Tirón a la cara con cuerda"),
                SeedExercise("Press en máquina", 150, workingSets(3, 8, 12),
                    "DECLINADO si la máquina lo permite"),
                SeedExercise("Remo con barra", 150, workingSets(3, 8, 12),
                    "Inclinado, torso a 45°")
            )
        )
    )
}
