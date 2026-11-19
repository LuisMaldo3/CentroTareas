# CentroTareas

App Android (Kotlin + Jetpack Compose + Material 3) para practicar **hilos y corrutinas**: una tarea con barra de progreso, un cálculo pesado (contar números primos) y dos cargas de datos simuladas, sin congelar nunca la pantalla.

> Laboratorio GLAB-S14 · Programación en Móviles · Arquitectura MVVM + Repository.

## Observación del Paso 1

**¿Qué pasa con la pantalla?**
Al pulsar **Tarea larga (MAL)** la app se queda congelada unos 4 segundos. El botón **Tócame** no responde: el contador no sube mientras dura la espera. Los toques hechos en ese tiempo no se ven en el momento. Cuando termina la espera, la pantalla vuelve de golpe y el texto pasa directamente a **Terminó**.

**¿Por qué no se ve "Trabajando..."?**
`tareaLargaMala()` se ejecuta en el **hilo principal (main)**, que es el mismo que dibuja la pantalla y atiende los toques. El código cambia el mensaje a "Trabajando..." y en seguida llama a `Thread.sleep(4000)`, que **bloquea** ese hilo. Para mostrar el nuevo texto, Compose necesita que el hilo principal quede libre y pueda dibujar el siguiente cuadro, pero está dormido. Cuando por fin se libera, el mensaje ya cambió a "Terminó", así que "Trabajando..." nunca llega a dibujarse.

**¿Qué mostraría Android si la espera fuera de 10 segundos?**
Mostraría un diálogo **ANR (Application Not Responding)**: *"CentroTareas no responde"*, con las opciones **Cerrar app** o **Esperar**. Android lo muestra cuando el hilo principal no atiende un evento de entrada (un toque) en unos **5 segundos**. Por eso el trabajo largo nunca debe ir en el hilo principal.