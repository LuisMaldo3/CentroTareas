package com.maldonado.centrotareas.model

sealed interface PrimosUi {
    data object Inicial : PrimosUi
    data object Calculando : PrimosUi
    data class Listo(val cantidad: Int, val milisegundos: Long) : PrimosUi
    data object Cancelado : PrimosUi
}

sealed interface CargaUi {
    data object Inicial : CargaUi
    data object Cargando : CargaUi
    data class Reintentando(val intento: Int, val maximo: Int, val esperaMs: Long) : CargaUi
    data class Exito(
        val modo: String,
        val perfil: String,
        val pedidos: Int,
        val milisegundos: Long
    ) : CargaUi
    data class Error(val mensaje: String) : CargaUi
}

data class RegistroTiempo(val modo: String, val milisegundos: Long)

data class ResumenTiempos(
    val promedioUnaPorUna: Double = 0.0,
    val promedioALaVez: Double = 0.0,
    val vecesMasRapido: Double = 0.0
)
