package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataAnswerPayload(
    val answers: List<CommonDataAnswerPerQuestion> = listOf(),
    val author: String = "",
    val created_at: Double = 0.0,
)
