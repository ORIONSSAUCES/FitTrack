# Registro de cambios

Formato basado en [Keep a Changelog](https://keepachangelog.com/es/).
Versionado: [SemVer](https://semver.org/lang/es/) simplificado (MAJOR.MINOR.PATCH).

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
