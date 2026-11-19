# PROMPTS.md · Fase 2 con IA

> **Proyecto:** CentroTareas · Laboratorio GLAB-S14 · Hilos en Android
> **Rama:** `mejora-ia`
> **Herramientas:** Gemini en Android Studio (modo Agent) para las etapas 1 a 5 · ChatGPT para el rediseño final con imagen de referencia (etapa 6)
> **Autor:** Luis Maldonado
> **Regla de la fase:** *la IA propone, yo decido*. Leí todo lo que generó Gemini, lo probé en el celular (realme RMX3501) y revisé que no rompiera las reglas del laboratorio antes de cada commit.

---

## Índice

1. [Tabla de prompts (formato pedido por la guía)](#1-tabla-de-prompts-formato-pedido-por-la-guía)
2. [Cómo trabajé con la IA](#2-cómo-trabajé-con-la-ia)
3. [Reglas que le impuse a la IA](#3-reglas-que-le-impuse-a-la-ia)
4. [El prompt completo (etapas 1 a 5 · Gemini)](#4-el-prompt-completo-etapas-1-a-5--gemini)
5. [Etapa 1 · Progreso desde el Repository](#5-etapa-1--progreso-desde-el-repository)
6. [Etapa 2 · Reintento automático](#6-etapa-2--reintento-automático)
7. [Etapa 3 · Tiempo máximo con withTimeout](#7-etapa-3--tiempo-máximo-con-withtimeout)
8. [Etapa 4 · Comparación de tiempos](#8-etapa-4--comparación-de-tiempos)
9. [Etapa 5 · Rediseño profesional](#9-etapa-5--rediseño-profesional)
10. [Etapa 6 · Rediseño final con imagen de referencia](#10-etapa-6--rediseño-final-con-imagen-de-referencia)
11. [Lo que corregí, adapté o descarté](#11-lo-que-corregí-adapté-o-descarté)
12. [Verificaciones finales](#12-verificaciones-finales)
13. [Historial de commits](#13-historial-de-commits)
14. [Conclusiones](#14-conclusiones)

---

## 1. Tabla de prompts (formato pedido por la guía)

| Prompt que escribí | Qué respondió Gemini | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «**Etapa 1:** mueve la tarea con progreso al Repository como `fun tareaConProgreso(): Flow<Float>` (no suspend) con `flow { … emit(i / 100f) }.flowOn(Dispatchers.Default)`, y que el ViewModel solo haga `collect`, conservando el `Job`, `corriendo`, el doble inicio y el `finally`» | Agregó `tareaConProgreso()` a la interface, la implementó con `flow { }` + `flowOn(Dispatchers.Default)` y un `Log.d("HILO","Dentro: …")` en la primera vuelta. En el ViewModel cambió el `for` por `repo.tareaConProgreso().collect { _progreso.value = it }` | **Nada que corregir.** Revisé que no usara `withContext` dentro del `flow { }` y que el `finally` siguiera en el ViewModel | `withContext` dentro de un `flow { }` rompe la regla de preservación de contexto de Flow y lanza `IllegalStateException`; `flowOn` es la forma correcta. El trabajo ahora vive en el Repository, como dice el «Ojo con esto» de la guía |
| «**Etapa 2:** agrega `CargaUi.Reintentando(intento, maximo, esperaMs)` y una función `conReintentos` que pruebe hasta 3 veces con espera creciente (500 → 1000 ms), sin reintentar nunca una `CancellationException`, y muestra *Reintentando (intento 2 de 3)…* en la pantalla» | Creó `conReintentos<T>(maxIntentos = 3, esperaInicialMs = 500, bloque)` con un `while (true)`, `delay(espera)` y `espera *= 2`; relanza la `CancellationException` al inicio del `catch`; agregó la rama `Reintentando` al `when` y bloqueó los botones también en ese estado | **Lo acepté.** Comprobé el orden del `catch` y que el `if` de doble carga incluyera `Reintentando` | Reintentar una cancelación haría que el usuario cancele y la app siga trabajando. Si los botones se habilitaran durante `Reintentando`, se podrían lanzar dos cargas a la vez |
| «**Etapa 3:** interruptor *Límite de 2 s* con `withTimeout(LIMITE_MS)`; si se supera: *Tardó demasiado: se superó el límite de 2 s*. Atrapa `TimeoutCancellationException` **antes** de la regla que relanza `CancellationException`» | Agregó `limiteActivo`, `cambiarLimite()` y `LIMITE_MS = 2000L`; envolvió `conReintentos { bloque() }` en `withTimeout` solo si el límite está activo; puso `catch (e: TimeoutCancellationException)` antes del `catch (e: Exception)`. Agregó el segundo `Switch` | **Corregí mi revisión del commit:** el mensaje salió como `…2 s with withTimeout` (mezcló inglés). El código lo acepté tal cual | `TimeoutCancellationException` es subclase de `CancellationException`: si se relanzara primero, la app nunca mostraría *Tardó demasiado*. El mensaje del commit no lo reescribí porque ya estaba subido; lo documento aquí |
| «**Etapa 4:** `RegistroTiempo(modo, ms)`, lista de los últimos 10 tiempos, `limpiarTiempos()` y un resumen con `map` + `stateIn(WhileSubscribed(5000))`; tabla *N.º / Modo / Tiempo (ms)* sin cálculos en la View» | Agregó `RegistroTiempo` **y además** `ResumenTiempos` al Model; calculó promedios y "veces más rápido" en el ViewModel con protección contra división entre cero; creó la tarjeta *Comparación de tiempos* | **Acepté `ResumenTiempos`** aunque no lo pedí | Es más claro que devolver un `Triple` y deja el cálculo en el ViewModel, que es lo que exige MVVM |
| «**Etapa 5:** rediseño profesional: cero grises, nada de estilo IA, paleta azul marino/azul, tarjetas numeradas, botones de 52 dp, chip de estado, recuadros con franja, tabla con cabecera azul marino, conservando todos los textos de la guía» | Reescribió `PantallaTareas.kt` con componentes propios (`TarjetaPaso`, `BotonPrimario`, `BotonSecundario`, `MensajeChip`, `RecuadroFranja`, `InterruptorItem`), definió la paleta en `Color.kt`, el esquema claro fijo en `Theme.kt` y tamaños mayores en `Type.kt` | **Descarté** el contador grande con la palabra "toques" que yo mismo había pedido; Gemini dejó **Tócame: n** y lo mantuve. Verifiqué con Ctrl+F que no quedara `Gray` | La guía pide exactamente el botón **Tócame: n** como prueba de vida. Sin grises el diseño se ve serio; los deshabilitados usan azul claro (#D6E4F5) en lugar de gris |
| «**Etapa 6:** rediseño final siguiendo `REFERENCIA_INTERFAZ.png`: encabezado con icono de portapapeles, 3 indicadores (Módulos, Activos, Listos), tarjetas 01–04 con badges de color, miniceldas de Perfil/Pedidos/Tiempo, barras horizontales con el promedio real, tabla, Conclusiones y *Limpiar historial*, iconos VectorDrawable y sin tocar ViewModel, Repository, Model ni MainActivity» (prompt completo en la sección 10) | Rehízo `PantallaTareas.kt`, la paleta en `Color.kt`, el tema y la barra de estado en `Theme.kt`, la tipografía en `Type.kt`, y creó los íconos XML en `res/drawable` | Revisé que los indicadores y las barras salieran **solo** de los `StateFlow` existentes, que *Conclusiones* no dijera que el paralelo siempre gana y que siguieran todos los textos de la guía | La referencia era una maqueta con números de ejemplo: si la IA copiaba esos números la app mentiría. Con el ViewModel bloqueado, el rediseño no podía romper corrutinas, reintentos ni timeouts |
| «Verifica que no exista código prohibido con `git grep -n -E "GlobalScope\|runBlocking\|Thread.sleep" -- "*.kt"`» (paso de cada etapa) | No devolvió resultados en ninguna etapa | — | Es una prueba de aceptación de la Fase 1 que también aplica a la Fase 2 |
| *(Decisión propia, sin prompt)* Android Studio dejó cambios sin commit en `.idea/`, `gradlew.bat` y `gradle-wrapper.properties` | — | Los **descarté** con `git checkout --` | Comprobé con `git diff --ignore-cr-at-eol` que solo eran saltos de línea (CRLF) de Windows; no eran cambios reales |

---

## 2. Cómo trabajé con la IA

1. Abrí **View → Tool Windows → Gemini** y activé el **modo Agent**, para que pudiera leer el proyecto y editar los archivos.
2. Escribí **un solo prompt** con las 5 mejoras divididas en **etapas** (sección 4). Le pedí que hiciera **una etapa por vez**, que hiciera su commit y su push, y que **se detuviera** hasta que yo escribiera `siguiente`.
3. En cada etapa:
   - leí los archivos que cambió antes de aceptarlos;
   - ejecuté la app en el celular y probé lo que pedía la etapa;
   - revisé que no hubiera código prohibido;
   - recién ahí escribí `siguiente`.
4. Al final revisé el proyecto completo (sección 11) y descarté los cambios de saltos de línea que generó Android Studio.

5. Para el **rediseño final (etapa 6)** usé **ChatGPT** con una imagen de referencia (`REFERENCIA_INTERFAZ.png`), 3 capturas del estado actual de la app y el zip del proyecto. Le prohibí tocar cualquier archivo que no fuera de la interfaz.

**Por qué un solo prompt por etapas:** así Gemini tenía todo el contexto desde el inicio (reglas, arquitectura y mensajes de commit), pero yo seguía controlando cada paso, y cada mejora quedó en **su propio commit**, como pide la rúbrica.

---

## 3. Reglas que le impuse a la IA

| # | Regla | Por qué |
|---|---|---|
| R1 | Prohibido `GlobalScope`, `runBlocking` y `Thread.sleep` | Son las prácticas que la guía castiga: corrutinas sin dueño, bloqueo del hilo y congelamiento de la pantalla |
| R2 | Toda `CancellationException` se relanza; nunca se traga ni se reintenta | Si se traga, `cancel()` deja de funcionar y la corrutina sigue viva |
| R3 | MVVM estricto: la View solo observa `StateFlow` y llama al ViewModel | La capa View no debe lanzar corrutinas ni calcular nada |
| R4 | No romper nada de la Fase 1 (primos, tiempos, cancelación, rotación, logs HILO) | La Fase 2 mejora; no reemplaza lo que ya estaba aprobado |
| R5 | Cambiar solo los archivos indicados en cada etapa y que compile al final de cada una | Commits pequeños y revisables; si algo falla se sabe en qué etapa fue |
| R6 | Sin dependencias nuevas salvo que fuera imprescindible | Menos riesgo de errores de Gradle; Gemini no agregó ninguna |

---

## 4. El prompt completo (etapas 1 a 5 · Gemini)

Este es el prompt exacto que pegué en Gemini:

```text
Eres un desarrollador Android senior. Vas a mejorar mi proyecto "CentroTareas" en 5 ETAPAS. Cada etapa = 1 mejora = 1 commit = 1 push. Trabaja SOLO una etapa a la vez.

=====================================================================
CONTEXTO DEL PROYECTO
=====================================================================
- Android, Kotlin + Jetpack Compose + Material 3 (sin XML). Paquete: com.maldonado.centrotareas
- Arquitectura MVVM + Repository:
  - model/Estados.kt -> sealed interface PrimosUi (Inicial, Calculando, Listo, Cancelado) y CargaUi (Inicial, Cargando, Exito(modo, perfil, pedidos, milisegundos), Error(mensaje))
  - data/TareasRepository.kt (interface) y data/TareasRepositoryLocal.kt (cargarPerfil 2 s, cargarPedidos 3 s con IOException("Sin conexión") si simularError, contarPrimos en Dispatchers.Default con ensureActive)
  - viewmodel/TareasViewModel.kt -> viewModelScope + StateFlow: tareaLarga/cancelar (Job, corriendo con finally, progreso 0f..1f, logs con tag "HILO"), contar/cancelarPrimos, cargarUnaPorUna/cargarALaVez (async + coroutineScope) usando un helper privado cargar(modo, bloque) con try/catch, simularError y reintentar()
  - viewmodel/TareasViewModelFactory.kt
  - ui/PantallaTareas.kt -> 3 tarjetas: "Tarea con progreso", "Contar números primos", "Dos cargas"
  - ui/theme/ (Theme.kt, Color.kt, Type.kt)
- Estoy en la rama git "mejora-ia" (ya existe en el remoto).

=====================================================================
REGLAS QUE APLICAN A TODAS LAS ETAPAS (NO NEGOCIABLES)
=====================================================================
1. PROHIBIDO usar GlobalScope, runBlocking y Thread.sleep.
2. Toda CancellationException se vuelve a lanzar (throw e). Nunca se "traga" ni se reintenta.
3. Respeta MVVM: la View (ui/) solo observa StateFlow con collectAsState y llama funciones del ViewModel; NO lanza corrutinas ni hace cálculos. El ViewModel solo conoce la interface del Repository; no recibe Context ni conoce Composables.
4. No rompas nada de la Fase 1: Iniciar/Cancelar, doble inicio bloqueado, primos (100->25, 1000->168, 10000->1229), Una por una ≈ 5 s, A la vez ≈ 3 s, Simular error + Reintentar, rotación sin perder el progreso, logs HILO (Antes: main / Dentro: DefaultDispatcher-worker / Despues: main).
5. Cambia SOLO los archivos que indica cada etapa. El proyecto debe compilar al final de cada etapa.
6. No agregues dependencias nuevas salvo que sea imprescindible; si lo haces, avísame.

=====================================================================
FLUJO DE TRABAJO POR ETAPA
=====================================================================
a) Aplica los cambios de la etapa.
b) Muéstrame un resumen corto: archivos modificados y qué cambió en cada uno.
c) Verifica que no exista código prohibido:  git grep -n -E "GlobalScope|runBlocking|Thread.sleep" -- "*.kt"   (no debe devolver nada)
d) Haz el commit y el push con EXACTAMENTE el mensaje indicado:
     git add .
     git commit -m "<mensaje de la etapa>"
     git push origin mejora-ia
   Si no puedes ejecutar comandos de terminal, dame los comandos exactos para que yo los ejecute.
e) Dime qué prueba hacer en el celular para comprobar la etapa.
f) DETENTE y espera a que yo escriba "siguiente". No empieces la siguiente etapa sin mi confirmación.

=====================================================================
ETAPA 1 · Progreso desde el Repository (Flow)
Archivos: data/TareasRepository.kt, data/TareasRepositoryLocal.kt, viewmodel/TareasViewModel.kt
=====================================================================
- En TareasRepository agrega: fun tareaConProgreso(): Flow<Float>   (NO suspend)
- En TareasRepositoryLocal: flow { for (i in 1..100) { delay(40); emit(i / 100f) } }.flowOn(Dispatchers.Default)
  En i == 1 escribe Log.d("HILO", "Dentro: ${Thread.currentThread().name}").
  No uses withContext dentro del flow { } (rompe la regla de contexto de Flow): usa flowOn.
- En TareasViewModel.tareaLarga(): conserva el if (_corriendo.value) return, _corriendo, el Job "trabajo" y el finally.
  Dentro del launch: progreso 0f, mensaje "Trabajando...", Log Antes, repo.tareaConProgreso().collect { _progreso.value = it }, Log Despues, mensaje "Terminó".
  Elimina el for/delay/withContext de esta tarea en el ViewModel. cancelar() queda igual.
Commit: feat(repo): mueve la tarea con progreso al Repository como Flow con flowOn

=====================================================================
ETAPA 2 · Reintento automático con espera creciente
Archivos: model/Estados.kt, viewmodel/TareasViewModel.kt, ui/PantallaTareas.kt
=====================================================================
- En CargaUi agrega: data class Reintentando(val intento: Int, val maximo: Int, val esperaMs: Long) : CargaUi
- En el ViewModel: private suspend fun <T> conReintentos(maxIntentos: Int = 3, esperaInicialMs: Long = 500, bloque: suspend () -> T): T
  Si el bloque falla con una excepción normal y quedan intentos: publica CargaUi.Reintentando(siguiente, max, espera), delay(espera), duplica la espera (500 ms, 1000 ms).
  Si se acaban los intentos, relanza la última excepción (el catch de cargar() muestra el Error).
  CancellationException se relanza de inmediato, nunca se reintenta.
- En cargar(): val (perfil, pedidos) = conReintentos { bloque() }. El tiempo medido incluye los reintentos.
- En la pantalla, tarjeta "Dos cargas": rama del when para Reintentando con un CircularProgressIndicator pequeño y "Reintentando (intento 2 de 3)... espera 500 ms". Mientras Reintentando, los botones Una por una / A la vez siguen deshabilitados (igual que Cargando).
- El botón "Reintentar" manual del estado Error debe seguir funcionando.
Commit: feat(errores): reintenta las cargas hasta 3 veces con espera creciente

=====================================================================
ETAPA 3 · Tiempo máximo con withTimeout
Archivos: viewmodel/TareasViewModel.kt, ui/PantallaTareas.kt
=====================================================================
- En el ViewModel: StateFlow<Boolean> limiteActivo (false por defecto), fun cambiarLimite(valor: Boolean), constante LIMITE_MS = 2000L.
- En cargar(): si limiteActivo es true, todo (incluidos los reintentos) va dentro de withTimeout(LIMITE_MS) { ... }; si es false, sin límite.
- Si se supera: CargaUi.Error("Tardó demasiado: se superó el límite de 2 s").
- MUY IMPORTANTE: TimeoutCancellationException es subclase de CancellationException. Atrápala ANTES de la regla que relanza CancellationException; cualquier otra CancellationException se sigue relanzando. conReintentos NO debe reintentar el timeout.
- En la pantalla, tarjeta "Dos cargas": un segundo Switch debajo de "Simular error" con el texto "Límite de 2 s".
Commit: feat(carga): agrega tiempo maximo de 2 s con withTimeout

=====================================================================
ETAPA 4 · Comparación de tiempos
Archivos: model/Estados.kt, viewmodel/TareasViewModel.kt, ui/PantallaTareas.kt
=====================================================================
- En Estados.kt: data class RegistroTiempo(val modo: String, val milisegundos: Long)
- En el ViewModel: StateFlow<List<RegistroTiempo>> tiempos (solo lectura); cada Exito agrega un registro (máximo los últimos 10); fun limpiarTiempos().
  Expón también un StateFlow de resumen (promedio "Una por una", promedio "A la vez" y cuántas veces más rápido es "A la vez") calculado con map + stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), valorInicial). Sin división entre cero.
- En la pantalla: nueva tarjeta "Comparación de tiempos" debajo de "Dos cargas" con tabla (N.º | Modo | Tiempo (ms)), fila de resumen con los promedios y el texto "A la vez fue X veces más rápido" (valor real con 1 decimal), botón "Limpiar", y si no hay datos: "Aún no hay tiempos. Prueba Una por una y A la vez."
- La View NO calcula nada: solo muestra lo que expone el ViewModel.
Commit: feat(comparacion): agrega tabla que compara tiempos de una por una y a la vez

=====================================================================
ETAPA 5 · Rediseño visual profesional
Archivos: ui/PantallaTareas.kt, ui/theme/Theme.kt, ui/theme/Color.kt, ui/theme/Type.kt (MainActivity.kt solo si hace falta para la barra de estado). NO toques viewmodel/, data/ ni model/.
=====================================================================
Estilo de app corporativa/fintech seria, grande y clara.
- CERO gris neutro: elimina Color.Gray, LightGray, DarkGray y cualquier gris (#888, #9E9E9E, #BDBDBD...). Texto secundario en azul pizarra #3A5A80. Botones deshabilitados con fondo #D6E4F5 y texto #5B7BA5 (ButtonDefaults.buttonColors / outlinedButtonColors).
- NADA de "estilo IA": sin degradados morados o neón, sin brillos, sin emojis, sin íconos de chispas o estrellas.
- Paleta: barra superior azul marino #0B2545, principal #1B6CA8, acento #2E86DE, fondo #F4F7FB, tarjetas blancas, éxito #0F8A5F sobre #E6F6EF, error #C62828 sobre #FDECEC, texto principal #0B1F3A. Tema claro fijo con lightColorScheme completo, sin colores dinámicos.
- Barra superior con título "CentroTareas" en negrita y subtítulo "Hilos y corrutinas en Android".
- Tarjetas: esquinas 20 dp, padding 20 dp, elevación 2-3 dp, borde fino #DCE6F2. Cabecera con número en círculo azul (1, 2, 3, 4), título en titleLarge negrita y una línea descriptiva corta debajo.
- Botones: alto 52 dp, esquinas 14 dp, texto negrita 16 sp, en filas con weight(1f).
- "Tócame" como contador destacado: número grande (headlineMedium) y debajo "toques".
- Barra de progreso de 12 dp con esquinas redondeadas; porcentaje grande (headlineMedium, azul); el mensaje como chip de color (Trabajando azul, Terminó verde, Cancelado rojo, Listo azul claro).
- Resultados en recuadros con franja de color a la izquierda (verde éxito / rojo error), datos etiqueta–valor y el tiempo resaltado.
- Interruptores: texto a la izquierda, Switch a la derecha, colores del tema.
- Tabla de comparación: cabecera azul marino con texto blanco, filas alternadas blanco / #EEF4FB, fila de resumen resaltada.
- Type.kt: titleLarge 22 sp, bodyLarge 17 sp, labelLarge 16 sp.
- CONSERVA EXACTAMENTE estos textos: "CentroTareas", "Tarea con progreso", "Tócame", "Iniciar", "Cancelar", "Contar números primos", "Hasta el número", "Contar", "Dos cargas", "Una por una", "A la vez", "Simular error", "Límite de 2 s", "Reintentar", "Comparación de tiempos", "Limpiar".
- Conserva los estados enabled de todos los botones, rememberSaveable del contador y del campo, y el scroll vertical (que se vea bien al rotar).
- Debe compilar sin dependencias nuevas; si quieres íconos de Material y hace falta una dependencia, avísame antes o no los uses.
Commit: style(ui): redisena la interfaz con estilo profesional y tema azul

=====================================================================
AL TERMINAR LA ETAPA 5
=====================================================================
Dame una tabla resumen con: etapa, archivos tocados, mensaje del commit, y cualquier decisión tuya que yo deba revisar.

Empieza AHORA con la ETAPA 1 y detente al terminarla.
```

---

## 5. Etapa 1 · Progreso desde el Repository

**Objetivo:** que el trabajo simulado viva en el **Repository** y no en el ViewModel. La guía lo advierte en el «Ojo con esto»: en una app real el trabajo pesado va en el Repository y el ViewModel solo lo llama.

**Archivos:** `TareasRepository.kt`, `TareasRepositoryLocal.kt`, `TareasViewModel.kt`

**Lo que generó Gemini (Repository):**
```kotlin
override fun tareaConProgreso(): Flow<Float> = flow {
    for (i in 1..100) {
        if (i == 1) Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
        delay(40)
        emit(i / 100f)
    }
}.flowOn(Dispatchers.Default)
```

**Lo que generó Gemini (ViewModel, dentro del `launch`):**
```kotlin
repo.tareaConProgreso().collect { progreso ->
    _progreso.value = progreso
}
```

**Qué revisé:**
- ✅ `tareaConProgreso()` **no** es `suspend`: un Flow es frío y solo trabaja cuando alguien lo recolecta.
- ✅ Usa `flowOn(Dispatchers.Default)` y no `withContext` dentro del `flow { }`.
- ✅ Se conservan el `if (_corriendo.value) return`, el `Job trabajo` y el `finally { _corriendo.value = false }`.
- ✅ `cancelar()` sigue funcionando: al cancelar el `Job` se cancela el `collect` y, con él, el Flow.

**Prueba en el celular:** Iniciar (0 → 100 % en ≈ 4 s), Cancelar a mitad (se queda en el %, dice *Cancelado*) y Logcat `tag:HILO` (`Antes: main` → `Dentro: DefaultDispatcher-worker-…` → `Despues: main`). ✅

**Qué cambié o descarté:** nada.

**Por qué es mejor:** el `emit` ocurre en un hilo de fondo gracias a `flowOn`, pero el `collect` y la actualización del `StateFlow` ocurren en el hilo principal (`viewModelScope`). El ViewModel ya no sabe *cómo* se hace la tarea; solo la escucha.

---

## 6. Etapa 2 · Reintento automático

**Objetivo:** reintentar hasta 3 veces con una espera creciente antes de mostrar el error.

**Archivos:** `Estados.kt`, `TareasViewModel.kt`, `PantallaTareas.kt`

**Lo que generó Gemini:**
```kotlin
private suspend fun <T> conReintentos(
    maxIntentos: Int = 3,
    esperaInicialMs: Long = 500,
    bloque: suspend () -> T
): T {
    var espera = esperaInicialMs
    var intento = 1
    while (true) {
        try {
            return bloque()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            if (intento >= maxIntentos) throw e
            val siguiente = intento + 1
            _carga.value = CargaUi.Reintentando(siguiente, maxIntentos, espera)
            delay(espera)
            espera *= 2
            intento++
        }
    }
}
```

**Qué revisé:**
- ✅ La primera línea del `catch` relanza la `CancellationException`: una cancelación **nunca** se reintenta.
- ✅ La espera es con `delay`, no con `Thread.sleep`.
- ✅ Esperas de 500 ms y 1000 ms: son 3 intentos y 2 esperas.
- ✅ El `if` de doble carga en `cargar()` incluye `Reintentando`, y en la pantalla `cargando` también es verdadero en ese estado.
- ✅ El `when` de la pantalla tiene la nueva rama (sin ella no compilaba, porque `CargaUi` es `sealed`).

**Prueba en el celular:** con **Simular error** activado, **A la vez** muestra *Reintentando (intento 2 de 3)… espera 500 ms*, luego *3 de 3… espera 1000 ms* y al final el recuadro rojo con **Reintentar**. Al apagar el interruptor y pulsar **Reintentar**, sale verde. ✅

**Qué cambié o descarté:** nada.

**Tiempo con error:** 3 intentos × 3 s + 0,5 s + 1 s ≈ **10,5 s** en *A la vez* y ≈ 16,5 s en *Una por una*. Es correcto: el tiempo medido incluye los reintentos, como pedí.

---

## 7. Etapa 3 · Tiempo máximo con withTimeout

**Objetivo:** si las cargas tardan más de 2 s, mostrar **Tardó demasiado**.

**Archivos:** `TareasViewModel.kt`, `PantallaTareas.kt`

**Decisión de diseño (mía, en el prompt):** el límite es **opcional**, con el interruptor *Límite de 2 s*. Como el perfil tarda 2 s y los pedidos 3 s, con un límite fijo **todas** las cargas fallarían y se perdería la comparación de la Fase 1.

**Lo que generó Gemini:**
```kotlin
try {
    val (perfil, pedidos) = if (_limiteActivo.value) {
        withTimeout(LIMITE_MS) { conReintentos { bloque() } }
    } else {
        conReintentos { bloque() }
    }
    // ... Exito
} catch (e: TimeoutCancellationException) {
    _carga.value = CargaUi.Error("Tardó demasiado: se superó el límite de 2 s")
} catch (e: Exception) {
    if (e is CancellationException) throw e
    _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
}
```

**Qué revisé (la parte más delicada de toda la fase):**
- ✅ `catch (e: TimeoutCancellationException)` está **antes** del `catch (e: Exception)`. Como `TimeoutCancellationException` hereda de `CancellationException`, si fuera al revés se relanzaría y el usuario nunca vería *Tardó demasiado*.
- ✅ `conReintentos` no reintenta el timeout, porque también es una `CancellationException` y se relanza en su primera línea.
- ✅ Cualquier otra cancelación se sigue relanzando.

**Prueba en el celular:** con **Límite de 2 s** activado, **A la vez** muestra a los ≈ 2 s *Tardó demasiado: se superó el límite de 2 s*. Al apagarlo vuelve a funcionar en ≈ 3 s. ✅

**Qué cambié o descarté:** el **mensaje del commit** salió como `feat(carga): agrega tiempo maximo de 2 s with withTimeout` (Gemini mezcló "with" en inglés en lugar de "con"). **No lo reescribí** porque ya estaba subido a GitHub, y cambiarlo exigía reescribir el historial con `push --force`. El código está correcto.

---

## 8. Etapa 4 · Comparación de tiempos

**Objetivo:** una tabla que acumule los tiempos de **Una por una** y **A la vez** para ver la diferencia.

**Archivos:** `Estados.kt`, `TareasViewModel.kt`, `PantallaTareas.kt`

**Lo que generó Gemini (ViewModel):**
```kotlin
val resumen: StateFlow<ResumenTiempos> = _tiempos.map { lista ->
    val unaPorUna = lista.filter { it.modo == "Una por una" }
    val aLaVez = lista.filter { it.modo == "A la vez" }
    val promUna = if (unaPorUna.isNotEmpty()) unaPorUna.map { it.milisegundos }.average() else 0.0
    val promALaVez = if (aLaVez.isNotEmpty()) aLaVez.map { it.milisegundos }.average() else 0.0
    val veces = if (promALaVez > 0.0) promUna / promALaVez else 0.0
    ResumenTiempos(promUna, promALaVez, veces)
}.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ResumenTiempos())
```
Y en `cargar()`, después de cada éxito:
```kotlin
_tiempos.value = (_tiempos.value + RegistroTiempo(modo, ms)).takeLast(10)
```

**Qué revisé:**
- ✅ Los promedios y el "veces más rápido" se calculan en el **ViewModel**; la View solo los muestra.
- ✅ No hay división entre cero (`if (promALaVez > 0.0)`).
- ✅ Solo se guardan los éxitos y como máximo los últimos 10.
- ✅ `stateIn` usa `viewModelScope`, así que no hay corrutinas sin dueño.

**Prueba en el celular:** 2 veces **Una por una** y 2 veces **A la vez** → 4 filas, promedios ≈ 5002 ms y ≈ 3002 ms, y *A la vez fue 1.7 veces más rápido*. **Limpiar** vacía la tabla. ✅

**Qué cambié o descarté:**
- **Acepté** la clase extra `ResumenTiempos`, que no pedí. Es más legible que un `Triple<Double, Double, Double>`.
- El número sale con punto (`1.7`) porque usa `Locale.US`. Lo dejé así: es un detalle de formato y no afecta la funcionalidad.

---

## 9. Etapa 5 · Rediseño profesional

**Objetivo:** una interfaz seria y grande, **sin grises** y **sin estilo IA**.

**Archivos:** `PantallaTareas.kt`, `Color.kt`, `Theme.kt`, `Type.kt` (no tocó `viewmodel/`, `data/` ni `model/`).

**Lo que generó Gemini:**

| Pieza | Resultado |
|---|---|
| Paleta (`Color.kt`) | `AzulMarino #0B2545`, `AzulPrincipal #1B6CA8`, `AzulSecundario #3A5A80`, `AzulFondo #F4F7FB`, éxito `#0F8A5F/#E6F6EF`, error `#C62828/#FDECEC`, deshabilitado `#D6E4F5/#5B7BA5` |
| Tema (`Theme.kt`) | `lightColorScheme` fijo, sin colores dinámicos |
| Tipografía (`Type.kt`) | `titleLarge` 22 sp, `bodyLarge` 17 sp, `labelLarge` 16 sp |
| `TarjetaPaso` | Tarjeta blanca de 20 dp con borde `#DCE6F2`, número en círculo azul, título y descripción |
| `BotonPrimario` / `BotonSecundario` | 52 dp de alto, esquinas de 14 dp, deshabilitados en azul claro |
| `MensajeChip` | Chip de color según el estado: Trabajando, Terminó, Cancelado y Listo |
| `RecuadroFranja` | Resultado con franja de color a la izquierda (verde o rojo) |
| `InterruptorItem` | Texto a la izquierda y `Switch` a la derecha |
| Tabla de comparación | Cabecera azul marino, filas alternadas y resumen resaltado |

**Qué revisé:**
- ✅ **Ctrl + F → `Gray`:** sin resultados en `ui/`.
- ✅ Están todos los textos de la guía: CentroTareas, Tarea con progreso, Tócame, Iniciar, Cancelar, Contar números primos, Hasta el número, Contar, Dos cargas, Una por una, A la vez, Simular error, Límite de 2 s, Reintentar, Comparación de tiempos y Limpiar.
- ✅ Los estados `enabled` siguen iguales.
- ✅ Se mantienen `rememberSaveable` y el scroll; al rotar no se pierde el progreso.
- ✅ No agregó dependencias (no usó íconos de Material).

**Qué cambié o descarté:**
- **Descarté mi propia idea del contador grande con la palabra "toques".** Gemini mantuvo el botón **Tócame: n** y lo dejé así, porque es exactamente lo que pide la guía como *prueba de vida*.
- El color del chip se decide con `mensaje.contains("…")` dentro de la View. Es una decisión visual, no de lógica de negocio, así que lo acepté. Una mejora futura sería que el ViewModel exponga un `enum` de estado.

---

## 10. Etapa 6 · Rediseño final con imagen de referencia

**Objetivo:** llevar la interfaz al nivel de la maqueta `REFERENCIA_INTERFAZ.png`, que muestra encabezado con logotipo, indicadores de actividad, cuatro tarjetas con badges de color, gráficas de barras, tabla y conclusiones, **sin tocar la lógica**.

**Herramienta:** ChatGPT, con 5 adjuntos:

| Adjunto | Para qué |
|---|---|
| `REFERENCIA_INTERFAZ.png` | El diseño deseado (dos partes de una misma pantalla con scroll) |
| `actual_1.png` | La app antes del cambio: encabezado y tarjetas 1–2 |
| `actual_2.png` | La tarjeta *Dos cargas* con los dos interruptores |
| `actual_3.png` | La tarjeta *Comparación de tiempos* con datos |
| `CentroTareas.zip` | El proyecto real, exportado con *File → Export → Export to Zip File* |

### El prompt completo de la etapa 6

```text
# REDISEÑO PROFESIONAL DE CENTROTAREAS — ANDROID (Kotlin + Jetpack Compose + Material 3)

Actúa como diseñador UI/UX senior y desarrollador Android experto en Kotlin, Jetpack Compose, Material Design 3, hilos y corrutinas.

Te adjunto:
- CentroTareas.zip → el proyecto REAL (rama git "mejora-ia").
- REFERENCIA_INTERFAZ.png → el diseño deseado, en dos teléfonos.
- actual_1.png, actual_2.png, actual_3.png → cómo se ve la app HOY (para que sepas qué conservar).

Implementa el rediseño DIRECTAMENTE en los archivos reales del proyecto.
No generes solo una maqueta. No reconstruyas la app desde cero. No modifiques su lógica.

=====================================================================
0. CONTEXTO EXACTO DEL PROYECTO (no lo cambies)
=====================================================================
Paquete: com.maldonado.centrotareas · Arquitectura MVVM + Repository · minSdk 24.

El ViewModel (viewmodel/TareasViewModel.kt) expone EXACTAMENTE esto (úsalo tal cual):
- StateFlow: mensaje: String ("Listo" | "Trabajando..." | "Terminó" | "Cancelado"), progreso: Float (0f..1f), corriendo: Boolean
- StateFlow: primos: PrimosUi  (Inicial | Calculando | Listo(cantidad: Int, milisegundos: Long) | Cancelado)
- StateFlow: carga: CargaUi   (Inicial | Cargando | Reintentando(intento, maximo, esperaMs) | Exito(modo, perfil, pedidos, milisegundos) | Error(mensaje))
- StateFlow: simularError: Boolean, limiteActivo: Boolean
- StateFlow: tiempos: List<RegistroTiempo(modo: String, milisegundos: Long)>
- StateFlow: resumen: ResumenTiempos(promedioUnaPorUna: Double, promedioALaVez: Double, vecesMasRapido: Double)
- Funciones: tareaLarga(), cancelar(), contar(hasta: Int), cancelarPrimos(), cargarUnaPorUna(), cargarALaVez(), reintentar(), cambiarSimularError(Boolean), cambiarLimite(Boolean), limpiarTiempos()

=====================================================================
1. REFERENCIA VISUAL
=====================================================================
Analiza REFERENCIA_INTERFAZ.png.
Teléfono izquierdo: encabezado con logotipo y título, tres indicadores de actividad, Tarjeta 01 (tarea con progreso) y Tarjeta 02 (contador de primos).
Teléfono derecho: Tarjeta 03 (carga de datos), Tarjeta 04 (comparación de tiempos) con gráficas horizontales, tabla de resultados y panel de conclusiones.
Los dos teléfonos son partes de UNA ÚNICA pantalla con desplazamiento vertical, no dos páginas.
Los números de la maqueta son ilustrativos: TODO valor mostrado debe salir del ViewModel.

=====================================================================
2. PALETA
=====================================================================
Encabezado #0D2349 · Azul principal #2457E7 · Azul secundario #1686F8 · Fondo #F2F6FC · Tarjetas #FFFFFF
Texto principal #102549 · Texto secundario #637593 · Bordes #E0E8F3
Éxito #087B62 · Turquesa #0D9B9B · Violeta #7652D4 · Ámbar #E29A20 · Error #BF3B4E
- Colores vivos solo en lo que debe destacar.
- Sin neón, sin sombras excesivas, sin degradados innecesarios, sin emojis.
- PROHIBIDO Color.Gray / LightGray / DarkGray y grises neutros: los deshabilitados usan azul muy claro (fondo #DDE7F7, texto #7B8FB3).
- Define la paleta en ui/theme/Color.kt y un lightColorScheme fijo (sin colores dinámicos) en ui/theme/Theme.kt.

=====================================================================
3. ENCABEZADO
=====================================================================
- Fondo azul marino #0D2349 con transición azul MUY discreta.
- Icono de portapapeles blanco sobre un cuadro azul #2457E7 con esquinas redondeadas.
- Título "CentroTareas" en blanco y negrita; subtítulo "Hilos y corrutinas en Android".
- Respeta la barra de estado (WindowInsets.statusBars / statusBarsPadding). Los íconos de la barra de estado deben verse BLANCOS y legibles: hazlo en Theme.kt con un SideEffect + WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false (NO toques MainActivity.kt).

=====================================================================
4. INDICADORES DE ACTIVIDAD (3 tarjetas en fila)
=====================================================================
- Módulos: 4 (fijo).
- Activos: cuántas operaciones están corriendo = (corriendo ? 1 : 0) + (primos is Calculando ? 1 : 0) + (carga is Cargando o Reintentando ? 1 : 0).
- Listos: cuántos módulos tienen resultado final = (mensaje == "Terminó" ? 1 : 0) + (primos is Listo ? 1 : 0) + (carga is Exito ? 1 : 0) + (tiempos no vacío ? 1 : 0).
- Calcúlalos en la View solo a partir de esos StateFlow (son derivaciones directas; NO inventes estados nuevos ni toques el ViewModel).
- Fondo blanco, borde suave, icono pequeño, número grande y etiqueta.

=====================================================================
5. TARJETA 01 — Tarea con progreso
=====================================================================
Badge azul "01", título "Tarea con progreso", subtítulo descriptivo.
Botón "Tócame: N" (contador con rememberSaveable; debe seguir respondiendo mientras la tarea corre).
Barra horizontal de progreso azul que se adapta al ancho, porcentaje real ((progreso*100) redondeado) y estado de ejecución como chip:
Trabajando = azul, Terminó = verde, Cancelado = rojo suave, Listo = azul claro.
Botón azul "Iniciar" (enabled = !corriendo) → vm.tareaLarga().
Botón secundario "Cancelar" (enabled = corriendo) → vm.cancelar().

=====================================================================
6. TARJETA 02 — Contador de primos
=====================================================================
Badge turquesa "02". Campo numérico profesional con etiqueta "Hasta el número" (solo dígitos, máx. 9 cifras, rememberSaveable).
Validación: botón "Contar" habilitado solo si el número es >= 2 y no está calculando → vm.contar(numero).
"Cancelar" habilitado solo mientras primos is Calculando → vm.cancelarPrimos().
Durante el cálculo: indicador circular.
Al finalizar (PrimosUi.Listo): panel verde claro con cantidad real de primos, número límite usado (guárdalo en un rememberSaveable al pulsar Contar) y tiempo en ms.
Si PrimosUi.Cancelado: mensaje en rojo suave "Cancelado".

=====================================================================
7. TARJETA 03 — Carga de datos
=====================================================================
Badge violeta "03" e icono de base de datos.
Dos botones principales: "Una por una" → vm.cargarUnaPorUna() y "A la vez" → vm.cargarALaVez(), ambos deshabilitados mientras carga is Cargando o Reintentando.
Conserva los interruptores "Simular error" (vm.cambiarSimularError) y "Límite de 2 s" (vm.cambiarLimite): texto a la izquierda, Switch a la derecha.
Exito: tres miniceldas → Perfil (estado.perfil), Pedidos (estado.pedidos), Tiempo (estado.milisegundos ms), más el modo usado (estado.modo).
Reintentando: indicador y texto "Reintentando (intento X de Y)... espera Z ms".
Error: mensaje legible en recuadro rojo suave (incluye "Tardó demasiado..." y "No se pudo cargar: ...") y botón "Reintentar" → vm.reintentar().

=====================================================================
8. TARJETA 04 — Comparación de tiempos
=====================================================================
Badge ámbar "04".
Con datos: dos barras horizontales (azul = "Una por una", turquesa = "A la vez"); la longitud de cada una = su promedio / el mayor de los dos promedios (resumen). Si un modo aún no tiene datos, muestra "Sin datos" en esa barra.
Tabla sin desbordar el ancho: N.º | Método | Tiempo (ms), con los registros de vm.tiempos.
Apartado "Conclusiones" desde vm.resumen:
 - si hay ambos promedios y vecesMasRapido > 1 → "A la vez fue X,X veces más rápido que Una por una en tus pruebas".
 - si vecesMasRapido está entre 0 y 1 → "En tus pruebas Una por una fue más rápida".
 - si falta algún modo → "Ejecuta ambos modos para comparar".
 NO afirmes que el paralelo siempre gana: usa solo las mediciones.
Botón "Limpiar historial" → vm.limpiarTiempos().
Sin datos: estado vacío atractivo (icono de gráfica + texto "Aún no hay tiempos. Prueba Una por una y A la vez."), nunca tiempos inventados.

=====================================================================
9. ICONOS, TARJETAS Y ESPACIADOS
=====================================================================
Iconos vectoriales coherentes como VectorDrawable XML en app/src/main/res/drawable, usados con painterResource:
ic_portapapeles.xml, ic_play.xml, ic_cancelar.xml, ic_calculadora.xml, ic_cronometro.xml, ic_base_datos.xml, ic_perfil.xml, ic_pedidos.xml, ic_grafica.xml, ic_check.xml, ic_error.xml.
NO agregues dependencias nuevas (nada de material-icons-extended).
Tarjetas blancas de 20–22 dp de radio, borde #E0E8F3, elevación leve; botones de 48 dp de alto; separación de 14–16 dp. Sin fotografías.

=====================================================================
10. PROHIBIDO MODIFICAR
=====================================================================
TareasViewModel.kt, TareasRepository.kt, TareasRepositoryLocal.kt, Estados.kt, TareasViewModelFactory.kt, MainActivity.kt, build.gradle.kts y libs.versions.toml.
Nada de cambiar StateFlow, corrutinas, cancelaciones, reintentos, timeouts, primos ni cálculos de tiempos/promedios.
Prohibido GlobalScope, runBlocking y Thread.sleep. La View no lanza corrutinas.
Solo cambias: ui/PantallaTareas.kt, ui/theme/Color.kt, ui/theme/Theme.kt, ui/theme/Type.kt y los XML nuevos de res/drawable.

TEXTOS QUE DEBEN EXISTIR TAL CUAL (los evalúa la guía):
"CentroTareas", "Tarea con progreso", "Tócame: N", "Iniciar", "Cancelar", "Contar números primos", "Hasta el número", "Contar", "Dos cargas" (puede ir como subtítulo de la Tarjeta 03), "Una por una", "A la vez", "Simular error", "Límite de 2 s", "Reintentar", "Comparación de tiempos", "Limpiar historial".

=====================================================================
11. ADAPTABILIDAD Y CALIDAD
=====================================================================
Debe verse bien en teléfonos pequeños y grandes, y en horizontal (al rotar no se pierde nada: usa rememberSaveable donde ya existe).
Sin textos cortados, sin botones superpuestos, barras adaptadas al ancho, tabla sin desbordamiento, scroll vertical correcto, la barra de navegación inferior no tapa contenido (navigationBarsPadding), errores legibles y todos los controles responden.

=====================================================================
12. VERIFICACIÓN Y ENTREGA
=====================================================================
Prueba (o revisa el código para garantizar):
1. Iniciar y cancelar la tarea con progreso.  2. Tócame responde mientras corre.  3. Calcular y cancelar primos (1000 → 168).
4. Una por una ≈ 5000 ms y A la vez ≈ 3000 ms.  5. Simular error → reintentos → Error → Reintentar.  6. Límite de 2 s → "Tardó demasiado".
7. Gráficas y tabla con valores reales.  8. Limpiar historial.  9. Compila sin errores.
10. git grep -n -E "GlobalScope|runBlocking|Thread.sleep" -- "*.kt" no devuelve nada.

ENTREGA:
- El proyecto completo en ZIP.
- El código COMPLETO de cada archivo modificado o creado (PantallaTareas.kt, Color.kt, Theme.kt, Type.kt y cada XML de drawable).
- Un resumen de cambios: archivo → qué cambió.
- Mensaje de commit sugerido: style(ui): redisena la interfaz con indicadores, iconos y graficas de comparacion
```

### Por qué el prompt es tan detallado

| Decisión en el prompt | Motivo |
|---|---|
| Lista exacta de `StateFlow` y funciones del ViewModel | Para que la IA no invente funciones como `vm.iniciar()` que no existen y rompan la compilación |
| *Activos* y *Listos* derivados de los `StateFlow` existentes | La maqueta tenía números de ejemplo; tenían que ser reales y sin crear estados nuevos en el ViewModel |
| Barra de estado clara desde `Theme.kt` con `WindowCompat` | `MainActivity.kt` estaba prohibido, pero los íconos de la barra de estado tenían que verse sobre el azul marino |
| Barras = promedio / mayor promedio | Así la longitud representa el tiempo real y no un valor fijo de la maqueta |
| *Conclusiones* con 3 casos | Para no afirmar que el paralelo siempre gana si las mediciones dicen otra cosa |
| Íconos como VectorDrawable XML | Evita agregar `material-icons-extended` (dependencia pesada) |
| Lista de textos que deben existir tal cual | Son los que evalúa la guía: Tócame, Iniciar, Cancelar, Contar, Una por una, A la vez, Simular error, Reintentar… |
| Archivos prohibidos (ViewModel, Repository, Model, Factory, MainActivity, Gradle) | El rediseño no podía romper corrutinas, cancelaciones, reintentos ni timeouts ya probados |

### Qué entregó la IA

| Archivo | Cambio |
|---|---|
| `ui/PantallaTareas.kt` | Encabezado con icono, fila de 3 indicadores, tarjetas 01–04 con badges azul/turquesa/violeta/ámbar, miniceldas, barras horizontales, tabla, *Conclusiones* y estado vacío |
| `ui/theme/Color.kt` | Nueva paleta: `#0D2349`, `#2457E7`, `#1686F8`, `#F2F6FC`, `#087B62`, `#0D9B9B`, `#7652D4`, `#E29A20`, `#BF3B4E` |
| `ui/theme/Theme.kt` | `lightColorScheme` fijo y los íconos de la barra de estado en blanco |
| `ui/theme/Type.kt` | Jerarquía tipográfica para los títulos, los números grandes y las etiquetas |
| `res/drawable/ic_*.xml` | Íconos vectoriales: portapapeles, play, cancelar, calculadora, cronómetro, base de datos, perfil, pedidos, gráfica, check y error |

### Qué revisé

- ✅ No cambió ningún archivo prohibido: con `git status`, solo aparecían `ui/` y `res/drawable/`.
- ✅ *Activos* sube a 1 al pulsar **Iniciar** y vuelve a 0 al terminar; *Listos* sube con cada resultado.
- ✅ Las barras y la tabla muestran los milisegundos reales; **Limpiar historial** vuelve al estado vacío.
- ✅ *Conclusiones* cambia según las mediciones.
- ✅ **Tócame: N** sigue sumando mientras corre la tarea.
- ✅ "Tardó demasiado", los reintentos y **Reintentar** siguen funcionando.
- ✅ Sin `Gray`, sin dependencias nuevas, y `git grep` de código prohibido vacío.
- ✅ Al rotar no se pierde el progreso, y la barra de navegación no tapa el último botón.

**Qué cambié o descarté:** acepté la propuesta. Mantuve fuera del ViewModel el cálculo de *Activos* y *Listos*, porque son solo conteos visuales de estados que ya existen.

Commit: `style(ui): redisena la interfaz con indicadores, iconos y graficas de comparacion`

---

## 11. Lo que corregí, adapté o descarté

| # | Qué hizo la IA | Mi decisión | Motivo |
|---|---|---|---|
| 1 | Mensaje del commit 3 con "with" en inglés | Lo dejé y lo documenté | Ya estaba en GitHub; reescribir el historial con `--force` no valía la pena por una palabra |
| 2 | Agregó `ResumenTiempos` sin que se lo pidiera | Aceptado | Más claro que un `Triple` y mantiene el cálculo en el ViewModel |
| 3 | No hizo el contador grande con "toques" | Aceptado (descarté mi idea) | La guía pide **Tócame: n** |
| 4 | Color del chip decidido con `contains()` en la View | Aceptado con observación | Es presentación, no lógica de negocio |
| 5 | Formato `1.7` con punto (`Locale.US`) | Aceptado | Detalle menor de formato |
| 6 | Android Studio dejó cambios CRLF en `.idea/`, `gradlew.bat` y `gradle-wrapper.properties` | Descartados con `git checkout --` | `git diff --ignore-cr-at-eol` mostró que no había cambios reales |
| 7 | Límite de 2 s fijo, como dice la guía literalmente | Lo hice **opcional** con un interruptor (decisión mía en el prompt) | Con 2 s fijos todas las cargas fallarían (el perfil tarda 2 s y los pedidos 3 s) |
| 8 | La maqueta de referencia traía números de ejemplo (tiempos, conteos) | Exigí que todo saliera del ViewModel | Mostrar números inventados sería engañar al usuario y al evaluador |
| 9 | La maqueta decía que el paralelo es más rápido | *Conclusiones* calculadas con 3 casos | La conclusión debe depender de las mediciones, no de lo que se espera |

---

## 12. Verificaciones finales

| Verificación | Comando o prueba | Resultado |
|---|---|---|
| Sin código prohibido | `git grep -n -E "GlobalScope\|runBlocking\|Thread.sleep" -- "*.kt"` | Sin resultados ✅ |
| `CancellationException` relanzada | Revisión de `contar()`, `conReintentos()` y `cargar()` | Se relanza en los 3 ✅ |
| Timeout antes que la cancelación | Orden de los `catch` en `cargar()` | Correcto ✅ |
| Primos | 100 / 1000 / 10 000 | 25 / 168 / 1229 ✅ |
| Una por una / A la vez | Tarjeta *Dos cargas* | ≈ 5002 ms / ≈ 3002 ms ✅ |
| Reintentos | Simular error + A la vez | 2 de 3, 3 de 3 y luego Error ✅ |
| Tardó demasiado | Límite de 2 s + A la vez | Error a los ≈ 2 s ✅ |
| Comparación | 2 + 2 cargas | Promedios y "1.7 veces más rápido" ✅ |
| Rotación | Girar durante Iniciar | El progreso continúa ✅ |
| Logs HILO | Logcat `tag:HILO` | main → DefaultDispatcher-worker → main ✅ |
| Sin grises | Ctrl + F `Gray` en `ui/` | Sin resultados ✅ |
| Indicadores reales | Iniciar / Contar / A la vez | *Activos* y *Listos* cambian con cada estado ✅ |
| Archivos protegidos | `git status` después de la etapa 6 | Solo `ui/` y `res/drawable/` ✅ |

---

## 13. Historial de commits

**Rama `mejora-ia`** (sobre los 9 commits de `main`):

| # | Commit | Archivos |
|---|---|---|
| 1 | `feat(repo): mueve la tarea con progreso al Repository como Flow con flowOn` | TareasRepository, TareasRepositoryLocal, TareasViewModel |
| 2 | `feat(errores): reintenta las cargas hasta 3 veces con espera creciente` | Estados, TareasViewModel, PantallaTareas |
| 3 | `feat(carga): agrega tiempo maximo de 2 s with withTimeout` | TareasViewModel, PantallaTareas |
| 4 | `feat(comparacion): agrega tabla que compara tiempos de una por una y a la vez` | Estados, TareasViewModel, PantallaTareas |
| 5 | `style(ui): redisena la interfaz con estilo profesional y tema azul` | PantallaTareas, Color, Theme, Type |
| 6 | `style(ui): redisena la interfaz con indicadores, iconos y graficas de comparacion` | PantallaTareas, Color, Theme, Type, res/drawable/ic_*.xml |
| 7 | `docs: agrega PROMPTS.md y actualiza README con la Fase 2` | PROMPTS.md, README.md, capturas |

Cumple las reglas de la Fase 2: **6 mejoras** (el mínimo es 3), y **4 de ellas** cambian el Repository o el ViewModel (el mínimo es 1). Son **7 commits** (el mínimo es 3), cada uno con un mensaje que dice qué mejora se hizo.

---

## 14. Conclusiones

1. **Un prompt por etapas funcionó mejor que prompts sueltos.** Gemini tuvo todo el contexto y las reglas desde el principio, pero cada mejora quedó aislada en su commit y la revisé antes de seguir.
2. **Lo que más hay que vigilar en la IA es el manejo de la cancelación.** El caso del `TimeoutCancellationException` es el ejemplo claro: un `catch` en el orden equivocado no da error de compilación, pero rompe la funcionalidad en silencio. Por eso lo especifiqué en el prompt y lo revisé a mano.
3. **Escribir las reglas explícitas (R1–R6) evitó errores típicos.** Gemini no usó `GlobalScope`, `runBlocking` ni `Thread.sleep`, y respetó MVVM, porque se lo dije de forma directa.
4. **La IA propone, yo decido.** Acepté mejoras que no pedí cuando eran buenas (`ResumenTiempos`), descarté una idea mía cuando chocaba con la guía (el contador "toques") y documenté el único error visible (el mensaje en inglés) en lugar de ocultarlo.
5. **Una imagen de referencia necesita reglas.** La maqueta traía números y conclusiones de ejemplo; sin indicarlo en el prompt, la IA los habría copiado. Bloquear el ViewModel y exigir datos reales fue lo que permitió un rediseño grande sin riesgo.
6. **El resultado** es una app que sigue cumpliendo todas las pruebas de la Fase 1 y además reintenta, limita el tiempo, compara rendimientos y se ve profesional.
