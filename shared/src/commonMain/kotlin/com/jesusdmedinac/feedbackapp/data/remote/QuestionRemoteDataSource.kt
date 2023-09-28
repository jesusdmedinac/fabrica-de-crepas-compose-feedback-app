package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.Answer
import com.jesusdmedinac.feedbackapp.data.model.AnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.Question
import example.imageviewer.model.WrappedHttpClient

interface QuestionRemoteDataSource {
    suspend fun getQuestions(): List<Question>
    suspend fun addAnswer(answer: Answer): AnswerResponse
}

class QuestionRemoteDataSourceImpl(
    private val httpClient: WrappedHttpClient,
) : QuestionRemoteDataSource {
    override suspend fun getQuestions(): List<Question> =
        httpClient.get(
            "https://feedback-app-vercel-serverless.vercel.app/api/question/all",
        ).questions

    override suspend fun addAnswer(answer: Answer): AnswerResponse = httpClient.post(
        urlString = "https://feedback-app-vercel-serverless.vercel.app/api/answer/add",
        answer,
    )
}
