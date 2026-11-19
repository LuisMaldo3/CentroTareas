package com.maldonado.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaCentroTareas = lightColorScheme(
    primary = AzulPrincipal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3E4F5),
    onPrimaryContainer = AzulMarino,
    secondary = AzulSecundario,
    onSecondary = Color.White,
    background = AzulFondo,
    onBackground = AzulTexto,
    surface = Color.White,
    onSurface = AzulTexto,
    surfaceVariant = Color(0xFFE8F1FC),
    onSurfaceVariant = AzulSecundario,
    error = ErrorTexto,
    errorContainer = ErrorFondo,
    onErrorContainer = ErrorTexto
)

@Composable
fun CentroTareasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaCentroTareas,
        typography = Typography,
        content = content
    )
}
