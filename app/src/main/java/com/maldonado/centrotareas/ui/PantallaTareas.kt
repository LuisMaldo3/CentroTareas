package com.maldonado.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maldonado.centrotareas.model.CargaUi
import com.maldonado.centrotareas.model.PrimosUi
import com.maldonado.centrotareas.viewmodel.TareasViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaProgreso(vm)
            TarjetaPrimos(vm)
            TarjetaCargas(vm)
        }
    }
}

// ---------- Tarjeta 1: tarea con progreso (pasos 1 a 4) ----------
@Composable
private fun TarjetaProgreso(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    // Contador de toques: es la "prueba de vida" de la pantalla.
    // Si deja de sumar, el hilo principal está bloqueado.
    var toques by rememberSaveable { mutableIntStateOf(0) }

    Tarjeta("Tarea con progreso") {
        Button(onClick = { toques++ }) {
            Text("Tócame: $toques", fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Iniciar se deshabilita mientras corre (no hay doble inicio)
            Button(onClick = { vm.tareaLarga() }, enabled = !corriendo) {
                Text("Iniciar", fontWeight = FontWeight.Bold)
            }
            // Cancelar solo se habilita mientras corre
            OutlinedButton(onClick = { vm.cancelar() }, enabled = corriendo) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth()
        )
        Text("${(progreso * 100).roundToInt()} %", color = Color.Gray)
        Text(mensaje)
    }
}

// ---------- Tarjeta 2: contar números primos (paso 5) ----------
@Composable
private fun TarjetaPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("1000") }
    var ultimoN by rememberSaveable { mutableIntStateOf(0) }

    val numero = texto.toIntOrNull()
    val valido = numero != null && numero >= 2 // ignora vacíos o menores que 2
    val calculando = primos is PrimosUi.Calculando

    Tarjeta("Contar números primos") {
        OutlinedTextField(
            value = texto,
            // solo dígitos y máximo 9 cifras (cabe en un Int)
            onValueChange = { nuevo -> texto = nuevo.filter { it.isDigit() }.take(9) },
            label = { Text("Hasta el número") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = {
                    if (numero != null && numero >= 2) {
                        ultimoN = numero
                        vm.contar(numero)
                    }
                },
                enabled = valido && !calculando
            ) {
                Text("Contar", fontWeight = FontWeight.Bold)
            }
            OutlinedButton(onClick = { vm.cancelarPrimos() }, enabled = calculando) {
                Text("Cancelar", fontWeight = FontWeight.Bold)
            }
        }

        when (val estado = primos) {
            PrimosUi.Inicial -> Text("Escribe un número (2 o más) y pulsa Contar", color = Color.Gray)
            PrimosUi.Calculando -> Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text("Calculando...")
            }
            is PrimosUi.Listo -> RecuadroExito {
                Text("Primos hasta $ultimoN: ${estado.cantidad}", fontWeight = FontWeight.Bold)
                Text("Tiempo: ${estado.milisegundos} ms")
            }
            PrimosUi.Cancelado -> Text("Cancelado", color = MaterialTheme.colorScheme.error)
        }
    }
}

// ---------- Tarjeta 3: dos cargas (pasos 6 y 7) ----------
@Composable
private fun TarjetaCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val cargando = carga is CargaUi.Cargando

    Tarjeta("Dos cargas") {
        // Interruptor: con él activado, los pedidos fallan con "Sin conexión"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Switch(
                checked = simularError,
                onCheckedChange = { vm.cambiarSimularError(it) }
            )
            Text("Simular error")
        }

        // Los botones se deshabilitan mientras carga (no hay segunda carga)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = { vm.cargarUnaPorUna() }, enabled = !cargando) {
                Text("Una por una", fontWeight = FontWeight.Bold)
            }
            Button(onClick = { vm.cargarALaVez() }, enabled = !cargando) {
                Text("A la vez", fontWeight = FontWeight.Bold)
            }
        }

        when (val estado = carga) {
            CargaUi.Inicial -> Text("Elige cómo cargar el perfil y los pedidos", color = Color.Gray)
            CargaUi.Cargando -> Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Text("Cargando...")
            }
            is CargaUi.Exito -> RecuadroExito {
                Text("Modo: ${estado.modo}", fontWeight = FontWeight.Bold)
                Text("Perfil: ${estado.perfil}")
                Text("Pedidos: ${estado.pedidos}")
                Text("Tiempo: ${estado.milisegundos} ms")
            }
            is CargaUi.Error -> {
                RecuadroError(estado.mensaje)
                OutlinedButton(onClick = { vm.reintentar() }) {
                    Text("Reintentar", fontWeight = FontWeight.Bold)
                }
            }
        }

        Text(
            "«Una por una» tarda ≈ 5000 ms · «A la vez» ≈ 3000 ms",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

// ---------- Piezas reutilizables ----------
@Composable
private fun Tarjeta(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFE3DFEA))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = titulo,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            contenido()
        }
    }
}

// Recuadro verde para los resultados correctos
@Composable
private fun RecuadroExito(contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE3F2E1),
        contentColor = Color(0xFF1B5E20),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = contenido
        )
    }
}

// Recuadro de color de error para cuando una carga falla
@Composable
private fun RecuadroError(mensaje: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = mensaje,
            modifier = Modifier.padding(12.dp),
            textAlign = TextAlign.Center
        )
    }
}