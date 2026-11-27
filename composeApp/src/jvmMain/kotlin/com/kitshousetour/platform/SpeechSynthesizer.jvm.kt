package com.kitshousetour.platform

actual object SpeechSynthesizer {
    actual fun speak(text: String) = Unit

    actual fun shutdown() = Unit
}
