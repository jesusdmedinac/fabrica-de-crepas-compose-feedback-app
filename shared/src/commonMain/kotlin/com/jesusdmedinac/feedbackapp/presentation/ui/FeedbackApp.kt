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
import com.jesusdmedinac.feedbackapp.data.model.Question
import com.jesusdmedinac.feedbackapp.data.remote.QuestionRemoteDataSource
import com.jesusdmedinac.feedbackapp.data.remote.QuestionRemoteDataSourceImpl
import com.jesusdmedinac.feedbackapp.presentation.ui.style.FeedbackAppTheme
import example.imageviewer.model.WrappedHttpClient
import example.imageviewer.toImageBitmap
import example.imageviewer.utils.ioDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource
import com.jesusdmedinac.feedbackapp.data.model.RateStar as DataRateStar
import com.jesusdmedinac.feedbackapp.domain.model.Question as DomainQuestion
import com.jesusdmedinac.feedbackapp.domain.model.RateStar as DomainRateStar

@Composable
fun FeedbackApp(httpClient: WrappedHttpClient) {
    var questionRemoteDataSource = QuestionRemoteDataSourceImpl(httpClient)

    FeedbackAppTheme {
        FeedbackAppContent(questionRemoteDataSource)
    }
}

@Composable
fun FeedbackAppContent(questionRemoteDataSource: QuestionRemoteDataSource) {
    var listOfQuestions by remember { mutableStateOf<List<Question>>(emptyList()) }
    var currentQuestion by remember { mutableStateOf(DomainQuestion()) }

    val composeScope = rememberCoroutineScope()
    val ioScope: CoroutineScope = rememberCoroutineScope { ioDispatcher }

    LaunchedEffect(Unit) {
        ioScope.launch {
            listOfQuestions = questionRemoteDataSource.getQuestions()
                .sortedBy { it.order }
            currentQuestion = listOfQuestions.first().toDomain()
        }
    }

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
                    rating = currentQuestion.rating,
                    onStarClick = {
                        currentQuestion = currentQuestion.copy(
                            rating = it,
                        )
                        listOfQuestions = listOfQuestions.map { question ->
                            if (question.order == currentQuestion.order) {
                                question.copy(
                                    rating = when (it) {
                                        DomainRateStar.UNSELECTED -> DataRateStar.UNSELECTED
                                        DomainRateStar.ONE -> DataRateStar.ONE
                                        DomainRateStar.TWO -> DataRateStar.TWO
                                        DomainRateStar.THREE -> DataRateStar.THREE
                                        DomainRateStar.FOUR -> DataRateStar.FOUR
                                        DomainRateStar.FIVE -> DataRateStar.FIVE
                                    },
                                )
                            } else {
                                question
                            }
                        }

                        composeScope.launch {
                            delay(1000)
                            val nextQuestion =
                                listOfQuestions.firstOrNull { it.order == currentQuestion.order + 1 }
                            nextQuestion?.let { question ->
                                currentQuestion = question.toDomain()
                            }
                        }
                    },
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Button(
                        onClick = {
                            val previousQuestion =
                                listOfQuestions.firstOrNull { it.order == currentQuestion.order - 1 }
                            previousQuestion?.let {
                                currentQuestion = it.toDomain()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowUp,
                            contentDescription = "Previous question",
                        )
                    }

                    if (currentQuestion.order >= (listOfQuestions.lastOrNull()?.order ?: -1)) {
                        Button(
                            onClick = {
                                // TODO Send answer to answer/add
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        ) {
                            Text(text = "Enviar")
                        }
                    } else {
                        Button(
                            onClick = {
                                val nextQuestion =
                                    listOfQuestions.firstOrNull { it.order == currentQuestion.order + 1 }
                                nextQuestion?.let {
                                    currentQuestion = it.toDomain()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        ) {
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = "Next question",
                            )
                        }
                    }
                }
            }
            QuestionImage(currentQuestion)
        }
    }
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

private fun Question.toDomain(): DomainQuestion = DomainQuestion(
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
fun RatingBlock(
    rating: DomainRateStar,
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
                            .clickable { onStarClick(rateStar) },
                    )
                }
            }
    }
}
