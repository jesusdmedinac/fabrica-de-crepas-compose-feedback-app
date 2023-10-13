package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.remote.utils.API

class PageRemoteDataSourceImpl(
    private val httpClient: WrappedHttpClient,
) : PageRemoteDataSource {

    override suspend fun getPages(): List<CommonDataPage> =
        httpClient.get(
            API.GET_PAGES,
        ).commonDataPages

    override suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse =
        httpClient.post(
            urlString = API.ADD_ANSWER,
            answer,
        )
}
