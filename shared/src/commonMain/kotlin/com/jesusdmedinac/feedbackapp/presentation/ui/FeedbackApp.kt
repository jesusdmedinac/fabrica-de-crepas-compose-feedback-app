package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPageType
import com.jesusdmedinac.feedbackapp.data.model.CommonDataRateStar
import com.jesusdmedinac.feedbackapp.data.remote.PageRemoteDataSource
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPage
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPageType
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainRateStar
import com.jesusdmedinac.feedbackapp.presentation.ui.shape.TriangleShape
import com.jesusdmedinac.feedbackapp.presentation.ui.style.FeedbackAppTheme
import com.jesusdmedinac.feedbackapp.utils.currentTimeInMillis
import com.jesusdmedinac.feedbackapp.utils.isDevMode
import com.jesusdmedinac.feedbackapp.utils.toImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

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
    val feedbackAppState = FeedbackAppState(pageRemoteDataSource)
    val coroutineScope: CoroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            feedbackAppState.getQuestions()
        }
    }

    val isLoading by feedbackAppState.isLoading.collectAsState()
    val currentPage: CommonDomainPage by feedbackAppState.currentQuestion.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (currentPage.type == CommonDomainPageType.MESSAGE) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        currentPage.text,
                        fontSize = 32.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                feedbackAppState.sendNewAnswer()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    ) {
                        Text(
                            "Enviar otra respuesta",
                            fontSize = 32.sp,
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(0.6f)
                            .padding(56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        QuestionBlock(currentPage)
                        Spacer(modifier = Modifier.size(32.dp))
                        RatingBlock(
                            isEnabled = !isLoading,
                            rating = currentPage.rating,
                            onStarClick = { rateStar ->
                                coroutineScope.launch {
                                    feedbackAppState.onStartClick(rateStar)
                                }
                            },
                        )
                        QuestionPagerControl(
                            feedbackAppBehavior = feedbackAppState,
                            isPreviousButtonEnabled = feedbackAppState.isPreviousButtonEnabled,
                            isNextButtonEnabled = feedbackAppState.isNextButtonEnabled,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    QuestionImage(currentPage)
                }
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

@Composable
private fun QuestionPagerControl(
    feedbackAppBehavior: FeedbackAppBehavior,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
    ) {
        Button(
            onClick = {
                feedbackAppBehavior.onPreviousClick()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            enabled = isPreviousButtonEnabled,
        ) {
            Icon(
                Icons.Default.KeyboardArrowUp,
                contentDescription = "Previous question",
            )
        }

        Button(
            onClick = {
                feedbackAppBehavior.onNextClick()
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            enabled = isNextButtonEnabled,
        ) {
            Icon(
                Icons.Default.KeyboardArrowDown,
                contentDescription = "Next question",
            )
        }
    }
}

private class FeedbackAppState(private val pageRemoteDataSource: PageRemoteDataSource) :
    FeedbackAppBehavior {
    private val _listOfQuestions = MutableStateFlow(emptyList<CommonDomainPage>())
    val listOfQuestions: StateFlow<List<CommonDomainPage>> get() = _listOfQuestions
    private val _currentQuestion = MutableStateFlow(CommonDomainPage())
    val currentQuestion: StateFlow<CommonDomainPage> get() = _currentQuestion
    val isPreviousButtonEnabled: Boolean
        get() = !isLoading.value && currentQuestion.value.order > 1

    val isNextButtonEnabled: Boolean
        get() = !isLoading.value &&
            currentQuestion.value.order < listOfQuestions.value.size &&
            listOfQuestions
                .value
                .any { it.rating != CommonDomainRateStar.UNSELECTED } &&
            currentQuestion.value.rating != CommonDomainRateStar.UNSELECTED
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    override suspend fun getQuestions() {
        _listOfQuestions.value = pageRemoteDataSource.getPages()
            .map { it.toDomain() }
            .sortedBy { it.order }
        _currentQuestion.value = listOfQuestions.value.first()
    }

    override fun onPreviousClick() {
        listOfQuestions
            .value
            .firstOrNull { it.order == _currentQuestion.value.order - 1 }
            ?.let { _currentQuestion.value = it }
    }

    override fun onNextClick() {
        listOfQuestions
            .value
            .firstOrNull { it.order == _currentQuestion.value.order + 1 }
            ?.let { _currentQuestion.value = it }
    }

    override suspend fun onStartClick(commonDomainRateStar: CommonDomainRateStar) {
        _currentQuestion.value = currentQuestion.value.copy(
            rating = commonDomainRateStar,
        )
        _listOfQuestions.value = listOfQuestions.value.map { question ->
            if (question.order == currentQuestion.value.order) {
                question.copy(
                    rating = commonDomainRateStar,
                )
            } else {
                question
            }
        }
        if (listOfQuestions.value.all { it.rating != CommonDomainRateStar.UNSELECTED }) {
            addAnswer()
        } else {
            controlledDelay()
            onNextClick()
        }
    }

    override suspend fun controlledDelay() {
        _isLoading.value = true
        delay(214)
        _isLoading.value = false
    }

    override suspend fun addAnswer() {
        val answer = CommonDataAnswer(
            answers = listOfQuestions.value.map {
                CommonDataAnswerPerQuestion(
                    it.text,
                    it.order,
                    it.rating.value,
                )
            },
            author = "default",
            created_at = currentTimeInMillis(),
        )
        pageRemoteDataSource.addAnswer(answer)
    }

    override suspend fun sendNewAnswer() {
        controlledDelay()
        getQuestions()
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

private interface FeedbackAppBehavior {
    suspend fun getQuestions()
    fun onPreviousClick()

    fun onNextClick()

    suspend fun onStartClick(commonDomainRateStar: CommonDomainRateStar)
    suspend fun controlledDelay()
    suspend fun addAnswer()
    suspend fun sendNewAnswer()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun RowScope.QuestionImage(currentPage: CommonDomainPage) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.4f),
        contentAlignment = Alignment.Center,
    ) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        LaunchedEffect(currentPage.image) {
            if (currentPage.image.isNotEmpty()) {
                imageBitmap = resource(currentPage.image)
                    .readBytes()
                    .toImageBitmap()
            }
        }
        imageBitmap?.let {
            Image(
                modifier = Modifier
                    .fillMaxSize(),
                painter = BitmapPainter(
                    image = it,
                ),
                contentDescription = "Feedback app question image",
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
fun QuestionBlock(
    commonDomainPage: CommonDomainPage,
    modifier: Modifier = Modifier,
) {
    Row {
        Text(
            text = "${commonDomainPage.order}.- ",
            modifier = modifier,
            fontSize = 32.sp,
        )
        Text(
            text = commonDomainPage.text,
            modifier = modifier,
            fontSize = 32.sp,
        )
    }
}

@Composable
private fun RatingBlock(
    rating: CommonDomainRateStar,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onStarClick: (CommonDomainRateStar) -> Unit = {},
) {
    Row(
        modifier = modifier,
    ) {
        CommonDomainRateStar
            .values()
            .filterNot { it == CommonDomainRateStar.UNSELECTED }
            .forEach { rateStar ->
                Box(
                    contentAlignment = Alignment.Center,
                    propagateMinConstraints = true,
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rate stroke star",
                        tint = Color.Red,
                        modifier = Modifier.size(64.dp),
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rate star",
                        tint = if (rateStar.value <= rating.value) {
                            Color.Red
                        } else {
                            Color.White
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clickable(
                                onClick = {
                                    onStarClick(rateStar)
                                },
                                enabled = isEnabled,
                            ),
                    )
                }
            }
    }
}
