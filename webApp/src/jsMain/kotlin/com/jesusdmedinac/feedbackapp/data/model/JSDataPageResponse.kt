package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataPageResponse(
    val pages: List<JSDataPage> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
