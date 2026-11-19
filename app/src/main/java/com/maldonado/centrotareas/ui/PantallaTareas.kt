package com.maldonado.centrotareas.ui

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.maldonado.centrotareas.R
import com.maldonado.centrotareas.model.CargaUi
import com.maldonado.centrotareas.model.PrimosUi
import com.maldonado.centrotareas.ui.theme.Ambar
import com.maldonado.centrotareas.ui.theme.AzulFondo
import com.maldonado.centrotareas.ui.theme.AzulMarino
import com.maldonado.centrotareas.ui.theme.AzulPrincipal
import com.maldonado.centrotareas.ui.theme.AzulSecundario
import com.maldonado.centrotareas.ui.theme.AzulSuave
import com.maldonado.centrotareas.ui.theme.AzulTexto
import com.maldonado.centrotareas.ui.theme.BordeTarjeta
import com.maldonado.centrotareas.ui.theme.BotonDeshabilitadoFondo
import com.maldonado.centrotareas.ui.theme.BotonDeshabilitadoTexto
import com.maldonado.centrotareas.ui.theme.ErrorFondo
import com.maldonado.centrotareas.ui.theme.ErrorTexto
import com.maldonado.centrotareas.ui.theme.ExitoFondo
import com.maldonado.centrotareas.ui.theme.ExitoTexto
import com.maldonado.centrotareas.ui.theme.FilaAlternada
import com.maldonado.centrotareas.ui.theme.Turquesa
import com.maldonado.centrotareas.ui.theme.Violeta
import com.maldonado.centrotareas.viewmodel.TareasViewModel
import androidx.core.view.WindowCompat
import java.util.Locale
import kotlin.math.max
import kotlin.math.roundToInt

