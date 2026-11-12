package com.brunoapp.fittrack.data.guide

/**
 * Bruno's two-phase nutrition guide (static content).
 * Phase 1: fat loss to 95-96 kg with tirzepatide. Phase 2: maintenance → lean bulk.
 * Numbers computed for 1.85 m, ~109.6 kg at start of the guide, 5 lifting days/week.
 * Doses of hormonal substances are intentionally NOT part of this content.
 */
object NutritionGuide {

    const val START_WEIGHT_KG = 112.6
    const val TARGET_WEIGHT_KG = 96.0
    const val TARGET_RANGE = "95–96 kg"

    data class Target(val label: String, val value: String)
    data class Meal(val name: String, val foods: String, val kcal: Int, val protein: Int, val carbs: Int)
    data class Menu(
        val title: String,
        val meals: List<Meal>,
        val totalKcal: Int,
        val totalProtein: Int,
        val totalCarbs: Int,
        val totalFat: Int
    )
    data class Section(val title: String, val emoji: String, val items: List<String>, val alert: Boolean = false)
    data class Phase(
        val number: Int,
        val title: String,
        val subtitle: String,
        val goal: String,
        val targets: List<Target>,
        val menus: List<Menu>,
        val sections: List<Section>
    )

    // ═══════════════════════ PHASE 1 ═══════════════════════

