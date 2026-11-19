# PROMPT MAESTRO — REDISEÑO PROFESIONAL DE CENTROTAREAS (ANDROID)

## ROL Y OBJETIVO

Actúa como un diseñador UI/UX sénior y desarrollador Android experto en Kotlin, Jetpack Compose y Material Design 3. Te adjunto el ZIP de CentroTareas y una imagen de referencia visual con dos pantallas del rediseño. Debes **editar el proyecto real**, no crear un sitio web, prototipo o captura. Tu objetivo es reproducir el estilo de la imagen en una app Android profesional, legible y funcional, sin romper la lógica del laboratorio de hilos y corrutinas.

## ARCHIVOS DE REFERENCIA

1. **ZIP:** `CentroTareas_PRO_Interfaz_Android.zip`. Debe abrirse como proyecto en Android Studio.
2. **Referencia visual:** `REFERENCIA_INTERFAZ.png` (también disponible como imagen adjunta). Es una maqueta de dos teléfonos: el izquierdo presenta cabecera, indicadores y ejercicios 01–02; el derecho contiene ejercicios 03–04, comparativa y conclusiones. Es **inspiración visual**, no fuente de datos verdaderos.
3. **Código original:** `app/src/main/java/com/maldonado/centrotareas/ui/PantallaTareas.kt` y `ui/theme/Color.kt`, `Theme.kt`, `Type.kt`.

## ARQUITECTURA A PRESERVAR

- No reemplaces `MainActivity`, `TareasViewModel`, `TareasViewModelFactory`, `model/Estados.kt`, `data/TareasRepository` ni `TareasRepositoryLocal`.
- No alteres `StateFlow`, `viewModelScope`, `Dispatchers.Default`, `async`, `await`, Flow, cancelación, reintentos ni `withTimeout`.
- No cambies los tiempos intencionales de las cargas (aproximadamente 2 s para perfil, 3 s para pedidos) ni la política del límite de 2 s.
- No añadas dependencias para implementar componentes que puedan crearse con Jetpack Compose, Material 3 y `vector drawable`.
- Mantén los contratos existentes y todos los callbacks reales.
- El ZIP debe contener el proyecto completo, no solamente fragmentos Kotlin.

## IDENTIDAD VISUAL

Estilo: panel de herramientas moderno, sobrio, pulido, con jerarquía visual y acentos suaves. **No** saturar la interfaz con gradientes, colores fluorescentes ni imágenes grandes.

Paleta recomendada:

| Uso | Color |
| --- | --- |
| Azul marino para cabecera | `#0D2349` |
| Azul principal de acciones | `#2457E7` |
| Azul secundario | `#1686F8` |
| Fondo general claro | `#F2F6FC` |
| Superficies/tarjetas | `#FFFFFF` |
| Texto principal | `#102549` |
| Texto secundario | `#637593` |
| Bordes suaves | `#E0E8F3` |
| Acento éxito/turquesa | `#0D9B9B` |
| Acento violeta | `#7652D4` |
| Acento ámbar | `#E29A20` |
| Éxito | `#087B62`, fondo `#E6F7F1` |
| Error | `#BF3B4E`, fondo `#FFEFF1` |

Ritmo de espacios: padding horizontal de pantalla 16 dp, 14 dp entre tarjetas, 16–18 dp de relleno en tarjetas; bordes redondeados de 12 dp para botones, 16 dp para pequeñas métricas, 22 dp para tarjetas grandes. Sombras muy suaves de 1–2 dp o bordes tenues, no ambos muy intensos.

## ESTRUCTURA DE LA PANTALLA

Una sola pantalla con `Scaffold` y `Column` con scroll vertical. Respetar barras de estado y navegación de Android (edge-to-edge / window insets). El contenido nunca debe quedar oculto bajo las barras del sistema ni cortado en teléfonos de 320–360 dp de ancho.

### 1. CABECERA (TELÉFONO IZQUIERDO)

