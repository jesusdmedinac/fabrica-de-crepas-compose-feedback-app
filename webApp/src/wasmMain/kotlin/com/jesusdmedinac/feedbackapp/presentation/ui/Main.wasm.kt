package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow("FeedbackApp") {
        FeedbackAppWithDependencies()
    }
}
