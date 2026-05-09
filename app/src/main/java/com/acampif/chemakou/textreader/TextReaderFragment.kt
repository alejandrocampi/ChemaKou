package com.acampif.chemakou

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.acampif.chemakou.databinding.FragmentTextReaderBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale

class TextReaderFragment : Fragment(R.layout.fragment_text_reader), LectorPantalla {

    private lateinit var binding: FragmentTextReaderBinding
    private lateinit var textToSpeech: TextToSpeech

    private val REQUEST_IMAGE = 100
    private val REQUEST_CAMERA = 200

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentTextReaderBinding.bind(view)

        val prefs = requireContext().getSharedPreferences("voz_settings", 0)

        textToSpeech = TextToSpeech(requireContext()) {
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

        binding.btnOpenCamera.setOnClickListener {
            abrirCamara()
        }
    }

    private fun abrirCamara() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
            return
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_IMAGE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CAMERA &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK) {

            val bitmap = data?.extras?.get("data") as? Bitmap ?: return

            reconocerTexto(bitmap)
        }
    }

    private fun reconocerTexto(bitmap: Bitmap) {

        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->

                val texto = visionText.text

                if (texto.isNotEmpty()) {
                    mostrarTexto(texto)
                    hablar("Texto detectado: $texto")
                } else {
                    hablar("No se ha detectado texto")
                }
            }
            .addOnFailureListener {
                hablar("Error al detectar texto")
            }
    }

    private fun mostrarTexto(texto: String) {
        binding.txtNoTextYet.visibility = View.GONE
        binding.scrollText.visibility = View.VISIBLE
        binding.txtDetectedText.text = texto
    }

    override fun leerPantalla() {
        val texto = binding.txtDetectedText.text.toString()

        val mensaje = if (texto.isEmpty()) {
            "No hay texto detectado"
        } else {
            "El texto detectado es: $texto"
        }

        hablar(mensaje)
    }

    private fun hablar(texto: String) {
        textToSpeech.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.shutdown()
    }
}