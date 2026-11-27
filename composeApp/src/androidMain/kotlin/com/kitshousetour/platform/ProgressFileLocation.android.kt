package com.kitshousetour.platform

import android.content.Context
import java.io.File

actual object ProgressFileLocation : ProgressFileAccess {
    private lateinit var applicationContext: Context

    actual override val path: String
        get() = progressFile().absolutePath

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    actual override fun readText(): String? {
        val file = progressFile()
        return if (file.exists()) file.readText() else null
    }

    actual override fun writeText(contents: String) {
        progressFile().writeText(contents)
    }

    private fun progressFile(): File {
        check(::applicationContext.isInitialized) {
            "ProgressFileLocation must be initialized before accessing progress."
        }
        return File(applicationContext.filesDir, "progress.json")
    }
}
