package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage

interface PageRemoteDataSource {
    suspend fun getPages(): List<CommonDataPage>
    suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse
}
