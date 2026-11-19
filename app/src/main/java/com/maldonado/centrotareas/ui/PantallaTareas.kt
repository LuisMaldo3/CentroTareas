package com.maldonado.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    Tarjeta("Contar números primos") {
        // TODO Paso 5: campo numérico, botones Contar / Cancelar y resultado
        Text("Pendiente (Paso 5)", color = Color.Gray)
    }
}

// ---------- Tarjeta 3: dos cargas (pasos 6 y 7) ----------
@Composable
private fun TarjetaCargas(vm: TareasViewModel) {
    Tarjeta("Dos cargas") {
        // TODO Pasos 6 y 7: botones Una por una / A la vez, interruptor y resultado
        Text("Pendiente (Pasos 6 y 7)", color = Color.Gray)
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