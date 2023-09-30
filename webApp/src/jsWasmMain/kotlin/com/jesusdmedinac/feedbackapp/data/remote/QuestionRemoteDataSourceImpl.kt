package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestion

class QuestionRemoteDataSourceImpl(
    private val httpClient: WrappedHttpClient,
) : QuestionRemoteDataSource {
    override suspend fun getQuestions(): List<CommonDataQuestion> =
        httpClient.get(
            "https://feedback-app-vercel-serverless.vercel.app/api/question/all",
        ).commonDataQuestions

    override suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse = httpClient.post(
        urlString = "https://feedback-app-vercel-serverless.vercel.app/api/answer/add",
        answer,
    )
}