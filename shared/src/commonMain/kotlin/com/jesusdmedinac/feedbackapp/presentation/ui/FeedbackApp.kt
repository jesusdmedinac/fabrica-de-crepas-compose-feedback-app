package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPageType
import com.jesusdmedinac.feedbackapp.data.model.CommonDataRateStar
import com.jesusdmedinac.feedbackapp.data.remote.PageRemoteDataSource
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPage
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPageType
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainRateStar
import com.jesusdmedinac.feedbackapp.presentation.ui.composable.MessagePage
import com.jesusdmedinac.feedbackapp.presentation.ui.composable.QuestionPage
import com.jesusdmedinac.feedbackapp.presentation.ui.shape.TriangleShape
import com.jesusdmedinac.feedbackapp.presentation.ui.style.FeedbackAppTheme
import com.jesusdmedinac.feedbackapp.utils.currentTimeInMillis
import com.jesusdmedinac.feedbackapp.utils.isDevMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@Composable
fun FeedbackAppWithTheme(
    pageRemoteDataSource: PageRemoteDataSource,
) {
    FeedbackAppTheme {
        FeedbackAppContent(pageRemoteDataSource)
    }
}

@Composable
fun FeedbackAppContent(
    pageRemoteDataSource: PageRemoteDataSource,
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
    val feedbackAppState = FeedbackAppState(pageRemoteDataSource, coroutineScope)
    LaunchedEffect(Unit) {
        feedbackAppState.getPages()
    }

    val isLoading by feedbackAppState.isLoading.collectAsState()
    val currentPage: CommonDomainPage by feedbackAppState.currentPage.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (currentPage.type == CommonDomainPageType.MESSAGE) {
                MessagePage(
                    page = currentPage,
                    onSendNewAnswerClick = {
                        feedbackAppState.sendNewAnswer()
                    },
                )
            } else {
                QuestionPage(
                    isLoading = isLoading,
                    page = currentPage,
                    isPreviousButtonEnabled = feedbackAppState.isPreviousButtonEnabled,
                    isNextButtonEnabled = feedbackAppState.isNextButtonEnabled,
                    feedbackAppBehavior = feedbackAppState,
                )
            }
            if (isDevMode()) {
                Box(
                    contentAlignment = Alignment.TopStart,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(TriangleShape())
                        .background(Color.Green),
                ) {
                    Text("Dev Mode")
                }
            }
        }
    }
}

class FeedbackAppState(
    private val pageRemoteDataSource: PageRemoteDataSource,
    private val coroutineScope: CoroutineScope,
) :
    FeedbackAppBehavior {
    private val _listOfPages = MutableStateFlow(emptyList<CommonDomainPage>())
    val listOfPages: StateFlow<List<CommonDomainPage>> get() = _listOfPages
    private val _currentPage = MutableStateFlow(CommonDomainPage())
    val currentPage: StateFlow<CommonDomainPage> get() = _currentPage
    val isPreviousButtonEnabled: Boolean
        get() = !isLoading.value && currentPage.value.order > 1

    val isNextButtonEnabled: Boolean
        get() = !isLoading.value &&
            currentPage.value.order < listOfPages.value.size &&
            listOfPages
                .value
                .any { it.rating != CommonDomainRateStar.UNSELECTED } &&
            currentPage.value.rating != CommonDomainRateStar.UNSELECTED
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    override fun getPages() {
        coroutineScope.launch {
            _listOfPages.value = pageRemoteDataSource.getPages()
                .map { it.toDomain() }
                .sortedBy { it.order }
            _currentPage.value = listOfPages.value.first()
        }
    }

    override fun onPreviousClick() {
        listOfPages
            .value
            .firstOrNull { it.order == _currentPage.value.order - 1 }
            ?.let { _currentPage.value = it }
    }

    override fun onNextClick() {
        listOfPages
            .value
            .firstOrNull { it.order == _currentPage.value.order + 1 }
            ?.let { _currentPage.value = it }
    }

    override fun onStartClick(commonDomainRateStar: CommonDomainRateStar) {
        _currentPage.value = currentPage.value.copy(
            rating = commonDomainRateStar,
        )
        _listOfPages.value = listOfPages.value.map { page ->
            if (page.order == currentPage.value.order) {
                page.copy(
                    rating = commonDomainRateStar,
                )
            } else {
                page
            }
        }
        if (listOfPages
                .value
                .filter { it.type == CommonDomainPageType.QUESTION }
                .all { it.rating != CommonDomainRateStar.UNSELECTED }
        ) {
            addAnswer()
        }
        controlledDelay {
            onNextClick()
        }
    }

    private fun controlledDelay(doAfterDelay: () -> Unit) {
        coroutineScope.launch {
            _isLoading.value = true
            delay(214)
            doAfterDelay()
            _isLoading.value = false
        }
    }

    override fun addAnswer() {
        val answer = CommonDataAnswer(
            answers = listOfPages.value.map {
                CommonDataAnswerPerQuestion(
                    it.text,
                    it.order,
                    it.rating.value,
                )
            },
            author = "default",
            created_at = currentTimeInMillis(),
        )
        coroutineScope.launch {
            pageRemoteDataSource.addAnswer(answer)
        }
    }

    override fun sendNewAnswer() {
        coroutineScope.launch {
            controlledDelay {
                getPages()
            }
        }
    }

    private fun CommonDataPage.toDomain(): CommonDomainPage = CommonDomainPage(
        order = order,
        text = text,
        image = image,
        rating = when (rating) {
            CommonDataRateStar.UNSELECTED -> CommonDomainRateStar.UNSELECTED
            CommonDataRateStar.ONE -> CommonDomainRateStar.ONE
            CommonDataRateStar.TWO -> CommonDomainRateStar.TWO
            CommonDataRateStar.THREE -> CommonDomainRateStar.THREE
            CommonDataRateStar.FOUR -> CommonDomainRateStar.FOUR
            CommonDataRateStar.FIVE -> CommonDomainRateStar.FIVE
        },
        type = when (type) {
            CommonDataPageType.MESSAGE -> CommonDomainPageType.MESSAGE
            CommonDataPageType.QUESTION -> CommonDomainPageType.QUESTION
            CommonDataPageType.UNKNOWN -> CommonDomainPageType.UNKNOWN
        },
    )
}

interface FeedbackAppBehavior {
    fun getPages()
    fun onPreviousClick()

    fun onNextClick()

    fun onStartClick(commonDomainRateStar: CommonDomainRateStar)
    fun addAnswer()
    fun sendNewAnswer()
}
