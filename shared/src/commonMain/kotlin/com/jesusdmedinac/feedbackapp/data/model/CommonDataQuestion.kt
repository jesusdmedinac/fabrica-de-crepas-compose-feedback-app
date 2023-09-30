package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataQuestion(
    val order: Int = -1,
    val question: String = "",
    val image: String = "",
    val rating: CommonDataRateStar = CommonDataRateStar.UNSELECTED,
)
