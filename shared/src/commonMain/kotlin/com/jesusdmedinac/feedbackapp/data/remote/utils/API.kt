package com.jesusdmedinac.feedbackapp.data.remote.utils

import com.jesusdmedinac.feedbackapp.utils.isDevMode

object API {
    private const val HOST = "https://feedback-app-vercel-serverless.vercel.app"
    private const val PROD_PATH = "/api"
    private const val DEV_PATH = "/dev"
    private const val PATH_GET_PAGES = "/page/all"
    private const val PATH_ADD_ANSWER = "/answer/add"

    private val BASE_URL get() = if (isDevMode()) "$HOST$DEV_PATH" else "$HOST$PROD_PATH"
    val GET_PAGES get() = BASE_URL + PATH_GET_PAGES
    val ADD_ANSWER get() = BASE_URL + PATH_ADD_ANSWER
}
