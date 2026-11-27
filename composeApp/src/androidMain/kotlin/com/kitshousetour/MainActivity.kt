package com.kitshousetour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kitshousetour.platform.ProgressFileLocation
import com.kitshousetour.platform.SpeechSynthesizer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProgressFileLocation.initialize(applicationContext)
        SpeechSynthesizer.initialize(applicationContext)
        setContent {
            App()
        }
    }

    override fun onDestroy() {
        SpeechSynthesizer.shutdown()
        super.onDestroy()
    }
}
