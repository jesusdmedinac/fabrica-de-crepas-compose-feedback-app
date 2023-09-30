package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataAnswerResponse(
    val answer: CommonDataAnswer = CommonDataAnswer(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