    val phase1 = Phase(
        number = 1,
        title = "Fase 1 — Descenso",
        subtitle = "De 112,6 kg a $TARGET_RANGE",
        goal = "Bajar grasa sin perder músculo. Ritmo ideal: 0,6 a 1,1 kg por semana. " +
            "Duración estimada: 14 a 19 semanas (entre fines de febrero y fines de marzo de 2027).",
        targets = listOf(
            Target("Día de entreno", "~2.300 kcal"),
            Target("Día de descanso", "~1.900 kcal"),
            Target("Proteína (todos los días)", "210–220 g"),
            Target("Carbohidratos", "~200 g entreno · ~100 g descanso"),
            Target("Grasas", "~70 g"),
            Target("Agua", "3 a 3,5 L"),
            Target("Fibra", "25 g o más"),
            Target("Creatina", "5 g diarios")
        ),
        menus = listOf(
            Menu(
                title = "Día de entreno",
                meals = listOf(
                    Meal("Desayuno", "4 huevos duros o revueltos + café o infusión", 315, 28, 2),
                    Meal("Almuerzo", "250 g de pollo (crudo) + 150 g de arroz cocido + ensalada grande con 1 cda de aceite", 635, 65, 58),
                    Meal("Pre/post entreno", "Licuado: 250 ml de leche + 1½ scoop de whey (45 g) + 60 g de avena + 1 banana + 5 g de creatina", 650, 53, 77),
                    Meal("Cena", "250 g de carne magra (o 2 latas de atún) + 200 g de papa hervida + ensalada", 600, 59, 51),
                    Meal("Snack", "1 yogur proteico", 120, 16, 12)
                ),
                totalKcal = 2320, totalProtein = 221, totalCarbs = 201, totalFat = 70
            ),
            Menu(
                title = "Día de descanso",
                meals = listOf(
                    Meal("Desayuno", "4 huevos + 40 g de avena con canela", 465, 33, 26),
                    Meal("Almuerzo", "250 g de pollo (crudo) + ensalada grande con 1 cda de aceite", 440, 61, 16),
                    Meal("Merienda", "Licuado: 250 ml de leche + 1½ scoop de whey + 5 g de creatina + 1 manzana", 400, 44, 36),
                    Meal("Cena", "250 g de carne magra (o 2 latas de atún/sardinas) + ensalada con 1 cda de aceite", 480, 56, 14),
                    Meal("Snack", "1 yogur proteico (solo si hay hambre)", 120, 16, 12)
                ),
                totalKcal = 1900, totalProtein = 210, totalCarbs = 103, totalFat = 73
            )
        ),
        sections = listOf(
            Section(
                "Reglas de alimentación", "🍽️", listOf(
                    "Proteína primero en cada comida. La tirzepatida te llena rápido: si dejas algo en el plato, que sea el arroz o la papa, nunca la carne o el huevo.",
                    "Llega a 200 g de proteína o más TODOS los días, aunque no tengas hambre. Es lo que evita que pierdas músculo.",
                    "No comas menos de ~1.800 kcal en ningún día. Comer demasiado poco acelera la pérdida de músculo y de fuerza.",
                    "Carbohidratos alrededor del entreno (almuerzo, licuado y cena). En días de descanso, al mínimo.",
                    "Come despacio y en porciones chicas. Para apenas te sientas lleno: forzar empeora las náuseas.",
                    "Evita frituras y comidas muy grasosas: con tirzepatida dan náuseas y reflujo.",
                    "Verduras en almuerzo y cena: el estreñimiento es un efecto común y la fibra lo previene.",
                    "Toma 3 a 3,5 L de agua. La tirzepatida baja la sed y la creatina necesita hidratación."
                )
            ),
            Section(
                "Tirzepatida (según prospecto)", "💉", listOf(
                    "Una aplicación por semana, siempre el mismo día. Rota la zona: abdomen, muslo o brazo.",
                    "La dosis se sube de a 2,5 mg y con al menos 4 semanas en cada escalón. Tu plan: 4 aplicaciones de 2,5 mg y después 5 mg. Te faltan 2 de 2,5 mg.",
                    "Dosis mínima efectiva: si sigues bajando entre 0,6 y 1,1 kg por semana, no hace falta subir la dosis. Consúltalo con tu médico antes de cualquier cambio.",
                    "Si te olvidas una dosis: aplícala si pasaron 4 días o menos. Si pasaron más, sáltala y sigue con tu día habitual.",
                    "Efectos comunes al empezar o subir dosis: náuseas, estreñimiento, reflujo y cansancio. Suelen mejorar en 1 a 2 semanas.",
                    "Para las náuseas: comidas chicas, poca grasa, no acostarse justo después de comer, jengibre o infusiones."
                )
            ),
            Section(
                "Señales de alarma — consulta ya", "🚨", listOf(
                    "Dolor fuerte en la parte alta del abdomen que no se va y puede irradiar a la espalda, con o sin vómitos.",
                    "Dolor del lado derecho debajo de las costillas, fiebre u ojos o piel amarillos.",
                    "Vómitos o diarrea que no te dejan tomar líquidos (riesgo de deshidratación).",
                    "Mareos fuertes, palpitaciones o presión arterial muy alta."
                ),
                alert = true
            ),
            Section(
                "Control médico y análisis", "🩺", listOf(
                    "Antes de empezar y cada 8 a 12 semanas: hemograma (hematocrito), perfil lipídico, hepatograma, creatinina, glucosa y HbA1c, estradiol y PSA.",
                    "Avísale al médico que tomas creatina: puede subir el valor de creatinina en sangre sin que haya un problema en los riñones.",
                    "Presión arterial una vez por semana. Objetivo: por debajo de 130/85.",
                    "Cualquier medicación o sustancia hormonal, y sus dosis, la define y controla tu médico. Anótalas en \"Mi protocolo\"."
                )
            ),
            Section(
                "Entrenamiento", "🏋️", listOf(
                    "Sigue con tu rutina de 5 días y con las mismas cargas: entrenar pesado es la señal que conserva el músculo.",
                    "Si la fuerza cae más de 10 % durante 2 semanas, sube 150 a 200 kcal de carbohidratos alrededor del entreno.",
                    "10 minutos de cardio después de las pesas y 8.000 a 10.000 pasos por día. Caminar quema más que sumar cardio intenso y no afecta la recuperación.",
                    "Duerme 7 a 8 horas: dormir poco aumenta el hambre y la pérdida de músculo."
                )
            ),
            Section(
                "Cómo ajustar cada semana", "📈", listOf(
                    "Pésate 3 a 7 veces por semana, en ayunas y después de ir al baño. Lo que cuenta es el promedio semanal, no un día suelto.",
                    "Bajas entre 0,6 y 1,1 kg por semana: sigue igual.",
                    "Bajas menos de 0,4 kg por semana durante 2 semanas seguidas: saca ~150 kcal (30 g de avena o 100 g de papa) o suma 2.000 pasos diarios.",
                    "Bajas más de 1,3 kg por semana durante 2 semanas (después del primer mes): suma 150 a 200 kcal de carbohidratos alrededor del entreno. Bajar muy rápido es perder músculo.",
                    "Las primeras 2 a 3 semanas se baja más rápido por pérdida de agua. Tus 3 kg en 2 semanas son normales."
                )
            ),
            Section(
                "Compras", "🛒", listOf(
                    "Huevos por maple de 30, pechuga de pollo por mayor, latas de atún y sardinas.",
                    "Whey: gasto inicial alto, pero de las fuentes de proteína más baratas por gramo.",
                    "Carbohidratos de un solo ingrediente: avena a granel, arroz y papas.",
                    "Verduras para ensalada: tomate, pepino, repollo, zanahoria, acelga, lechuga.",
                    "Creatina monohidrato: 5 g diarios, todos los días."
                )
            ),
            Section(
                "Cortes vacunos (Paraguay)", "🥩", listOf(
                    "Económicos y muy magros (molida o estofado): carnaza negra y carnaza blanca. Pide que la piquen en el momento.",
                    "Para bife a la plancha: bola de lomo y rabadilla, sacando la grasa externa.",
                    "Sin desperdicio (horno o hervido): peceto.",
                    "Evitar: costilla, vacío, falda y tapa de cuadril (mucha grasa)."
                )
            )
        )
    )

