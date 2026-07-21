# Registro de cambios

Formato basado en [Keep a Changelog](https://keepachangelog.com/es/).
Versionado: [SemVer](https://semver.org/lang/es/) simplificado (MAJOR.MINOR.PATCH).

## [0.11.0] - Sin publicar

### Agregado
- Imágenes de ejercicios:
  - 473 imágenes integradas en la app (13 MB, funcionan sin internet)
  - Los 57 ejercicios base ahora muestran su imagen demostrativa
  - +416 ejercicios nuevos del catálogo extendido (nombres en inglés) con imagen,
    grupo muscular y equipamiento detectados automáticamente
- Módulo 9 — Peso, medidas y fotos:
  - Registro de peso con fecha/hora y notas (ej: "en ayunas")
  - Tarjetas: peso actual, promedio 7 días, cambio total, comparación vs semana anterior
  - Gráfico de evolución del peso
  - Medidas corporales: cintura, abdomen, pecho, cadera, cuello, brazos, muslos y % grasa
  - Fotos de progreso: frontal/lateral/posterior desde el selector del sistema,
    copiadas a almacenamiento privado (nunca salen del dispositivo)
  - Eliminación con confirmación en los tres registros
  - Migración de base de datos v7 → v8

## [0.10.0] - Sin publicar

### Agregado
- Módulo 8 — Registro diario de comidas:
  - Iniciar el día copiando las comidas del plan activo (o empezar vacío)
  - Marcar comidas como completadas con checkbox (tarjeta se ilumina)
  - Editar cantidades realmente consumidas, agregar/quitar alimentos y recetas
  - Resumen "Consumido hoy vs objetivo" con barras de macros
  - Toggle día de entrenamiento/descanso (cambia los objetivos)
  - Adherencia del día: completo / parcial / no seguido
  - Cumplimiento de hoy (%) y promedio semanal (%)
  - Migración de base de datos v6 → v7
  - 5 pruebas unitarias de cumplimiento

## [0.9.0] - Sin publicar

### Agregado
- Módulo 7 — Plan de dieta semanal:
  - Plan "Dieta base — Volumen" precargado con el menú completo de lunes a domingo
  - 5 comidas por día con cantidades exactas (desayuno, media mañana, almuerzo, pre-entreno/merienda, cena)
  - Objetivos diferenciados: entrenamiento (2700 kcal / 200 g prot) y descanso (2550 kcal / 195 g prot)
  - Selector de día L-M-X-J-V-S-D con totales de macros vs objetivo (barras de progreso)
  - Marcar cada día como entrenamiento o descanso
  - Agregar/quitar comidas e items (alimentos o recetas) con cantidades editables
  - Migración de base de datos v5 → v6
  - 3 pruebas unitarias de totales del plan

## [0.8.0] - Sin publicar

### Agregado
- Módulo 6 — Alimentos y recetas:
  - 20 alimentos precargados con los macros de la dieta real (pollo, avena, arroz, porotos, lentejas, atún, sardinas, mayonesa light Hellmann's, yogur proteico Trébol, proteína, creatina, etc.)
  - Notas de pesaje en cada alimento (crudo/cocido/escurrido)
  - 4 recetas de desayuno precargadas: waffles de banana, waffles de cacao, avena overnight y postre de huevo y cacao
  - Crear, editar y eliminar alimentos con macros por 100 g/ml y porción habitual
  - Recetas con ingredientes y totales de macros calculados en vivo
  - Búsqueda y favoritos
  - Migración de base de datos v4 → v5
  - 6 pruebas unitarias de cálculos nutricionales

## [0.5.0] - Sin publicar

### Agregado
- Módulo 5 — Historial y estadísticas:
  - Pestaña Historial con contadores: entrenamientos esta semana, este mes y volumen semanal
  - Lista de sesiones con fecha, duración, series, volumen y récords 🏆
  - Detalle de sesión: series completadas por ejercicio, RIR, récords marcados
  - Notas editables por sesión
  - Eliminar sesiones del historial (con confirmación)
  - Progresión por ejercicio: mejor serie, 1RM estimado, gráfico de evolución y series recientes
  - Gráfico de líneas propio sin dependencias externas
  - 7 pruebas unitarias de utilidades de fecha

## [0.4.0] - Sin publicar

### Agregado
- Módulo 4 — Entrenamiento activo:
  - Iniciar entrenamiento desde cualquier rutina (botón ▶)
  - Registro de peso, repeticiones y RIR por serie con campos grandes
  - Columna "Anterior" con los valores de la última sesión del mismo ejercicio
  - Peso prellenado desde la sesión anterior o la rutina
  - Temporizador de descanso automático al completar serie: +15 s / −15 s / saltar
  - El temporizador y la sesión sobreviven al cierre de la aplicación
  - Detección de récords personales en vivo (fórmula Epley) con aviso 🏆
  - Agregar/eliminar series durante la sesión
  - Banner "Entrenamiento en curso" para continuar tras minimizar
  - Protección: confirmación al descartar, al terminar y al reemplazar sesión activa
  - Resumen final: duración, ejercicios, series, volumen total y récords
  - Actualización automática del peso objetivo en la rutina tras entrenar
  - Migración de base de datos v3 → v4
  - 5 pruebas unitarias de récords y volumen

## [0.3.0] - Sin publicar

### Agregado
- Módulo 3 — Rutinas:
  - Crear, editar, duplicar y eliminar rutinas (con confirmación)
  - Asignación de día de la semana a cada rutina
  - Agregar ejercicios desde la biblioteca con buscador
  - Series objetivo por ejercicio: tipo (calentamiento/normal/descendente/al fallo), rango de repeticiones
  - Descanso configurable y notas por ejercicio
  - Reordenar ejercicios con flechas
  - Migración de base de datos v2 → v3
  - 6 pruebas unitarias de reordenamiento

## [0.2.0] - Sin publicar

### Agregado
- Módulo 2 — Biblioteca de ejercicios:
  - 57 ejercicios base precargados con músculo principal, secundarios, equipamiento e instrucciones
  - Búsqueda por nombre y filtros por grupo muscular, equipamiento y favoritos
  - Pantalla de detalle con información completa y notas personales editables
  - Crear, editar y eliminar ejercicios personalizados (con confirmación)
  - Favoritos con estrella dorada
  - Migración de base de datos v1 → v2 (sin pérdida de datos)
  - Tabla de récords personales (se llenará al registrar entrenamientos)
  - 8 pruebas unitarias del filtro de ejercicios

## [0.1.0] - Sin publicar

### Agregado
- Módulo 1 — Perfil y configuración:
  - Pantalla de Perfil funcional: nombre, altura, peso inicial y objetivo
  - Selector de objetivo: perder grasa / mantener / ganar masa muscular
  - Tema visual claro/oscuro/automático aplicado en vivo desde DataStore
  - Descanso por defecto configurable (15 s a 10 min, slider)
  - Día de control semanal de peso configurable
  - Validación de entradas (altura 100-250 cm, peso 30-400 kg, coma o punto decimal)
  - Repositorio de perfil con Room + capa de dominio limpia
  - 12 pruebas unitarias de validadores
  - Corrección: etiquetas de navegación en una sola línea ("Entreno", "Nutrición")

### Agregado
- Módulo 0 — Proyecto base:
  - Proyecto Android con Kotlin 2.0, Jetpack Compose y Material 3
  - Arquitectura MVVM con Hilt para inyección de dependencias
  - Base de datos Room configurada (entidad Profile inicial)
  - DataStore para preferencias de usuario
  - Navegación inferior con 5 secciones: Inicio, Entrenamiento, Alimentación, Progreso, Perfil
  - Tema visual propio: verde lima sobre gris carbón, tema claro y oscuro
  - Icono adaptativo de la app (mancuerna en verde lima)
  - Cálculos de dominio: 1RM (Epley), volumen, calorías desde macros, promedios
  - 12 pruebas unitarias de los cálculos
  - Workflow de GitHub Actions: test + build + APK como artifact en cada push a main
  - Workflow de Release: APK adjunto al crear etiquetas v*.*.*
