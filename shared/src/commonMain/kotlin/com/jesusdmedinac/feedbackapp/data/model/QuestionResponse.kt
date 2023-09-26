package com.jesusdmedinac.feedbackapp.data.model

data class QuestionResponse(
    val questions: List<Question> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
