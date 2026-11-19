package com.maldonado.centrotareas.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maldonado.centrotareas.model.CargaUi
import com.maldonado.centrotareas.model.PrimosUi
import com.maldonado.centrotareas.ui.theme.AzulMarino
import com.maldonado.centrotareas.ui.theme.AzulPrincipal
import com.maldonado.centrotareas.ui.theme.AzulSecundario
import com.maldonado.centrotareas.ui.theme.AzulTexto
import com.maldonado.centrotareas.ui.theme.BotonDeshabilitadoFondo
import com.maldonado.centrotareas.ui.theme.BotonDeshabilitadoTexto
import com.maldonado.centrotareas.ui.theme.BordeTarjeta
import com.maldonado.centrotareas.ui.theme.ErrorFondo
import com.maldonado.centrotareas.ui.theme.ErrorTexto
import com.maldonado.centrotareas.ui.theme.ExitoFondo
import com.maldonado.centrotareas.ui.theme.ExitoTexto
import com.maldonado.centrotareas.ui.theme.FilaAlternada
import com.maldonado.centrotareas.viewmodel.TareasViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("CentroTareas", fontWeight = FontWeight.Bold)
                        Text(
                            "Hilos y corrutinas en Android",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulMarino,
                    titleContentColor = Color.White
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TarjetaProgreso(vm)
            TarjetaPrimos(vm)
            TarjetaCargas(vm)
            TarjetaComparacion(vm)
        }
    }
}

// ---------- Componentes reutilizables de UI Profesional ----------

@Composable
private fun TarjetaPaso(
    numero: Int,
    titulo: String,
    descripcion: String,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BordeTarjeta)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = AzulPrincipal
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = numero.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleLarge,
                        color = AzulTexto,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulSecundario
                    )
                }
            }
            contenido()
        }
    }
}

@Composable
private fun BotonPrimario(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulPrincipal,
            contentColor = Color.White,
            disabledContainerColor = BotonDeshabilitadoFondo,
            disabledContentColor = BotonDeshabilitadoTexto
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun BotonSecundario(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, if (enabled) AzulPrincipal else BordeTarjeta),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AzulPrincipal,
            disabledContentColor = BotonDeshabilitadoTexto
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}

@Composable
private fun MensajeChip(mensaje: String) {
    val (bgColor, textColor) = when {
        mensaje.contains("Trabajando") -> Color(0xFFD3E4F5) to AzulPrincipal
        mensaje.contains("Terminó") -> ExitoFondo to ExitoTexto
        mensaje.contains("Cancelado") -> ErrorFondo to ErrorTexto
        else -> Color(0xFFE8F1FC) to AzulSecundario
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = mensaje,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = textColor,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RecuadroFranja(
    isError: Boolean,
    contenido: @Composable ColumnScope.() -> Unit
) {
    val stripeColor = if (isError) ErrorTexto else ExitoTexto
    val bgColor = if (isError) ErrorFondo else ExitoFondo
    val contentColor = if (isError) ErrorTexto else ExitoTexto

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Surface(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight(),
                color = stripeColor
            ) {}
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                content = contenido
            )
        }
    }
}

@Composable
private fun InterruptorItem(
    texto: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(texto, style = MaterialTheme.typography.bodyLarge, color = AzulTexto)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AzulPrincipal,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = AzulSecundario.copy(alpha = 0.3f)
            )
        )
    }
}


// ---------- Tarjeta 1: tarea con progreso ----------
@Composable
private fun TarjetaProgreso(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    var toques by rememberSaveable { mutableIntStateOf(0) }

    TarjetaPaso(
        numero = 1,
        titulo = "Tarea con progreso",
        descripcion = "Ejecución reactiva con Flow y cancelación"
    ) {
        BotonPrimario(
            text = "Tócame: $toques",
            onClick = { toques++ },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonPrimario(
                text = "Iniciar",
                onClick = { vm.tareaLarga() },
                enabled = !corriendo,
                modifier = Modifier.weight(1f)
            )
            BotonSecundario(
                text = "Cancelar",
                onClick = { vm.cancelar() },
                enabled = corriendo,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
        ) {
            LinearProgressIndicator(
                progress = { progreso },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = AzulPrincipal,
                trackColor = BordeTarjeta
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${(progreso * 100).roundToInt()} %",
                style = MaterialTheme.typography.headlineMedium,
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold
            )
            MensajeChip(mensaje = mensaje)
        }
    }
}


// ---------- Tarjeta 2: contar números primos ----------
@Composable
private fun TarjetaPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("1000") }
    var ultimoN by rememberSaveable { mutableIntStateOf(0) }

    val numero = texto.toIntOrNull()
    val valido = numero != null && numero >= 2
    val calculando = primos is PrimosUi.Calculando

    TarjetaPaso(
        numero = 2,
        titulo = "Contar números primos",
        descripcion = "Cálculo pesado en hilo secundario (Dispatchers.Default)"
    ) {
        OutlinedTextField(
            value = texto,
            onValueChange = { nuevo -> texto = nuevo.filter { it.isDigit() }.take(9) },
            label = { Text("Hasta el número") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonPrimario(
                text = "Contar",
                onClick = {
                    if (numero != null && numero >= 2) {
                        ultimoN = numero
                        vm.contar(numero)
                    }
                },
                enabled = valido && !calculando,
                modifier = Modifier.weight(1f)
            )
            BotonSecundario(
                text = "Cancelar",
                onClick = { vm.cancelarPrimos() },
                enabled = calculando,
                modifier = Modifier.weight(1f)
            )
        }

        when (val estado = primos) {
            PrimosUi.Inicial -> Text(
                "Escribe un número (2 o más) y pulsa Contar",
                color = AzulSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
            PrimosUi.Calculando -> Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AzulPrincipal)
                Text("Calculando...", color = AzulSecundario, fontWeight = FontWeight.Bold)
            }
            is PrimosUi.Listo -> RecuadroFranja(isError = false) {
                Text("Primos hasta $ultimoN: ${estado.cantidad}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Tiempo: ${estado.milisegundos} ms", fontWeight = FontWeight.Bold)
            }
            PrimosUi.Cancelado -> RecuadroFranja(isError = true) {
                Text("Cancelado", fontWeight = FontWeight.Bold)
            }
        }
    }
}


