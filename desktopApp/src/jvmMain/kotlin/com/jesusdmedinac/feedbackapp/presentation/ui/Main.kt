package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.ui.window.application
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.remote.PageRemoteDataSource

fun main() = application {
    FeedbackAppWithTheme(object : PageRemoteDataSource {
        override suspend fun getPages(): List<CommonDataPage> {
            TODO("Not yet implemented")
        }

        override suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse {
            TODO("Not yet implemented")
        }
    })
}
