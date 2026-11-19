package com.maldonado.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maldonado.centrotareas.data.TareasRepository
import com.maldonado.centrotareas.model.CargaUi
import com.maldonado.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "HILO"

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Tarea con progreso (pasos 1 a 4)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    // Paso 3: progreso de 0f a 1f. La pantalla solo puede leerlo.
    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    // Paso 4: Job de la tarea (para poder cancelarla) y si está corriendo.
    private var trabajo: Job? = null
    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    fun tareaLarga() {
        if (_corriendo.value) return // evita lanzar dos tareas a la vez
        _corriendo.value = true

        trabajo = viewModelScope.launch {
            try {
                _progreso.value = 0f
                _mensaje.value = "Trabajando..."
                Log.d(TAG, "Antes: ${Thread.currentThread().name}")

                repo.tareaConProgreso().collect { progreso ->
                    _progreso.value = progreso
                }

                Log.d(TAG, "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                // Se ejecuta siempre: termine bien o la cancelen
                _corriendo.value = false
            }
        }
    }

    // Detiene la tarea; la barra se queda donde estaba
    fun cancelar() {
        if (!_corriendo.value) return
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos (paso 5)
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()
    private var trabajoPrimos: Job? = null

    // Paso 5: el cálculo lo hace el Repository en Dispatchers.Default;
    // el ViewModel solo lo lanza, mide el tiempo y publica el estado.
    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value is PrimosUi.Calculando) return

        trabajoPrimos = viewModelScope.launch {
            _primos.value = PrimosUi.Calculando
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta)
                val ms = System.currentTimeMillis() - inicio
                _primos.value = PrimosUi.Listo(cantidad, ms)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e // se vuelve a lanzar para no romper la cancelación
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    // Paso 7: interruptor "Simular error" (se pasa a repo.cargarPedidos)
    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    // Paso 6a: una por una -> primero el perfil (2 s) y DESPUÉS los pedidos (3 s) ≈ 5 s
    fun cargarUnaPorUna() = cargar("Una por una") {
        val perfil = repo.cargarPerfil()
        val pedidos = repo.cargarPedidos(_simularError.value)
        Pair(perfil, pedidos)
    }

    // Paso 6b: a la vez -> las dos llamadas arrancan juntas con async
    // y se esperan con await(): tarda lo que la más lenta ≈ 3 s
    fun cargarALaVez() = cargar("A la vez") {
        coroutineScope {
            val perfil = async { repo.cargarPerfil() }
            val pedidos = async { repo.cargarPedidos(_simularError.value) }
            Pair(perfil.await(), pedidos.await())
        }
    }

    // Paso 7: Reintentar vuelve a ejecutar la carga "A la vez"
    fun reintentar() = cargarALaVez()

    // Lógica común: estado Cargando, medir el tiempo y publicar Exito o Error
    private fun cargar(modo: String, bloque: suspend () -> Pair<String, List<String>>) {
        if (_carga.value is CargaUi.Cargando) return // no lanza una segunda carga

        viewModelScope.launch {
            _carga.value = CargaUi.Cargando
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = bloque()
                val ms = System.currentTimeMillis() - inicio
                _carga.value = CargaUi.Exito(modo, perfil, pedidos.size, ms)
            } catch (e: Exception) {
                // Primero se deja pasar la cancelación: no es un error de carga
                if (e is CancellationException) throw e
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }
}
