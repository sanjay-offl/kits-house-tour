package com.kitshousetour

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Kit's House Tour",
    ) {
        App()
    }
}
