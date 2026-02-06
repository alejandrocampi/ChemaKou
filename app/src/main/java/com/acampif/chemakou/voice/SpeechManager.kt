package com.acampif.chemakou.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

class SpeechManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: () -> Unit
) {

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                onError()
            }

            override fun onResults(results: Bundle?) {
                val matches =
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

                if (!matches.isNullOrEmpty()) {
                    onResult(matches[0].lowercase(Locale("es", "ES")))
                } else {
                    onError()
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer.startListening(intent)
    }

    fun destroy() {
        speechRecognizer.destroy()
    }
}