    // ═══════════════════════ PHASE 2 ═══════════════════════

    val phase2 = Phase(
        number = 2,
        title = "Fase 2 — Volumen limpio",
        subtitle = "Desde $TARGET_RANGE",
        goal = "Ganar fuerza y músculo sumando la menor grasa posible. Empieza con 2 a 4 semanas de transición " +
            "para estabilizar el peso, y recién después un superávit chico.",
        targets = listOf(
            Target("Transición (2–4 semanas)", "Subir ~150–200 kcal por semana hasta que el peso se estabilice"),
            Target("Mantenimiento estimado", "~2.900–3.100 kcal"),
            Target("Volumen limpio", "Mantenimiento + 200–300 kcal"),
            Target("Proteína", "190–200 g (2 g por kg)"),
            Target("Carbohidratos", "300 g o más"),
            Target("Grasas", "70–85 g"),
            Target("Ritmo de subida", "0,5 a 1 kg por mes"),
            Target("Creatina", "5 g diarios")
        ),
        menus = listOf(
            Menu(
                title = "Menú base (~2.950 kcal)",
                meals = listOf(
                    Meal("Desayuno", "4 huevos + 80 g de avena cocida en 250 ml de leche", 765, 46, 60),
                    Meal("Almuerzo", "250 g de pollo (crudo) + 300 g de arroz cocido + ensalada con 1 cda de aceite", 810, 68, 96),
                    Meal("Pre/post entreno", "Licuado: 250 ml de leche + 1½ scoop de whey + 50 g de avena + 1 banana + 5 g de creatina", 615, 52, 71),
                    Meal("Cena", "220 g de carne magra o atún + 300 g de papa + ensalada", 635, 54, 69),
                    Meal("Snack", "1 yogur proteico", 120, 16, 12)
                ),
                totalKcal = 2945, totalProtein = 237, totalCarbs = 309, totalFat = 81
            )
        ),
        sections = listOf(
            Section(
                "Cómo arrancar la fase", "🚦", listOf(
                    "No pases directo a comer de más. Durante 2 a 4 semanas sube ~150 a 200 kcal por semana (sobre todo carbohidratos) hasta que el peso se mantenga estable: ese es tu mantenimiento real.",
                    "Recién ahí suma 200 a 300 kcal por encima del mantenimiento para el volumen.",
                    "Sube más de 1 kg por mes: saca ~150 kcal. No sube en 3 semanas: suma ~150 kcal."
                )
            ),
            Section(
                "Dejar la tirzepatida", "💉", listOf(
                    "La suspensión se planifica con tu médico (de golpe o bajando la dosis).",
                    "Al dejarla, el apetito vuelve. Los estudios muestran que se recupera peso si se suspende sin un plan: por eso esta fase empieza con la transición y con controles de peso semanales.",
                    "Mantén la proteína alta y la comida ordenada: es lo que sostiene el resultado."
                )
            ),
            Section(
                "Alimentación", "🍽️", listOf(
                    "Proteína alta, igual que en la Fase 1.",
                    "Más carbohidratos para rendir y recuperar: avena, arroz, pastas, papas y frutas.",
                    "Se mantiene la rotación de pollo, carne magra, huevos, lácteos proteicos y pescado.",
                    "Agua: 3 L o más por día."
                )
            ),
            Section(
                "Control médico y análisis", "🩺", listOf(
                    "Repite los análisis al empezar la fase y cada 8 a 12 semanas: hemograma, perfil lipídico, hepatograma, creatinina, estradiol, PSA y glucosa.",
                    "Presión arterial semanal.",
                    "Cualquier cambio en medicación o sustancias hormonales, y sus dosis, lo define y controla tu médico. Regístralo en \"Mi protocolo\"."
                )
            ),
            Section(
                "Entrenamiento", "🏋️", listOf(
                    "Prioridad: sobrecarga progresiva. Usa las metas ⬆ que te muestra la app en cada serie.",
                    "Cada 4 a 6 semanas, una semana de descarga: mismas rutinas con menos series.",
                    "10 minutos de cardio después de entrenar y caminatas en los días de descanso."
                )
            )
        )
    )
}
