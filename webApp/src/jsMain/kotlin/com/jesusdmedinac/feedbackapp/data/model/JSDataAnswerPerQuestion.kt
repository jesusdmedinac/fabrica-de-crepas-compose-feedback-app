package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataAnswerPerQuestion(
    val question: String = "",
    val order: Int = -1,
    val rating: Int = 0,
)
