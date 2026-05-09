package com.acampif.chemakou.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class AyudanteVoz(context: Context) {

    private var tts: TextToSpeech = TextToSpeech(context) {
        tts.language = Locale.getDefault()
    }

    fun speak(text: String) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    fun shutdown() {
        tts.shutdown()
    }
}