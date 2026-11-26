package com.kitshousetour

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun App() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color(0xFFFFF8EE),
            surface = Color(0xFFFFF8EE),
            primary = Color(0xFF8A5A44),
        ),
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {}
    }
}
