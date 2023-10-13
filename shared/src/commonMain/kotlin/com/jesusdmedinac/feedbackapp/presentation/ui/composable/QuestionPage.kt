package com.jesusdmedinac.feedbackapp.presentation.ui.composable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainPage
import com.jesusdmedinac.feedbackapp.domain.model.CommonDomainRateStar
import com.jesusdmedinac.feedbackapp.presentation.ui.FeedbackAppBehavior
import com.jesusdmedinac.feedbackapp.utils.toImageBitmap
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

@Composable
fun QuestionPage(
    isLoading: Boolean,
    page: CommonDomainPage,
    isPreviousButtonEnabled: Boolean,
    isNextButtonEnabled: Boolean,
    feedbackAppBehavior: FeedbackAppBehavior,
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
            QuestionBlock(page)
            Spacer(modifier = Modifier.size(32.dp))
            RatingBlock(
                isEnabled = !isLoading,
                rating = page.rating,
                onStarClick = { rateStar ->
                    feedbackAppBehavior.onStartClick(rateStar)
                },
            )
            QuestionPagerControl(
                feedbackAppBehavior = feedbackAppBehavior,
                isPreviousButtonEnabled = isPreviousButtonEnabled,
                isNextButtonEnabled = isNextButtonEnabled,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        QuestionImage(page)
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

@OptIn(ExperimentalResourceApi::class, ExperimentalAnimationApi::class)
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

        AnimatedContent(
            targetState = imageBitmap,
            transitionSpec = {
                if (currentPage.isForward) {
                    slideInVertically { height -> height } + fadeIn() with
                        slideOutVertically { height -> -height } + fadeOut()
                } else {
                    slideInVertically { height -> -height } + fadeIn() with
                        slideOutVertically { height -> height } + fadeOut()
                }
            },
        ) { bitmap ->
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
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun QuestionBlock(
    commonDomainPage: CommonDomainPage,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = commonDomainPage,
        transitionSpec = {
            fadeIn() with fadeOut()
        },
    ) { page ->
        Row {
            Text(
                text = "${page.order}.- ",
                modifier = modifier,
                fontSize = 32.sp,
            )
            Text(
                text = page.text,
                modifier = modifier,
                fontSize = 32.sp,
            )
        }
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
                    val starTint by animateColorAsState(
                        targetValue = if (rateStar.value <= rating.value) {
                            Color.Red
                        } else {
                            Color.White
                        },
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Rate star",
                        tint = starTint,
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
