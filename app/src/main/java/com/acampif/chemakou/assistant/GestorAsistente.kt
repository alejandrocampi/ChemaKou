package com.acampif.chemakou.assistant

import androidx.navigation.NavController
import com.acampif.chemakou.R

class GestorAsistente(
    private val navController: NavController
) {

    fun manejarComando(command: String): String {

        return when {

            command.contains("texto") || command.contains("leer") || command.contains("foto") -> {
                navController.navigate(R.id.textReaderFragment)
                "Abriendo lector de texto"
            }

            command.contains("ubicación") || command.contains("ubicacion") || command.contains("donde") -> {
                navController.navigate(R.id.locationFragment)
                "Mostrando tu ubicación"
            }

            command.contains("ayuda") -> {
                navController.navigate(R.id.helpFragment)
                "Abriendo ayuda"
            }

            command.contains("historial") -> {
                navController.navigate(R.id.historyFragment)
                "Abriendo historial"
            }

            else -> {
                "He entendido: $command"
            }
        }
    }
}