package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.data.database.seed.RoutineSeed.SeedExercise
import com.brunoapp.fittrack.data.database.seed.RoutineSeed.SeedRoutine
import com.brunoapp.fittrack.data.database.seed.RoutineSeed.SeedSet

/**
 * 4-day at-home plan (Mon/Tue/Thu/Fri): upper/lower split, 5 kg plate,
 * chair and mat. Postpartum-conscious: no crunches, no jumps, no sets to
 * failure (RIR 2-3), core work focused on deep-core stability.
 */
object HomeRoutineSeed {

    private fun sets(count: Int, min: Int, max: Int): List<SeedSet> =
        List(count) { SeedSet(min, max, 2) }

    private const val SAFETY =
        "Exhala al hacer fuerza, nunca aguantes el aire. Para si sientes dolor en la cicatriz, " +
            "abombamiento del abdomen, pesadez pélvica o pérdidas de orina."

    fun all(): List<SeedRoutine> = listOf(
        SeedRoutine(
            name = "Lunes — Tren superior A",
            description = "Pecho, espalda, hombros, bíceps y tríceps. ~35 min. $SAFETY",
            dayOfWeek = 0,
            exercises = listOf(
                SeedExercise("Flexiones inclinadas en silla", 90, sets(3, 8, 12),
                    "Silla contra la pared para que no se mueva"),
                SeedExercise("Remo a una mano con disco", 90, sets(3, 10, 12), "Reps por brazo"),
                SeedExercise("Press de hombros sentada con disco", 90, sets(3, 10, 12)),
                SeedExercise("Curl de bíceps con disco", 60, sets(3, 12, 15)),
                SeedExercise("Extensión de tríceps sobre la cabeza con disco", 60, sets(3, 12, 15)),
                SeedExercise("Dead bug", 60, sets(2, 6, 8), "Reps por lado, lento")
            )
        ),
        SeedRoutine(
            name = "Martes — Tren inferior A",
            description = "Cuádriceps, glúteos, femorales y gemelos. ~35 min. $SAFETY",
            dayOfWeek = 1,
            exercises = listOf(
                SeedExercise("Sentadilla a la silla con disco", 90, sets(3, 10, 15)),
                SeedExercise("Puente de glúteos con disco", 90, sets(3, 12, 15),
                    "Aprieta glúteos 1 s arriba"),
                SeedExercise("Zancada atrás con apoyo", 90, sets(3, 8, 10), "Reps por pierna"),
                SeedExercise("Peso muerto rumano con disco", 90, sets(3, 10, 12)),
                SeedExercise("Elevación de talones con disco", 60, sets(2, 15, 20)),
                SeedExercise("Bird dog", 60, sets(2, 6, 8), "Reps por lado")
            )
        ),
        SeedRoutine(
            name = "Jueves — Tren superior B",
            description = "Pecho, espalda, hombros, bíceps y tríceps. ~35 min. $SAFETY",
            dayOfWeek = 3,
            exercises = listOf(
                SeedExercise("Press de pecho en el suelo con disco", 90, sets(3, 10, 15)),
                SeedExercise("Remo inclinada con disco", 90, sets(3, 10, 12)),
                SeedExercise("Elevación frontal con disco", 60, sets(3, 10, 12)),
                SeedExercise("Curl martillo con disco", 60, sets(3, 12, 15)),
                SeedExercise("Extensión de tríceps tumbada con disco", 60, sets(3, 12, 15)),
                SeedExercise("Plancha con rodillas apoyadas", 60, sets(3, 20, 30),
                    "Las 'reps' son segundos")
            )
        ),
        SeedRoutine(
            name = "Viernes — Tren inferior B",
            description = "Glúteos, piernas y gemelos. ~35 min. $SAFETY",
            dayOfWeek = 4,
            exercises = listOf(
                SeedExercise("Sentadilla sumo con disco", 90, sets(3, 12, 15)),
                SeedExercise("Sentadilla estática dividida con disco", 90, sets(3, 8, 10),
                    "Reps por pierna"),
                SeedExercise("Puente de glúteos a una pierna", 90, sets(3, 8, 10),
                    "Reps por pierna"),
                SeedExercise("Patada de glúteo en cuadrupedia", 60, sets(2, 12, 15),
                    "Reps por pierna"),
                SeedExercise("Elevación de talones a una pierna", 60, sets(2, 10, 12),
                    "Reps por pierna"),
                SeedExercise("Dead bug", 60, sets(2, 6, 8), "Reps por lado")
            )
        )
    )
}
