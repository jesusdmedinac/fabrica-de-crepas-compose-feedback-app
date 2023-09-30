package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestion

interface QuestionRemoteDataSource {
    suspend fun getQuestions(): List<CommonDataQuestion>
    suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse
}
