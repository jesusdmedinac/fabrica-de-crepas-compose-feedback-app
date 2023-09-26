package com.jesusdmedinac.feedbackapp.data.model

data class AnswerResponse(
    val answer: Answer = Answer(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
