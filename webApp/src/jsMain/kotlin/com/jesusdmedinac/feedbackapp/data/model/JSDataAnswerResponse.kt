package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataAnswerResponse(
    val answer: JSDataAnswer = JSDataAnswer(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
