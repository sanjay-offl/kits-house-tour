package com.kitshousetour.platform

import java.io.File

actual object ProgressFileLocation : ProgressFileAccess {
    private val progressFile: File by lazy {
        File(System.getProperty("user.home"), ".kits-house-tour/progress.json")
    }

    actual override val path: String
        get() = progressFile.absolutePath

    actual override fun readText(): String? =
        if (progressFile.exists()) progressFile.readText() else null

    actual override fun writeText(contents: String) {
        progressFile.parentFile?.mkdirs()
        progressFile.writeText(contents)
    }
}
