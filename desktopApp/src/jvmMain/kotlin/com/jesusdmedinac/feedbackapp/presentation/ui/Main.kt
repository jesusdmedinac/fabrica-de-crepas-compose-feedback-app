package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.ui.window.application
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestion
import com.jesusdmedinac.feedbackapp.data.remote.QuestionRemoteDataSource

fun main() = application {
    FeedbackAppWithTheme(object : QuestionRemoteDataSource {
        override suspend fun getQuestions(): List<CommonDataQuestion> {
            TODO("Not yet implemented")
        }

        override suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse {
            TODO("Not yet implemented")
        }
    })
}