- Fondo azul marino con gradiente horizontal muy discreto hacia azul oscuro.
- Icono de portapapeles blanco sobre un cuadrado azul de 54 dp con esquinas redondeadas.
- Título **CentroTareas** blanco, grande y en negrita.
- Subtítulo **Hilos y corrutinas en Android** en blanco azulado.
- Adorno geométrico suave, muy transparente, sin interferir con el texto.

### 2. TARJETAS DE INDICADORES

Justo bajo la cabecera coloca tres métricas reales en una fila:

- `4 Módulos` (siempre 4).
- `N Activos` (cantidad real de procesos en ejecución: progreso, primos, carga).
- `N Listos` (cantidad de módulos actualmente finalizados, calculada desde estados del ViewModel).

Cada una en tarjeta blanca de borde muy tenue, con un pequeño icono coloreado y texto legible. **No usar números de ejemplo fijos como “4 completados”** si los datos reales aún no muestran ese valor.

### 3. EJERCICIO 01 — TAREA CON PROGRESO

Tarjeta blanca de 22 dp con acento azul y badge **01**.

Título: `Tarea con progreso`.
Subtítulo: `Ejecuta una tarea sin bloquear la interfaz`.

Controles y presentación:

- Una pequeña franja azul suave y botón `Tócame: N`. El contador debe seguir incrementándose mientras el trabajo progresa, manteniendo `rememberSaveable`.
- Texto `Progreso de ejecución` y porcentaje derivado de `vm.progreso`.
- `LinearProgressIndicator` de 10 dp con esquinas suaves.
- Estado dinámico de `vm.mensaje`: Listo, Trabajando, Terminó o Cancelado. Mostrar verde para éxito, azul para progreso y rojo suave para cancelación.
- Dos botones contiguos: **Iniciar** (azul, icono play) y **Cancelar** (borde claro, icono stop); habilitación exactamente igual a la lógica original según `vm.corriendo`.
- Funciones intactas: `vm.tareaLarga()` y `vm.cancelar()`.

### 4. EJERCICIO 02 — CONTADOR DE PRIMOS

Tarjeta blanca con badge **02**, acento turquesa y icono matemático.

- Título: `Contador de primos`.
- Subtítulo: `Calcula números primos fuera del hilo principal`.
- `OutlinedTextField` con texto `Hasta el número`, teclado numérico, solo dígitos, máximo nueve cifras y valor válido desde 2.
- Acciones: **Contar** y **Cancelar**, respetando el estado `PrimosUi.Calculando`.
- Al calcular, indicador circular azul con texto `Calculando números primos...`.
- Al terminar, panel verde muy claro que muestre `estado.cantidad`, el valor `ultimoN` y `estado.milisegundos`.
- Al cancelar, mostrar el estado correspondiente en rojo suave.
- Los resultados deben provenir solo del ViewModel, nunca de valores falsos o decorativos.
- Funciones intactas: `vm.contar(numero)` y `vm.cancelarPrimos()`.

### 5. EJERCICIO 03 — CARGA DE DATOS (TELÉFONO DERECHO)

Tarjeta blanca con badge **03**, acento violeta e icono de base de datos.

- Título: `Carga de datos`.
- Subtítulo: `Compara procesos secuenciales y paralelos`.
- Botones `Una por una` (contorno) y `A la vez` (azul), con iconos reconocibles.
- Conservar ambos interruptores originales: **Simular error** y **Límite de 2 s**; son parte de las pruebas de laboratorio y no deben desaparecer aunque el mockup no los enseñe.
- Mientras procesa, indicador circular y texto de carga.
- Durante reintentos, mostrar `intento`, `máximo` y `esperaMs` reales.
- En éxito, mostrar tres miniceldas con iconos: **Perfil**, **Pedidos** y **Tiempo**. Usar `estado.perfil`, `estado.pedidos` y `estado.milisegundos`; evitar overflow con `maxLines` y elipsis.
- En error, un panel rojo claro con `estado.mensaje` y botón **Reintentar**.
- Mantener la nota que explica los tiempos estimados, claramente indicada como referencia, no como medición real.
- Funciones intactas: `vm.cargarUnaPorUna()`, `vm.cargarALaVez()`, `vm.cambiarSimularError(it)`, `vm.cambiarLimite(it)` y `vm.reintentar()`.

