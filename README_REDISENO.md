# CentroTareas — Interfaz profesional con Compose

Rediseño visual de CentroTareas: encabezado, métricas dinámicas, tarjetas compactas,
iconos vectoriales y comparación con datos reales. No se modifican ViewModel, Repository,
Modelos ni MainActivity.

## Abrir

1. Abre la carpeta `CentroTareas` en Android Studio.
2. Configura el Android SDK de tu PC, sincroniza Gradle y ejecuta `app`.
3. Las métricas se llenarán según los resultados de los ejercicios; no hay valores simulados.

## Archivos modificados

- `app/src/main/java/com/maldonado/centrotareas/ui/PantallaTareas.kt`
- `app/src/main/java/com/maldonado/centrotareas/ui/theme/Color.kt`
- `app/src/main/java/com/maldonado/centrotareas/ui/theme/Theme.kt`
- `app/src/main/java/com/maldonado/centrotareas/ui/theme/Type.kt`
- 16 nuevos archivos `app/src/main/res/drawable/ct_*.xml`.

## Referencias

- `design/REFERENCIA_INTERFAZ.png` — maqueta visual; contiene números ilustrativos.
- `design/PROMPT_REDISENO.md` — prompt detallado para reutilizar el diseño.

## Pruebas pendientes

La entrega no incluye APK ni compilación verificada: este entorno no dispone del SDK de Android para construir y ejecutar la app. Se validaron las referencias de recursos, sintaxis XML y correspondencia de callbacks con ViewModel.
