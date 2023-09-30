package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestionResponse

interface WrappedHttpClient {
    suspend fun get(urlString: String): CommonDataQuestionResponse
    suspend fun post(urlString: String, answer: CommonDataAnswer): CommonDataAnswerResponse
}
