package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataPage(
    val order: Int = -1,
    val text: String = "",
    val image: String = "",
    val rating: JSDataRateStar = JSDataRateStar.UNSELECTED,
    val type: String = "UNKNOWN",
)
