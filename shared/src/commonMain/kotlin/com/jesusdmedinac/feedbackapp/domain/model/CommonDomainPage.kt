package com.jesusdmedinac.feedbackapp.domain.model

data class CommonDomainPage(
    val order: Int = -1,
    val text: String = "",
    val image: String = "",
    val rating: CommonDomainRateStar = CommonDomainRateStar.UNSELECTED,
    val type: CommonDomainPageType = CommonDomainPageType.UNKNOWN,
    val isForward: Boolean = true,
)
