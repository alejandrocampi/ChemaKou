package com.acampif.chemakou.assistant

import androidx.navigation.NavController
import com.acampif.chemakou.R

class AssistantManager(
    private val navController: NavController
) {

    fun handleCommand(command: String): String {

        return when {
            command.contains("texto") ||
                    command.contains("leer") ||
                    command.contains("foto") ||
                    command.contains("camara") ||
                    command.contains("cámara") -> {
                navController.navigate(R.id.textReaderFragment)
                "Abriendo lector de texto"
            }

            command.contains("ubicacion") ||
                    command.contains("ubicación") ||
                    command.contains("donde") ||
                    command.contains("mapa") ||
                    command.contains("localizacion") -> {
                navController.navigate(R.id.locationFragment)
                "Mostrando tu ubicación"
            }

            command.contains("ayuda") ||
                    command.contains("como funciona") ||
                    command.contains("informacion") -> {
                navController.navigate(R.id.helpFragment)
                "Abriendo la ayuda"
            }

            command.contains("historial") ||
                    command.contains("comandos") -> {
                navController.navigate(R.id.historyFragment)
                "Mostrando el historial de comandos"
            }

            else -> {
                "He entendido: $command"
            }
        }
    }
}