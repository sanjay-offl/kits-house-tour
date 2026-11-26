package com.kitshousetour.domain

/** A multiple-choice question and the index of its correct option. */
data class QuizQuestion(
    val id: String,
    val prompt: String,
    val options: List<String>,
    val correctIndex: Int,
) {
    init {
        require(options.size in 2..4) { "A quiz question must have 2 to 4 options." }
        require(correctIndex in options.indices) { "The correct index must identify an option." }
    }
}
