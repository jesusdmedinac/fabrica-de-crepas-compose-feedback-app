package com.jesusdmedinac.feedbackapp.domain.model

data class Answer(
    val answers: List<AnswerPerQuestion> = listOf(),
    val author: String = "",
    val createdAt: Long = 0L,
)

data class AnswerPerQuestion(
    val question: String = "",
    val order: Int = -1,
    val rating: Int = 0,
)
