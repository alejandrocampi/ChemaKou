package com.acampif.chemakou.home

import android.app.AlertDialog
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.acampif.chemakou.assistant.AssistantManager
import com.acampif.chemakou.data.CommandHistoryManager
import com.acampif.chemakou.R
import com.acampif.chemakou.databinding.FragmentHomeBinding
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var assistantManager: AssistantManager
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var historyManager: CommandHistoryManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentHomeBinding.bind(view)

        assistantManager = AssistantManager(findNavController())
        historyManager = CommandHistoryManager(requireContext())

        textToSpeech = TextToSpeech(requireContext()) {
            textToSpeech.language = Locale.getDefault()
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
            .setTitle("Entrada por voz (simulada)")
            .setMessage("Escribe el comando")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val command = input.text.toString()
                    .lowercase(Locale.getDefault())
                    .trim()

                if (command.isNotEmpty()) {
                    processCommand(command)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun processCommand(command: String) {
        showLastCommand(command)

        historyManager.saveCommand(command)

        val response = assistantManager.handleCommand(command)
        speak(response)
    }

    private fun showLastCommand(command: String) {
        binding.txtNoLastCommand.visibility = View.GONE
        binding.lastCommandContent.visibility = View.VISIBLE
        binding.lastCommandText.text = command
    }

    private fun speak(text: String) {
        textToSpeech.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            null
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.shutdown()
    }
}