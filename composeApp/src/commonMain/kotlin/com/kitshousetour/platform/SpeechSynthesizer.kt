package com.kitshousetour.platform

/** Platform text-to-speech API used by Kit. */
expect object SpeechSynthesizer {
    /** Reads [text] aloud when platform speech is ready. */
    fun speak(text: String)

    /** Releases speech resources when the owning platform component is destroyed. */
    fun shutdown()
}
