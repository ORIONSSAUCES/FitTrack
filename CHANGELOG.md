# Registro de cambios

Formato basado en [Keep a Changelog](https://keepachangelog.com/es/).
Versionado: [SemVer](https://semver.org/lang/es/) simplificado (MAJOR.MINOR.PATCH).

## [0.1.0] - Sin publicar

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
