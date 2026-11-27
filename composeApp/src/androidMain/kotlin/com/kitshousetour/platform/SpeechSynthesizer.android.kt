package com.kitshousetour.platform

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

actual object SpeechSynthesizer {
    private var engine: TextToSpeech? = null
    private var isReady = false

    fun initialize(context: Context) {
        engine?.shutdown()
        isReady = false
        engine = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val languageStatus = engine?.setLanguage(Locale.US)
                isReady = languageStatus != null &&
                    languageStatus != TextToSpeech.LANG_MISSING_DATA &&
                    languageStatus != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    actual fun speak(text: String) {
        if (isReady) {
            engine?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kit-speech")
        }
    }

    actual fun shutdown() {
        isReady = false
        engine?.shutdown()
        engine = null
    }
}
