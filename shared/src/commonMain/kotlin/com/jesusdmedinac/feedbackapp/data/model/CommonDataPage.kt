package com.jesusdmedinac.feedbackapp.data.model

data class CommonDataPage(
    val order: Int = -1,
    val text: String = "",
    val image: String = "",
    val rating: CommonDataRateStar = CommonDataRateStar.UNSELECTED,
    val type: CommonDataPageType = CommonDataPageType.UNKNOWN,
)
