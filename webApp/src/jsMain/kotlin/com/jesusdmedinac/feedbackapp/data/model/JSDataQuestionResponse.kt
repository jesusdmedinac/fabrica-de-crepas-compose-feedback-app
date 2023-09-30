package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataQuestionResponse(
    val questions: List<JSDataQuestion> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
