package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataQuestionResponse(
    val commonDataPages: List<CommonDataPage> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
