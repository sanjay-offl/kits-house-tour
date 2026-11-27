package com.kitshousetour.platform

/** Platform file access for the app's single progress JSON file. */
interface ProgressFileAccess {
    /** Absolute path of the progress file. */
    val path: String

    /** Returns the file contents, or null if no saved file exists. */
    fun readText(): String?

    /** Replaces the saved file contents. */
    fun writeText(contents: String)
}

/** Provides access to the platform-specific progress file location. */
expect object ProgressFileLocation : ProgressFileAccess {
    override val path: String

    override fun readText(): String?

    override fun writeText(contents: String)
}
