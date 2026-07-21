package com.brunoapp.fittrack.data.database.seed

/**
 * Effectiveness tiers for the base Spanish exercises, following the
 * hypertrophy evidence used in Bruno's training plan (mechanical tension,
 * progressive overload capacity, stretch-position loading, EMG).
 * 1 = most effective · 2 = effective · 3 = complementary.
 */
object ExerciseTierMap {

    val byExerciseName: Map<String, Int> = mapOf(
        // Chest
        "Press de banca con barra" to 1,
        "Press de banca con mancuernas" to 1,
        "Press inclinado con barra" to 1,
        "Press inclinado con mancuernas" to 1,
        "Fondos en paralelas" to 1,
        "Press en máquina" to 2,
        "Peck Deck" to 2,
        "Cruce de poleas" to 2,
        "Flexiones" to 2,
        // Back
        "Dominadas" to 1,
        "Jalón al pecho" to 1,
        "Remo con barra" to 1,
        "Remo sentado en polea" to 1,
        "Remo T-bar" to 1,
        "Jalón agarre supino" to 2,
        "Remo con mancuerna a una mano" to 2,
        "Remo en máquina" to 2,
        "Pull-over en polea" to 3,
        // Quads
        "Sentadilla con barra" to 1,
        "Prensa 45°" to 1,
        "Hack Squat" to 1,
        "Zancada búlgara" to 1,
        "Extensión de cuádriceps" to 2,
        "Sentadilla en multipower" to 2,
        // Hamstrings
        "Peso muerto rumano" to 1,
        "Curl femoral tumbado" to 1,
        "Curl femoral sentado" to 1,
        "Peso muerto rumano con mancuernas" to 2,
        // Glutes
        "Hip Thrust con barra" to 1,
        "Patada de glúteo en polea" to 2,
        "Puente de glúteo" to 3,
        // Calves
        "Elevación de talones de pie" to 1,
        "Elevación de talones sentado" to 1,
        "Elevación de talones en prensa" to 2,
        // Shoulders
        "Press militar con barra" to 1,
        "Press militar con mancuernas" to 1,
        "Elevación lateral con mancuernas" to 1,
        "Elevación lateral en polea" to 1,
        "Face Pull" to 1,
        "Reverse Peck Deck" to 1,
        "Pájaros con mancuernas" to 2,
        // Biceps
        "Curl con barra EZ" to 1,
        "Curl inclinado con mancuernas" to 1,
        "Curl en polea baja" to 1,
        "Curl con mancuernas alternado" to 2,
        "Curl martillo" to 2,
        "Curl predicador" to 2,
        // Triceps
        "Pressón en polea con cuerda" to 1,
        "Extensión sobre cabeza en polea" to 1,
        "Press francés con barra EZ" to 1,
        "Press de banca agarre cerrado" to 1,
        "Pressón en polea con barra" to 2,
        "Extensión con mancuerna sobre cabeza" to 2,
        // Abs
        "Crunch en polea" to 1,
        "Elevación de piernas colgado" to 1,
        "Rueda abdominal" to 1,
        "Plancha" to 2
    )
}
