package com.brunoapp.fittrack.data.database.seed

import com.brunoapp.fittrack.core.constants.Equipment
import com.brunoapp.fittrack.core.constants.MuscleGroup
import com.brunoapp.fittrack.data.database.entity.ExerciseEntity
import java.time.Instant

/**
 * At-home exercises using a 5 kg plate, a chair and a mat.
 * Added to every install's library (inserted by name if missing).
 */
object HomeExerciseSeed {

    private fun ex(
        name: String,
        muscle: MuscleGroup,
        equipment: Equipment,
        image: String,
        instructions: String,
        secondary: List<MuscleGroup> = emptyList()
    ) = ExerciseEntity(
        name = name,
        muscleGroup = muscle.name,
        secondaryMuscles = secondary.joinToString(",") { it.name },
        equipment = equipment.name,
        instructions = instructions,
        isCustom = false,
        imagePath = "exercises/$image",
        effectivenessTier = 2,
        createdAt = Instant.now().toString()
    )

    fun all(): List<ExerciseEntity> = listOf(
        // ── Upper body ──
        ex("Flexiones inclinadas en silla", MuscleGroup.CHEST, Equipment.BODYWEIGHT,
            "10718120_1assistedweightedpushupfemale.jpg",
            "Manos en el asiento de una silla apoyada contra la pared. Cuerpo recto, baja el pecho al borde y empuja. Exhala al subir.",
            listOf(MuscleGroup.TRICEPS, MuscleGroup.FRONT_DELTS)),
        ex("Press de pecho en el suelo con disco", MuscleGroup.CHEST, Equipment.PLATE,
            "74281201_dumbbelllyingonfloorchestpressfemale.jpg",
            "Boca arriba en la colchoneta, rodillas flexionadas. Disco sujeto con ambas manos sobre el pecho, empuja hacia el techo y baja hasta que los codos toquen el suelo.",
            listOf(MuscleGroup.TRICEPS)),
        ex("Remo a una mano con disco", MuscleGroup.BACK, Equipment.PLATE,
            "23931201_dumbbellonearmbentoverrowfemale.jpg",
            "Mano y rodilla apoyadas en la silla, espalda plana. Lleva el disco hacia la cadera con el codo pegado. Pausa arriba.",
            listOf(MuscleGroup.BICEPS, MuscleGroup.REAR_DELTS)),
        ex("Remo inclinada con disco", MuscleGroup.BACK, Equipment.PLATE,
            "24821201_dumbbellbentoverrowfemale.jpg",
            "Rodillas suaves, torso inclinado ~45°, espalda recta. Lleva el disco hacia el ombligo juntando las escápulas.",
            listOf(MuscleGroup.BICEPS, MuscleGroup.REAR_DELTS)),
        ex("Press de hombros sentada con disco", MuscleGroup.SIDE_DELTS, Equipment.PLATE,
            "22371201_dumbbellseatedshoulderpressfemale.jpg",
            "Sentada en la silla, espalda apoyada. Disco a la altura del mentón, empuja hacia arriba sin arquear la zona lumbar.",
            listOf(MuscleGroup.FRONT_DELTS, MuscleGroup.TRICEPS)),
        ex("Elevación frontal con disco", MuscleGroup.FRONT_DELTS, Equipment.PLATE,
            "22331201_dumbbellfrontraisefemale.jpg",
            "De pie, disco sujeto por los bordes. Sube con brazos casi rectos hasta la altura de los hombros y baja lento."),
        ex("Curl de bíceps con disco", MuscleGroup.BICEPS, Equipment.PLATE,
            "24831201_dumbbellbicepscurlfemale.jpg",
            "Disco sujeto por los bordes, codos pegados al cuerpo. Sube sin balancear el torso y baja en 2-3 segundos."),
        ex("Curl martillo con disco", MuscleGroup.BICEPS, Equipment.PLATE,
            "24851201_dumbbellhammercurlfemale.jpg",
            "Disco vertical sujeto por los costados (agarre neutro). Codos fijos, sube y baja controlado.",
            listOf(MuscleGroup.FOREARMS)),
        ex("Extensión de tríceps sobre la cabeza con disco", MuscleGroup.TRICEPS, Equipment.PLATE,
            "23901201_dumbbellstandingtricepsextensionfemale.jpg",
            "Sentada o de pie, disco con ambas manos detrás de la cabeza. Extiende los codos hacia el techo sin abrirlos."),
        ex("Extensión de tríceps tumbada con disco", MuscleGroup.TRICEPS, Equipment.PLATE,
            "22511201_dumbbelllyingtricepsextensionfemale.jpg",
            "Boca arriba, disco sobre el pecho con brazos extendidos. Flexiona solo los codos llevando el disco hacia la frente y extiende."),

        // ── Lower body ──
        ex("Sentadilla a la silla con disco", MuscleGroup.QUADS, Equipment.PLATE,
            "22811201_dumbbellgobletsquatfemale.jpg",
            "Disco abrazado al pecho. Siéntate en la silla de forma controlada, roza el asiento y sube empujando con los talones.",
            listOf(MuscleGroup.GLUTES)),
        ex("Sentadilla sumo con disco", MuscleGroup.GLUTES, Equipment.PLATE,
            "23221201_dumbbellsumosquatfemale.jpg",
            "Pies más anchos que la cadera, puntas hacia afuera. Disco colgando entre las piernas, baja con la espalda recta.",
            listOf(MuscleGroup.QUADS)),
        ex("Zancada atrás con apoyo", MuscleGroup.QUADS, Equipment.BODYWEIGHT,
            "63881201_bodyweightreverselungefemale.jpg",
            "Mano en el respaldo de la silla para equilibrio. Da un paso atrás y baja la rodilla trasera cerca del suelo. Alterna piernas.",
            listOf(MuscleGroup.GLUTES)),
        ex("Sentadilla estática dividida con disco", MuscleGroup.QUADS, Equipment.PLATE,
            "29591201_dumbbellsplitsquatfemale.jpg",
            "Un pie adelante y otro atrás, fijos. Baja y sube vertical. Completa las reps de una pierna y cambia.",
            listOf(MuscleGroup.GLUTES)),
        ex("Peso muerto rumano con disco", MuscleGroup.HAMSTRINGS, Equipment.PLATE,
            "22221201_dumbbellromaniandeadliftfemale.jpg",
            "Rodillas suaves, lleva la cadera atrás con la espalda recta y el disco pegado a los muslos. Sube apretando glúteos.",
            listOf(MuscleGroup.GLUTES, MuscleGroup.LOWER_BACK)),
        ex("Puente de glúteos con disco", MuscleGroup.GLUTES, Equipment.PLATE,
            "21451201_hipthrustsfemale.jpg",
            "Boca arriba, rodillas flexionadas, disco sobre la cadera. Exhala, eleva la cadera y aprieta glúteos 1 segundo arriba.",
            listOf(MuscleGroup.HAMSTRINGS)),
        ex("Puente de glúteos a una pierna", MuscleGroup.GLUTES, Equipment.BODYWEIGHT,
            "10782120_1dumbbellsingleleghipthrustfemale.jpg",
            "Espalda alta apoyada en el borde de la silla (o en el suelo). Una pierna estirada, sube la cadera con la otra.",
            listOf(MuscleGroup.HAMSTRINGS)),
        ex("Patada de glúteo en cuadrupedia", MuscleGroup.GLUTES, Equipment.BODYWEIGHT,
            "14741201_bentlegkickbackkneeling.jpg",
            "En cuatro apoyos, rodilla flexionada a 90°. Empuja el talón hacia el techo sin arquear la espalda baja."),
        ex("Elevación de talones con disco", MuscleGroup.CALVES, Equipment.PLATE,
            "22101201_dumbbellstandingcalfraisefemale.jpg",
            "De pie, una mano en la silla para equilibrio. Sube en puntas de pie, pausa arriba y baja lento."),
        ex("Elevación de talones a una pierna", MuscleGroup.CALVES, Equipment.BODYWEIGHT,
            "39731201_dumbbellsinglelegcalfraisefemaleversion2.jpg",
            "Apoyada en la silla, sobre una pierna. Sube en puntas de pie y baja controlado."),

        // ── Core (postpartum-safe) ──
        ex("Dead bug", MuscleGroup.ABS, Equipment.BODYWEIGHT,
            "78391201_deadbugversion3female.jpg",
            "Boca arriba, brazos al techo y rodillas a 90°. Exhala y extiende brazo y pierna contrarios sin despegar la zona lumbar. Detente si el abdomen se abomba en la línea media."),
        ex("Bird dog", MuscleGroup.ABS, Equipment.BODYWEIGHT,
            "51821201_birddogversion2female.jpg",
            "En cuatro apoyos, extiende brazo y pierna contrarios manteniendo la cadera nivelada. Pausa 2 segundos.",
            listOf(MuscleGroup.LOWER_BACK, MuscleGroup.GLUTES)),
        ex("Plancha con rodillas apoyadas", MuscleGroup.ABS, Equipment.BODYWEIGHT,
            "90061201_frontelbowplankfemale.jpg",
            "Antebrazos y RODILLAS en la colchoneta, cuerpo recto de cabeza a rodillas. Respira normal, no aguantes el aire. Detente si aparece abombamiento abdominal.")
    )
}
