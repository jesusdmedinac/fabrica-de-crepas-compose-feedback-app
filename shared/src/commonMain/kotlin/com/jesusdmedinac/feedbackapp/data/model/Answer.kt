package com.jesusdmedinac.feedbackapp.data.model

data class Answer(
    val answers: List<AnswerPerQuestion> = listOf(),
    val author: String = "",
    val created_at: Double = 0.0,
)

data class AnswerPerQuestion(
    val question: String = "",
    val order: Int = -1,
    val rating: Int = 0,
)
