package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataQuestionResponse(
    val commonDataQuestions: List<CommonDataQuestion> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
