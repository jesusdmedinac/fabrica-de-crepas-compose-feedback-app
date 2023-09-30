package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.runtime.Composable
import com.jesusdmedinac.feedbackapp.data.remote.QuestionRemoteDataSourceImpl
import com.jesusdmedinac.feedbackapp.data.remote.WrappedHttpClient
import com.jesusdmedinac.feedbackapp.data.remote.createWrappedHttpClient

@Composable
fun FeedbackAppWithDependencies() {
    val httpClient: WrappedHttpClient = createWrappedHttpClient()
    val questionRemoteDataSource = QuestionRemoteDataSourceImpl(httpClient)
    FeedbackAppWithTheme(questionRemoteDataSource)
}