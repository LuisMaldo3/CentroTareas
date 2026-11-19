package com.maldonado.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Azul del tema (barra superior, botones y títulos de las tarjetas)
private val EsquemaCentroTareas = lightColorScheme(
    primary = Color(0xFF1B6CA8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E4F5),
    onPrimaryContainer = Color(0xFF0B3A5E),
    secondary = Color(0xFF4F6B85),
    onSecondary = Color.White,
    background = Color(0xFFFBF9FE),
    onBackground = Color(0xFF1B1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1B1B1F),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF8C1D18)
)

@Composable
fun CentroTareasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaCentroTareas,
        typography = Typography,
        content = content
    )
}