// La pantalla consume únicamente los flujos del ViewModel original.
// No incorpora resultados simulados ni duplica lógica de negocio.
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    // La cabecera es oscura: la barra de estado necesita iconos blancos.
    val actividad = LocalContext.current as? Activity
    val vista = LocalView.current
    SideEffect {
        actividad?.window?.let { ventana ->
            WindowCompat.getInsetsController(ventana, vista).isAppearanceLightStatusBars = false
        }
    }
    val corriendo by vm.corriendo.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()
    val tiempos by vm.tiempos.collectAsState()

    val activos = listOf(
        corriendo,
        primos is PrimosUi.Calculando,
        carga is CargaUi.Cargando || carga is CargaUi.Reintentando
    ).count { it }
    val listos = listOf(
        mensaje == "Terminó",
        primos is PrimosUi.Listo,
        carga is CargaUi.Exito,
        tiempos.isNotEmpty()
    ).count { it }

    Scaffold(containerColor = AzulFondo, topBar = { CabeceraCentroTareas() }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                StatCompacta(numero = "4", etiqueta = "Módulos", icono = R.drawable.ct_bolt, color = AzulPrincipal, modifier = Modifier.weight(1f))
                StatCompacta(numero = activos.toString(), etiqueta = "Activos", icono = R.drawable.ct_play, color = Violeta, modifier = Modifier.weight(1f))
                StatCompacta(numero = listos.toString(), etiqueta = "Listos", icono = R.drawable.ct_check, color = ExitoTexto, modifier = Modifier.weight(1f))
            }
            TarjetaProgreso(vm)
            TarjetaPrimos(vm)
            TarjetaCargas(vm)
            TarjetaComparacion(vm)
            Text(
                "CentroTareas · Laboratorio de hilos y corrutinas",
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                color = AzulSecundario,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CabeceraCentroTareas() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Brush.horizontalGradient(listOf(AzulMarino, Color(0xFF174284))))
            .statusBarsPadding()
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawCircle(
                color = Color(0xFF388CF9).copy(alpha = 0.18f),
                radius = size.width * 0.42f,
                center = Offset(size.width * 0.93f, size.height * 1.5f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.055f),
                radius = size.width * 0.30f,
                center = Offset(size.width * 0.94f, size.height * 1.35f),
                style = Stroke(width = 13.dp.toPx())
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 21.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                color = Color(0xFF2774EF),
                shape = RoundedCornerShape(17.dp),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(R.drawable.ct_clipboard),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text("CentroTareas", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                Text("Hilos y corrutinas en Android", color = Color(0xFFD7E7FF), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StatCompacta(numero: String, etiqueta: String, icono: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BordeTarjeta)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Surface(color = color.copy(alpha = 0.11f), shape = RoundedCornerShape(10.dp)) {
                Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
                    Icon(painter = painterResource(icono), contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                }
            }
            Column {
                Text(numero, color = AzulTexto, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
                Text(etiqueta, color = AzulSecundario, style = MaterialTheme.typography.labelSmall, maxLines = 1)
            }
        }
    }
}

@Composable
private fun TarjetaSeccion(
    numero: String,
    titulo: String,
    descripcion: String,
    icono: Int,
    acento: Color,
    contenido: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BordeTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Surface(color = acento.copy(alpha = 0.13f), shape = RoundedCornerShape(12.dp)) {
                    Box(modifier = Modifier.size(50.dp), contentAlignment = Alignment.Center) {
                        Text(numero, fontWeight = FontWeight.ExtraBold, color = acento, fontSize = 21.sp)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(titulo, color = AzulTexto, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(descripcion, color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
                }
                Icon(painter = painterResource(icono), contentDescription = null, tint = acento, modifier = Modifier.size(22.dp))
            }
            contenido()
        }
    }
}

@Composable
private fun BotonAccion(texto: String, icono: Int, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AzulPrincipal,
            contentColor = Color.White,
            disabledContainerColor = BotonDeshabilitadoFondo,
            disabledContentColor = BotonDeshabilitadoTexto
        ),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Icon(painter = painterResource(icono), contentDescription = null, modifier = Modifier.size(19.dp))
        Spacer(Modifier.width(7.dp))
        Text(texto, maxLines = 1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun BotonBorde(texto: String, icono: Int, onClick: () -> Unit, modifier: Modifier = Modifier, habilitado: Boolean = true) {
    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BordeTarjeta),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = AzulTexto,
            disabledContentColor = BotonDeshabilitadoTexto
        ),
        contentPadding = PaddingValues(horizontal = 10.dp)
    ) {
        Icon(painter = painterResource(icono), contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(7.dp))
        Text(texto, maxLines = 1, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun EtiquetaEstado(mensaje: String, cargando: Boolean = false, error: Boolean = false) {
    val tono = if (error) ErrorTexto else if (cargando) AzulPrincipal else ExitoTexto
    Surface(shape = RoundedCornerShape(9.dp), color = tono.copy(alpha = 0.10f)) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                painter = painterResource(if (error) R.drawable.ct_warning else if (cargando) R.drawable.ct_refresh else R.drawable.ct_check),
                contentDescription = null,
                tint = tono,
                modifier = Modifier.size(15.dp)
            )
            Text(mensaje, color = tono, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun MensajeEstado(error: Boolean, contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color = if (error) ErrorFondo else ExitoFondo,
        contentColor = if (error) ErrorTexto else ExitoTexto
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(6.dp), content = contenido)
    }
}

@Composable
private fun TarjetaProgreso(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    var toques by rememberSaveable { mutableIntStateOf(0) }
    val porcentaje = (progreso.coerceIn(0f, 1f) * 100).roundToInt()

    TarjetaSeccion("01", "Tarea con progreso", "Ejecuta una tarea sin bloquear la interfaz", R.drawable.ct_timer, AzulPrincipal) {
        Surface(color = AzulSuave, shape = RoundedCornerShape(11.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(painter = painterResource(R.drawable.ct_tap), contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(19.dp))
                    Text("Comprueba que responde", color = AzulTexto, style = MaterialTheme.typography.bodyMedium)
                }
                OutlinedButton(onClick = { toques++ }, shape = RoundedCornerShape(10.dp), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp), modifier = Modifier.height(36.dp)) {
                    Text("Tócame: $toques", maxLines = 1, fontSize = 12.sp)
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Progreso de ejecución", color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
                Text("$porcentaje %", color = AzulPrincipal, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { progreso.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(10.dp),
                color = AzulPrincipal,
                trackColor = AzulSuave
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            when {
                mensaje.contains("Cancelado") -> EtiquetaEstado(mensaje, error = true)
                mensaje.contains("Trabajando") -> EtiquetaEstado(mensaje, cargando = true)
                mensaje == "Terminó" -> EtiquetaEstado(mensaje)
                else -> Text(mensaje, color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonAccion("Iniciar", R.drawable.ct_play, onClick = { vm.tareaLarga() }, modifier = Modifier.weight(1f), habilitado = !corriendo)
            BotonBorde("Cancelar", R.drawable.ct_stop, onClick = { vm.cancelar() }, modifier = Modifier.weight(1f), habilitado = corriendo)
        }
    }
}

@Composable
private fun TarjetaPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("1000") }
    var ultimoN by rememberSaveable { mutableIntStateOf(0) }
    val numero = texto.toIntOrNull()
    val valido = numero != null && numero >= 2
    val calculando = primos is PrimosUi.Calculando

    TarjetaSeccion("02", "Contador de primos", "Calcula números primos fuera del hilo principal", R.drawable.ct_prime, Turquesa) {
        OutlinedTextField(
            value = texto,
            onValueChange = { nuevo -> texto = nuevo.filter { it.isDigit() }.take(9) },
            label = { Text("Hasta el número") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            supportingText = { if (!valido) Text("Ingresa un número mayor o igual a 2") }
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonAccion("Contar", R.drawable.ct_prime, onClick = {
                if (numero != null && numero >= 2) {
                    ultimoN = numero
                    vm.contar(numero)
                }
            }, modifier = Modifier.weight(1f), habilitado = valido && !calculando)
            BotonBorde("Cancelar", R.drawable.ct_stop, onClick = { vm.cancelarPrimos() }, modifier = Modifier.weight(1f), habilitado = calculando)
        }
        when (val estado = primos) {
            PrimosUi.Inicial -> Text("Indica un número y toca Contar para empezar.", color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
            PrimosUi.Calculando -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(19.dp), color = AzulPrincipal, strokeWidth = 2.dp)
                Text("Calculando números primos...", color = AzulSecundario, style = MaterialTheme.typography.bodyMedium)
            }
            is PrimosUi.Listo -> MensajeEstado(error = false) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Icon(painter = painterResource(R.drawable.ct_check), contentDescription = null, modifier = Modifier.size(22.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${estado.cantidad} números primos", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Text("Hasta $ultimoN", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Icon(painter = painterResource(R.drawable.ct_timer), contentDescription = null, modifier = Modifier.size(18.dp))
                        Text("${estado.milisegundos} ms", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            PrimosUi.Cancelado -> MensajeEstado(error = true) { Text("Cálculo cancelado", fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun AjusteInterruptor(texto: String, explicacion: String, marcado: Boolean, cambiar: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(texto, color = AzulTexto, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
            Text(explicacion, color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
        }
        Switch(
            checked = marcado,
            onCheckedChange = cambiar,
            colors = SwitchDefaults.colors(checkedTrackColor = AzulPrincipal, checkedThumbColor = Color.White)
        )
    }
}

@Composable
private fun MiniResultado(icono: Int, titulo: String, valor: String, color: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, color = color.copy(alpha = 0.07f), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(painter = painterResource(icono), contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Text(titulo, color = AzulTexto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(valor, color = color, style = MaterialTheme.typography.labelMedium, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun TarjetaCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val limiteActivo by vm.limiteActivo.collectAsState()
    val cargando = carga is CargaUi.Cargando || carga is CargaUi.Reintentando

    TarjetaSeccion("03", "Carga de datos", "Compara procesos secuenciales y paralelos", R.drawable.ct_database, Violeta) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonBorde("Una por una", R.drawable.ct_database, onClick = { vm.cargarUnaPorUna() }, modifier = Modifier.weight(1f), habilitado = !cargando)
            BotonAccion("A la vez", R.drawable.ct_layers, onClick = { vm.cargarALaVez() }, modifier = Modifier.weight(1f), habilitado = !cargando)
        }
        Surface(color = FilaAlternada, shape = RoundedCornerShape(12.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp)) {
                AjusteInterruptor("Simular error", "Prueba los reintentos", simularError, { vm.cambiarSimularError(it) })
                HorizontalDivider(color = BordeTarjeta)
                AjusteInterruptor("Límite de 2 s", "Prueba el tiempo de espera", limiteActivo, { vm.cambiarLimite(it) })
            }
        }
        when (val estado = carga) {
            CargaUi.Inicial -> Text("Elige un modo para cargar el perfil y los pedidos.", color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
            CargaUi.Cargando -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(19.dp), color = AzulPrincipal, strokeWidth = 2.dp)
                Text("Cargando perfil y pedidos...", color = AzulSecundario, style = MaterialTheme.typography.bodyMedium)
            }
            is CargaUi.Reintentando -> Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(19.dp), color = Ambar, strokeWidth = 2.dp)
                Text("Reintento ${estado.intento}/${estado.maximo} · espera ${estado.esperaMs} ms", color = AzulTexto, style = MaterialTheme.typography.bodySmall)
            }
            is CargaUi.Exito -> {
                EtiquetaEstado("${estado.modo} · completado")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MiniResultado(R.drawable.ct_person, "Perfil", estado.perfil, Turquesa, modifier = Modifier.weight(1f))
                    MiniResultado(R.drawable.ct_orders, "Pedidos", estado.pedidos.toString(), AzulPrincipal, modifier = Modifier.weight(1f))
                    MiniResultado(R.drawable.ct_timer, "Tiempo", "${estado.milisegundos} ms", Violeta, modifier = Modifier.weight(1f))
                }
            }
            is CargaUi.Error -> {
                MensajeEstado(error = true) { Text(estado.mensaje, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium) }
                BotonBorde("Reintentar", R.drawable.ct_refresh, onClick = { vm.reintentar() }, modifier = Modifier.fillMaxWidth())
            }
        }
        Text("Referencia: una por una ≈ 5 s · a la vez ≈ 3 s (sin errores ni límite).", color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun BarraTiempo(nombre: String, tiempo: Double, maximo: Double, color: Color) {
    val fraccion = if (maximo > 0.0) (tiempo / maximo).toFloat().coerceIn(0f, 1f) else 0f
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(nombre, color = AzulTexto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Text("${tiempo.roundToInt()} ms", color = AzulTexto, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { fraccion },
            modifier = Modifier.fillMaxWidth().height(12.dp),
            color = color,
            trackColor = FilaAlternada
        )
    }
}

@Composable
private fun TarjetaComparacion(vm: TareasViewModel) {
    val tiempos by vm.tiempos.collectAsState()
    val resumen by vm.resumen.collectAsState()
    val tieneSecuencial = resumen.promedioUnaPorUna > 0.0
    val tieneParalela = resumen.promedioALaVez > 0.0

    TarjetaSeccion("04", "Comparación de tiempos", "Analiza el rendimiento con mediciones reales", R.drawable.ct_chart, Ambar) {
        if (tiempos.isEmpty()) {
            Surface(shape = RoundedCornerShape(13.dp), color = AzulSuave) {
                Row(modifier = Modifier.fillMaxWidth().padding(13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(painter = painterResource(R.drawable.ct_chart), contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(22.dp))
                    Text("Ejecuta «Una por una» y «A la vez» para ver tus tiempos y compararlos aquí.", color = AzulTexto, style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text("Promedio por método", color = AzulTexto, style = MaterialTheme.typography.labelLarge)
                val mayor = max(resumen.promedioUnaPorUna, resumen.promedioALaVez)
                if (tieneSecuencial) BarraTiempo("Una por una", resumen.promedioUnaPorUna, mayor, AzulPrincipal)
                if (tieneParalela) BarraTiempo("A la vez", resumen.promedioALaVez, mayor, Turquesa)
            }
            Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                Surface(color = FilaAlternada, shape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 10.dp)) {
                        Text("#", modifier = Modifier.weight(0.35f), color = AzulTexto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Método", modifier = Modifier.weight(1.4f), color = AzulTexto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text("Tiempo", modifier = Modifier.weight(1f), color = AzulTexto, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }
                }
                tiempos.forEachIndexed { index, registro ->
                    Surface(color = if (index % 2 == 0) Color.White else FilaAlternada) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 11.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${index + 1}", modifier = Modifier.weight(0.35f), color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
                            Text(registro.modo, modifier = Modifier.weight(1.4f), color = AzulTexto, style = MaterialTheme.typography.bodySmall)
                            Text("${registro.milisegundos} ms", modifier = Modifier.weight(1f), color = AzulTexto, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.End, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    HorizontalDivider(color = BordeTarjeta)
                }
            }
            if (tieneSecuencial && tieneParalela) {
                val conclusion = when {
                    resumen.promedioALaVez < resumen.promedioUnaPorUna ->
                        "En estas pruebas, «A la vez» fue ${String.format(Locale.US, "%.1f", resumen.vecesMasRapido)} veces más rápido que «Una por una»."
                    resumen.promedioALaVez > resumen.promedioUnaPorUna ->
                        "En estas pruebas, «Una por una» registró un tiempo promedio menor."
                    else -> "Ambos métodos registraron el mismo tiempo promedio."
                }
                Surface(shape = RoundedCornerShape(13.dp), color = AzulSuave) {
                    Row(modifier = Modifier.fillMaxWidth().padding(13.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(painter = painterResource(R.drawable.ct_chart), contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(22.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Text("Conclusión", color = AzulTexto, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text(conclusion, color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            } else {
                Text("Ejecuta también el otro método para comparar sus promedios.", color = AzulSecundario, style = MaterialTheme.typography.bodySmall)
            }
            BotonBorde("Limpiar historial", R.drawable.ct_refresh, onClick = { vm.limpiarTiempos() }, modifier = Modifier.fillMaxWidth())
        }
    }
}
