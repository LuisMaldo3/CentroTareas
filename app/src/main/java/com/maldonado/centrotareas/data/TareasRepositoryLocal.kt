package com.maldonado.centrotareas.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import java.io.IOException

class TareasRepositoryLocal : TareasRepository {

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    // Paso 5: cálculo pesado de CPU -> Dispatchers.Default (hilos para cálculo).
    // Así la función suspend no bloquea el hilo desde el que se llama.
    override suspend fun contarPrimos(hasta: Int): Int = withContext(Dispatchers.Default) {
        var cantidad = 0
        for (n in 2..hasta) {
            ensureActive() // si cancelaron la corrutina, se detiene aquí
            if (esPrimo(n)) cantidad++
        }
        cantidad
    }

    // Prueba dividir n entre 2 y la raíz de n.
    // d.toLong() * d evita desbordes y no necesita calcular la raíz.
    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}