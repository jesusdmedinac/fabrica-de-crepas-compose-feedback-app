package com.jesusdmedinac.feedbackapp.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.remote.PageRemoteDataSource

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FeedbackAppWithTheme(object : PageRemoteDataSource {
                override suspend fun getPages(): List<CommonDataPage> {
                    TODO("Not yet implemented")
                }

                override suspend fun addAnswer(answer: CommonDataAnswer): CommonDataAnswerResponse {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}
