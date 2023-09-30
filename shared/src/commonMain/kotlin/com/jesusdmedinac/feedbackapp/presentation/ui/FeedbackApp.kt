package com.jesusdmedinac.feedbackapp.presentation.ui

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestion
import com.jesusdmedinac.feedbackapp.data.remote.QuestionRemoteDataSource
import com.jesusdmedinac.feedbackapp.domain.model.RateStar
import com.jesusdmedinac.feedbackapp.presentation.ui.style.FeedbackAppTheme
import com.jesusdmedinac.feedbackapp.utils.currentTimeInMillis
import com.jesusdmedinac.feedbackapp.utils.toImageBitmap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import com.jesusdmedinac.feedbackapp.data.model.CommonDataRateStar as DataRateStar
import com.jesusdmedinac.feedbackapp.domain.model.Question as DomainQuestion
import com.jesusdmedinac.feedbackapp.domain.model.RateStar as DomainRateStar

@Composable
fun FeedbackAppWithTheme(questionRemoteDataSource: QuestionRemoteDataSource) {
    FeedbackAppTheme {
        FeedbackAppContent(questionRemoteDataSource)
    }
}

@Composable
fun FeedbackAppContent(questionRemoteDataSource: QuestionRemoteDataSource) {
    val feedbackAppState = FeedbackAppState(questionRemoteDataSource)
    val coroutineScope: CoroutineScope = rememberCoroutineScope { Dispatchers.Unconfined }
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            feedbackAppState.getQuestions()
        }
    }

    val isLoading by feedbackAppState.isLoading.collectAsState()
    val listOfQuestions by feedbackAppState.listOfQuestions.collectAsState()
    val currentQuestion by feedbackAppState.currentQuestion.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
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
                QuestionBlock(currentQuestion)
                Spacer(modifier = Modifier.size(32.dp))
                RatingBlock(
                    isEnabled = !isLoading,
                    rating = currentQuestion.rating,
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
            QuestionImage(currentQuestion)
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

private class FeedbackAppState(private val questionRemoteDataSource: QuestionRemoteDataSource) :
    FeedbackAppBehavior {
    private val _listOfQuestions = MutableStateFlow(emptyList<DomainQuestion>())
    val listOfQuestions: StateFlow<List<DomainQuestion>> get() = _listOfQuestions
    private val _currentQuestion = MutableStateFlow(DomainQuestion())
    val currentQuestion: StateFlow<DomainQuestion> get() = _currentQuestion
    val isPreviousButtonEnabled: Boolean
        get() = !isLoading.value && currentQuestion.value.order > 1

    val isNextButtonEnabled: Boolean
        get() = !isLoading.value &&
            currentQuestion.value.order < listOfQuestions.value.size &&
            listOfQuestions
                .value
                .any { it.rating != DomainRateStar.UNSELECTED } &&
            currentQuestion.value.rating != DomainRateStar.UNSELECTED
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    override suspend fun getQuestions() {
        _listOfQuestions.value = questionRemoteDataSource.getQuestions()
            .map { it.toDomain() }
            .sortedBy { it.order }
        _currentQuestion.value = listOfQuestions.value.first()
    }

    override fun onPreviousClick() {
        val previousQuestion = listOfQuestions
            .value
            .firstOrNull { it.order == _currentQuestion.value.order - 1 }

        previousQuestion
            ?.let { _currentQuestion.value = it }
    }

    override fun onNextClick() {
        val nextQuestion = listOfQuestions
            .value
            .firstOrNull { it.order == _currentQuestion.value.order + 1 }

        nextQuestion
            ?.let { _currentQuestion.value = it }
    }

    override suspend fun onStartClick(rateStar: RateStar) {
        _currentQuestion.value = currentQuestion.value.copy(
            rating = rateStar,
        )
        _listOfQuestions.value = listOfQuestions.value.map { question ->
            if (question.order == currentQuestion.value.order) {
                question.copy(
                    rating = rateStar,
                )
            } else {
                question
            }
        }
        if (listOfQuestions.value.all { it.rating != DomainRateStar.UNSELECTED }) {
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
                    it.question,
                    it.order,
                    it.rating.value,
                )
            },
            author = "default",
            created_at = currentTimeInMillis(),
        )
        questionRemoteDataSource.addAnswer(answer)
        controlledDelay()
        getQuestions()
    }
}

private interface FeedbackAppBehavior {
    suspend fun getQuestions()
    fun onPreviousClick()

    fun onNextClick()

    suspend fun onStartClick(rateStar: RateStar)
    suspend fun controlledDelay()
    suspend fun addAnswer()
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun RowScope.QuestionImage(currentQuestion: com.jesusdmedinac.feedbackapp.domain.model.Question) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .weight(0.4f),
        contentAlignment = Alignment.Center,
    ) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        LaunchedEffect(currentQuestion.image) {
            if (currentQuestion.image.isNotEmpty()) {
                imageBitmap = resource(currentQuestion.image)
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

private fun CommonDataQuestion.toDomain(): DomainQuestion = DomainQuestion(
    order = order,
    question = question,
    image = image,
    rating = when (rating) {
        DataRateStar.UNSELECTED -> DomainRateStar.UNSELECTED
        DataRateStar.ONE -> DomainRateStar.ONE
        DataRateStar.TWO -> DomainRateStar.TWO
        DataRateStar.THREE -> DomainRateStar.THREE
        DataRateStar.FOUR -> DomainRateStar.FOUR
        DataRateStar.FIVE -> DomainRateStar.FIVE
    },
)

@Composable
fun QuestionBlock(
    question: DomainQuestion,
    modifier: Modifier = Modifier,
) {
    Row {
        Text(
            text = "${question.order}.- ",
            modifier = modifier,
            fontSize = 32.sp,
        )
        Text(
            text = question.question,
            modifier = modifier,
            fontSize = 32.sp,
        )
    }
}

@Composable
private fun RatingBlock(
    rating: DomainRateStar,
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    onStarClick: (DomainRateStar) -> Unit = {},
) {
    Row(
        modifier = modifier,
    ) {
        DomainRateStar
            .values()
            .filterNot { it == DomainRateStar.UNSELECTED }
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
