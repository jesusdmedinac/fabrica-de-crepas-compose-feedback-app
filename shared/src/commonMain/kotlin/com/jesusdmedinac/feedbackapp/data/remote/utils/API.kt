package com.jesusdmedinac.feedbackapp.data.remote.utils

import com.jesusdmedinac.feedbackapp.utils.isDevMode

object API {
    private const val PROD_URL = "https://feedback-app-vercel-serverless.vercel.app/api"
    private const val DEV_URL =
        "https://feedback-app-vercel-serverless-git-dev-jesusdmedinac.vercel.app/api"
    private const val PATH_GET_PAGES = "/page/all"
    private const val PATH_ADD_ANSWER = "/answer/add"

    private val BASE_URL get() = if (isDevMode()) DEV_URL else PROD_URL
    val GET_PAGES get() = BASE_URL + PATH_GET_PAGES
    val ADD_ANSWER get() = BASE_URL + PATH_ADD_ANSWER
}