// ---------- Tarjeta 3: dos cargas ----------
@Composable
private fun TarjetaCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val limiteActivo by vm.limiteActivo.collectAsState()
    val cargando = carga is CargaUi.Cargando || carga is CargaUi.Reintentando

    TarjetaPaso(
        numero = 3,
        titulo = "Dos cargas",
        descripcion = "Gestión concurrente, reintentos y timeouts"
    ) {
        InterruptorItem(
            texto = "Simular error",
            checked = simularError,
            onCheckedChange = { vm.cambiarSimularError(it) }
        )

        InterruptorItem(
            texto = "Límite de 2 s",
            checked = limiteActivo,
            onCheckedChange = { vm.cambiarLimite(it) }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BotonSecundario(
                text = "Una por una",
                onClick = { vm.cargarUnaPorUna() },
                enabled = !cargando,
                modifier = Modifier.weight(1f)
            )
            BotonPrimario(
                text = "A la vez",
                onClick = { vm.cargarALaVez() },
                enabled = !cargando,
                modifier = Modifier.weight(1f)
            )
        }

        when (val estado = carga) {
            CargaUi.Inicial -> Text(
                "Elige cómo cargar el perfil y los pedidos",
                color = AzulSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
            CargaUi.Cargando -> Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AzulPrincipal)
                Text("Cargando...", color = AzulSecundario, fontWeight = FontWeight.Bold)
            }
            is CargaUi.Reintentando -> Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = AzulPrincipal)
                Text("Reintentando (intento ${estado.intento} de ${estado.maximo})... espera ${estado.esperaMs} ms", color = AzulSecundario, fontWeight = FontWeight.Bold)
            }
            is CargaUi.Exito -> RecuadroFranja(isError = false) {
                Text("Modo: ${estado.modo}", fontWeight = FontWeight.Bold)
                Text("Perfil: ${estado.perfil}")
                Text("Pedidos: ${estado.pedidos}")
                Text("Tiempo: ${estado.milisegundos} ms", fontWeight = FontWeight.Bold)
            }
            is CargaUi.Error -> {
                RecuadroFranja(isError = true) {
                    Text(estado.mensaje, fontWeight = FontWeight.Bold)
                }
                BotonSecundario(
                    text = "Reintentar",
                    onClick = { vm.reintentar() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Text(
            "«Una por una» tarda ≈ 5000 ms · «A la vez» ≈ 3000 ms",
            color = AzulSecundario,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


// ---------- Tarjeta 4: comparación de tiempos ----------
@Composable
private fun TarjetaComparacion(vm: TareasViewModel) {
    val tiempos by vm.tiempos.collectAsState()
    val resumen by vm.resumen.collectAsState()

    TarjetaPaso(
        numero = 4,
        titulo = "Comparación de tiempos",
        descripcion = "Métricas históricas y análisis de rendimiento"
    ) {
        if (tiempos.isEmpty()) {
            Text(
                "Aún no hay tiempos. Prueba Una por una y A la vez.",
                color = AzulSecundario,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cabecera de la tabla
                Surface(
                    color = AzulMarino,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("N.º", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(0.5f))
                        Text("Modo", fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1.5f))
                        Text("Tiempo (ms)", fontWeight = FontWeight.Bold, color = Color.White, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                    }
                }

                tiempos.forEachIndexed { index, registro ->
                    val rowBg = if (index % 2 == 0) Color.White else FilaAlternada
                    Surface(color = rowBg) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${index + 1}", color = AzulTexto, modifier = Modifier.weight(0.5f))
                            Text(registro.modo, color = AzulTexto, modifier = Modifier.weight(1.5f))
                            Text("${registro.milisegundos}", color = AzulTexto, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                    }
                }

                // Resumen
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    color = Color(0xFFD3E4F5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (resumen.promedioUnaPorUna > 0.0) {
                            Text("Promedio Una por una: ${resumen.promedioUnaPorUna.roundToInt()} ms", color = AzulTexto, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (resumen.promedioALaVez > 0.0) {
                            Text("Promedio A la vez: ${resumen.promedioALaVez.roundToInt()} ms", color = AzulTexto, style = MaterialTheme.typography.bodyMedium)
                        }
                        if (resumen.vecesMasRapido > 0.0) {
                            Text(
                                "A la vez fue ${String.format(java.util.Locale.US, "%.1f", resumen.vecesMasRapido)} veces más rápido",
                                fontWeight = FontWeight.Bold,
                                color = AzulPrincipal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }

            BotonSecundario(
                text = "Limpiar",
                onClick = { vm.limpiarTiempos() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
