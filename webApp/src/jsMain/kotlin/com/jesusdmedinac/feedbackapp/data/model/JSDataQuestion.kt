package com.jesusdmedinac.feedbackapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class JSDataQuestion(
    val order: Int = -1,
    val question: String = "",
    val image: String = "",
    val rating: JSDataRateStar = JSDataRateStar.UNSELECTED,
)
