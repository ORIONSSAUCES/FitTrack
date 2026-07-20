# FitTrack 💪

Aplicación Android personal para registrar entrenamientos, alimentación, peso y progreso físico.
100% offline, sin cuentas, sin anuncios, sin telemetría. Tus datos nunca salen de tu teléfono.

## Características (MVP en desarrollo)

- 🏋️ Rutinas de entrenamiento con ejercicios, series y repeticiones
- ⏱️ Entrenamiento activo con temporizador de descanso
- 📊 Historial y progresión por ejercicio (1RM estimado con fórmula Epley)
- 🍽️ Plan de dieta semanal con seguimiento de cumplimiento
- 🔥 Control de calorías y macronutrientes
- ⚖️ Registro de peso semanal con gráficos
- 📸 Fotos de progreso privadas en el dispositivo
- 💾 Copias de seguridad manuales (exportar/importar JSON)

## Tecnología

| Componente | Herramienta |
|-----------|-------------|
| Lenguaje | Kotlin |
| UI | Jetpack Compose + Material Design 3 |
| Arquitectura | MVVM + Clean Architecture |
| Base de datos | Room |
| Preferencias | DataStore |
| Inyección de dependencias | Hilt |
| Navegación | Navigation Compose |
| Asincronía | Coroutines + Flow |
| Recordatorios | WorkManager |
| Build | Gradle Kotlin DSL |
| CI/CD | GitHub Actions |

Requisitos: Android 8.0+ (API 26).

## 📲 Cómo descargar el APK

### Opción 1: Desde una compilación (cada push a main)

1. Ve a la pestaña **Actions** del repositorio.
2. Abre la ejecución más reciente de **Build APK**.
3. Baja a la sección **Artifacts**.
4. Descarga `fittrack-debug-apk` (es un ZIP con `app-debug.apk` dentro).
5. Copia el APK a tu teléfono e instálalo.

### Opción 2: Desde una Release (versiones etiquetadas)

1. Ve a la pestaña **Releases**.
2. Descarga el archivo `FitTrack-vX.Y.Z.apk` de la última versión.
3. Instálalo en tu teléfono.

> **Nota:** al ser un APK fuera de Google Play, Android pedirá permitir
> "instalar aplicaciones de origen desconocido". Es normal y seguro:
> el APK lo compila GitHub Actions desde este mismo código.

## 🔄 Actualizaciones automáticas en el teléfono (Obtainium)

Para recibir aviso de nuevas versiones e instalarlas directo desde el celular:

1. Instala [Obtainium](https://github.com/ImranR98/Obtainium/releases) (descarga el APK `arm64-v8a`).
2. Como este repo es privado, crea un token de lectura:
   - GitHub → **Settings** → **Developer settings** → **Personal access tokens** → **Fine-grained tokens** → *Generate new token*.
   - Repository access: *Only select repositories* → `FitTrack`.
   - Permissions → Repository permissions → **Contents: Read-only**.
3. En Obtainium: **Ajustes → Fuente: GitHub →** pega el token en *API key*.
4. En Obtainium: **➕ Agregar app** → URL: `https://github.com/ORIONSSAUCES/FitTrack`.
5. Listo: cada vez que se publique una Release (etiqueta `v*.*.*`), Obtainium te notifica y la instalas con un toque.

Todas las builds están firmadas con la misma clave (`signing/fittrack.keystore`),
por lo que las actualizaciones se instalan encima sin perder datos.

## 🛠️ Compilar localmente

### Requisitos

- Android Studio (Iguana o superior) **o** JDK 17 + Android SDK
- Gradle 8.9 (o usar el wrapper)

### Pasos

```bash
git clone https://github.com/TU_USUARIO/FitTrack.git
cd FitTrack

# Si es la primera vez y no existe gradle/wrapper/gradle-wrapper.jar:
gradle wrapper --gradle-version 8.9

# Compilar y testear
./gradlew testDebugUnitTest
./gradlew assembleDebug

# El APK queda en:
# app/build/outputs/apk/debug/app-debug.apk
```

### Abrir en Android Studio

1. **File → Open** y selecciona la carpeta del proyecto.
2. Espera la sincronización de Gradle (descarga dependencias la primera vez).
3. Ejecuta con el botón ▶️ o `Shift+F10`.

## 📁 Estructura del proyecto

```
app/src/main/java/com/brunoapp/fittrack/
├── core/          # Constantes, enums, cálculos puros
├── data/          # Room, DataStore, repositorios, backup
├── domain/        # Modelos y contratos (interfaces)
├── presentation/  # Compose UI, ViewModels, navegación, tema
├── di/            # Módulos de Hilt
└── worker/        # WorkManager (recordatorios)
```

## ✅ Estado del desarrollo

### Terminado
- [x] Módulo 0 — Proyecto base: Gradle KTS, Hilt, Room, Navigation, tema visual, CI/CD

- [x] Módulo 1 — Perfil y configuración: datos personales, objetivo, tema claro/oscuro/auto, descanso por defecto, día de control

- [x] Módulo 2 — Biblioteca de ejercicios: 57 ejercicios base, búsqueda, filtros, favoritos, ejercicios personalizados

- [x] Módulo 3 — Rutinas: CRUD completo, día asignado, series objetivo con rangos y tipos, reordenar, duplicar

- [x] Módulo 4 — Entrenamiento activo: registro por serie, temporizador persistente, récords, datos de sesión anterior

- [x] Módulo 5 — Historial y estadísticas: sesiones, frecuencia, detalle con notas, progresión con gráfico

### En desarrollo
- [ ] Módulo 6 — Alimentos y recetas
- [ ] Módulo 7 — Plan de dieta
- [ ] Módulo 8 — Registro diario de comidas
- [ ] Módulo 9 — Peso, medidas y fotos
- [ ] Módulo 10 — Backup y recordatorios
- [ ] Módulo 11 — Datos de demostración
- [ ] Módulo 12 — Pulido final

Ver [CHANGELOG.md](CHANGELOG.md) para el historial de cambios.

## 🔒 Privacidad

- Sin conexión a internet requerida
- Sin registro ni inicio de sesión
- Sin recopilación de estadísticas
- Sin anuncios
- Las fotos de progreso se guardan en almacenamiento privado de la app
- Único permiso: notificaciones (opcional, para recordatorios locales)
