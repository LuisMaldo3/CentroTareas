package com.maldonado.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maldonado.centrotareas.data.TareasRepositoryLocal
import com.maldonado.centrotareas.ui.PantallaTareas
import com.maldonado.centrotareas.ui.theme.CentroTareasTheme
import com.maldonado.centrotareas.viewmodel.TareasViewModel
import com.maldonado.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                val vm: TareasViewModel = viewModel(
                    factory = TareasViewModelFactory(TareasRepositoryLocal())
                )
                PantallaTareas(vm)
            }
        }
    }
}
