package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataAnswer(
    val answers: List<JSDataAnswerPerQuestion> = listOf(),
    val author: String = "",
    val created_at: Double = 0.0,
)