### 6. EJERCICIO 04 — COMPARACIÓN DE TIEMPOS

Tarjeta con badge **04** ámbar e icono de gráfica.

- Título `Comparación de tiempos`.
- Subtítulo `Analiza el rendimiento con mediciones reales`.
- Antes de ejecutar cargas, mostrar un estado vacío elegante con instrucciones, **no inventar barras ni tiempos**.
- Cuando existan registros, pintar barras horizontales sobre datos `vm.resumen`: azul para promedio **Una por una** y turquesa para promedio **A la vez**. Las barras tienen escala relativa al tiempo máximo registrado en ambos promedios.
- Si falta un método, mostrar solamente el otro y un mensaje invitando a probar ambos.
- Mostrar tabla dinámica de hasta diez registros según `vm.tiempos`: número de fila, modo, duración en ms. Filas alternadas blancas/azul muy claro, líneas sutiles.
- Crear un recuadro **Conclusión** con texto derivado de las mediciones, sin afirmar que el modo paralelo es siempre más rápido. Si la media paralela es menor, mostrar `vm.resumen.vecesMasRapido`; si es mayor, informar de la observación inversa. Si son iguales, comunicarlo.
- Botón **Limpiar historial**, conectado a `vm.limpiarTiempos()`.

## ICONOS Y RECURSOS

Usar iconos VectorDrawable XML en `app/src/main/res/drawable/` y `painterResource` con `Icon` de Compose. Iconos: portapapeles, rayo, reproducción, check, primo/calculadora, cronómetro, base de datos, capas, perfil, pedidos, gráfica, detener, pulsación, reiniciar, advertencia.

No añadir fotografías: es un laboratorio técnico y los iconos quedan más coherentes. Todos los iconos deben tener tamaño similar (18–24 dp), mismo grosor de trazo y tintes coherentes por sección.

## ACCESIBILIDAD Y RESPONSIVE

- Respetar tamaños táctiles recomendados de 48 dp.
- Los textos no deben quedar cortados ni superponerse.
- Asegurar contraste correcto y tamaño mínimo legible.
- Scroll vertical fluido; `Column` para contenido y `Row + weight` para botones y métricas.
- Soportar teclado, rotación y cambios de configuración sin perder controles o acceso a acciones.
- No ocultar estados importantes solo mediante colores; acompañarlos con textos.
- El dispositivo pequeño no puede tener elementos fuera del viewport horizontal.

## VALIDACIÓN FUNCIONAL

1. Comprobar que la UI recibe los mismos `StateFlow` del ViewModel original.
2. Ejecutar progreso e incrementar `Tócame` simultáneamente.
3. Cancelar progreso y comprobar estado y botones.
4. Contar primos con 1000; no escribir el resultado en el código: leer del ViewModel.
5. Cancelar un cálculo largo.
6. Cargar una por una y a la vez; comprobar mensajes, duración real y registro.
7. Activar simular error, observar reintentos y recuperar con el botón.
8. Activar límite 2s y revisar que el tiempo de espera original funcione.
9. Comparar medias reales y limpiar la tabla.
10. Verificar navegación por scroll, tamaños 320–360 dp y orientación vertical.
11. Sincronizar Gradle y compilar si se dispone de Android SDK. Si no es posible, decirlo expresamente.
12. Comparar archivos contra el proyecto recibido: solo editar archivos de interfaz `ui/PantallaTareas.kt`, `ui/theme/*` y recursos vectoriales requeridos.

## RESULTADO A ENTREGAR

- Un ZIP completo que abra directamente en Android Studio, sin `.git`, caches, builds ni `local.properties` de otra PC.
- Código Kotlin completo de los archivos visuales modificados.
- Lista de archivos modificados y recursos nuevos.
- Explicación breve de las decisiones de diseño.
- Estado real de compilación/pruebas, sin afirmar pruebas no ejecutadas.
- Capturas de emulador solo si la app se ejecutó realmente.

**No crees una maqueta solamente. Implementa la interfaz y conserva absolutamente la lógica y funciones del laboratorio.**
