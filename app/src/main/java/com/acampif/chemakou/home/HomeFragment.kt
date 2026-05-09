package com.acampif.chemakou.home

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acampif.chemakou.assistant.GestorAsistente
import com.acampif.chemakou.data.GestorHistorial
import com.acampif.chemakou.R
import com.acampif.chemakou.databinding.FragmentHomeBinding
import com.acampif.chemakou.LectorPantalla
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home), LectorPantalla {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var gestorAsistente: GestorAsistente
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var gestorHistorial: GestorHistorial

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        gestorAsistente = GestorAsistente(findNavController())
        gestorHistorial = GestorHistorial(requireContext())

        textToSpeech = TextToSpeech(requireContext()) {
            val prefs = requireContext().getSharedPreferences("voz_settings", 0)

            val idioma = prefs.getString("idioma", "Español")

            val locale = if (idioma == "Inglés") {
                Locale.US
            } else {
                Locale("es", "ES")
            }

            textToSpeech.language = locale

            val velocidad = prefs.getFloat("speed", 1.0f)
            textToSpeech.setSpeechRate(velocidad)
        }

        binding.btnMicrophone.setOnClickListener {
            showFakeVoiceDialog()
        }

        binding.cardTextReader.setOnClickListener {
            findNavController().navigate(R.id.textReaderFragment)
        }

        binding.cardGeolocation.setOnClickListener {
            findNavController().navigate(R.id.locationFragment)
        }
    }

    private fun showFakeVoiceDialog() {
        val input = EditText(requireContext())
        input.hint = "Ej: leer texto, ubicación, ayuda..."

        AlertDialog.Builder(requireContext())
            .setTitle("Entrada por voz")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val command = input.text.toString()
                    .lowercase(Locale.getDefault())
                    .trim()

                if (command.isNotEmpty()) {
                    procesarComando(command)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun procesarComando(command: String) {
        mostrarUltimoComando(command)
        gestorHistorial.guardarComando(command)
        val respuesta = gestorAsistente.manejarComando(command)
        hablar(respuesta)
    }

    private fun mostrarUltimoComando(command: String) {
        binding.txtNoLastCommand.visibility = View.GONE
        binding.lastCommandContent.visibility = View.VISIBLE
        binding.lastCommandText.text = command
    }

    private fun hablar(texto: String) {
        textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun leerPantalla() {
        val ultimo = binding.lastCommandText.text.toString()

        val texto = if (ultimo.isEmpty()) {
            "Pantalla principal. Puedes usar el micrófono o los botones."
        } else {
            "Pantalla principal. Último comando: $ultimo"
        }

        hablar(texto)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.shutdown()
    }
}