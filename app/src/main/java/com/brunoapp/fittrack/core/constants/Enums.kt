package com.brunoapp.fittrack.core.constants

/** Primary and secondary muscle groups for exercise classification. */
enum class MuscleGroup(val displayName: String) {
    CHEST("Pecho"),
    BACK("Espalda"),
    QUADS("Cuádriceps"),
    HAMSTRINGS("Isquiosurales"),
    GLUTES("Glúteos"),
    CALVES("Gemelos"),
    FRONT_DELTS("Deltoides anterior"),
    SIDE_DELTS("Deltoides medial"),
    REAR_DELTS("Deltoides posterior"),
    BICEPS("Bíceps"),
    TRICEPS("Tríceps"),
    FOREARMS("Antebrazos"),
    ABS("Abdominales"),
    LOWER_BACK("Lumbar"),
    TRAPS("Trapecios"),
    FULL_BODY("Cuerpo completo")
}

enum class Equipment(val displayName: String) {
    BARBELL("Barra"),
    DUMBBELL("Mancuernas"),
    MACHINE("Máquina"),
    CABLE("Polea"),
    BODYWEIGHT("Peso corporal"),
    KETTLEBELL("Kettlebell"),
    BAND("Banda elástica"),
    SMITH_MACHINE("Multipower"),
    EZ_BAR("Barra EZ"),
    OTHER("Otro")
}

enum class SetType(val displayName: String) {
    WARMUP("Calentamiento"),
    NORMAL("Normal"),
    DROPSET("Descendente"),
    FAILURE("Al fallo")
}

enum class Objective(val displayName: String) {
    LOSE_FAT("Perder grasa"),
    MAINTAIN("Mantener"),
    GAIN_MUSCLE("Ganar masa muscular")
}

enum class ThemeMode(val displayName: String) {
    LIGHT("Claro"),
    DARK("Oscuro"),
    AUTO("Automático")
}

enum class AdherenceLevel(val displayName: String) {
    FULL("Completo"),
    PARTIAL("Parcial"),
    NONE("No seguido"),
    NOT_SET("Sin registrar")
}
