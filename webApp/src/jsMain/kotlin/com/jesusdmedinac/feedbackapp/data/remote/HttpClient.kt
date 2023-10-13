package com.jesusdmedinac.feedbackapp.data.remote

import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.CommonDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPage
import com.jesusdmedinac.feedbackapp.data.model.CommonDataPageType
import com.jesusdmedinac.feedbackapp.data.model.CommonDataQuestionResponse
import com.jesusdmedinac.feedbackapp.data.model.CommonDataRateStar
import com.jesusdmedinac.feedbackapp.data.model.JSDataAnswer
import com.jesusdmedinac.feedbackapp.data.model.JSDataAnswerPayload
import com.jesusdmedinac.feedbackapp.data.model.JSDataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.JSDataAnswerResponse
import com.jesusdmedinac.feedbackapp.data.model.JSDataPage
import com.jesusdmedinac.feedbackapp.data.model.JSDataPageResponse
import com.jesusdmedinac.feedbackapp.data.model.JSDataRateStar
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.JsClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

actual fun createWrappedHttpClient(): WrappedHttpClient = object : WrappedHttpClient {
    private val ktorClient = HttpClient(JsClient()) {
        install(ContentNegotiation) {
            json(
                (
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                    ),
            )
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        console.log("Ktor: $message")
                    }
                }
                level = LogLevel.HEADERS
            }
        }
    }

    override suspend fun get(urlString: String): CommonDataQuestionResponse {
        val jsDataPageResponse: JSDataPageResponse = ktorClient.get(urlString).body()
        return jsDataPageResponse.toCommonDataQuestionResponse()
    }

    override suspend fun post(
        urlString: String,
        answer: CommonDataAnswer,
    ): CommonDataAnswerResponse {
        val answerPayload = answer.toJSDataAnswerPayload()
        val answerPayloadAsString = Json.encodeToString(answerPayload)

        val answerResponse: JSDataAnswerResponse = ktorClient.get(urlString) {
            url {
                parameters.append("answer", answerPayloadAsString)
            }
        }.body()

        return answerResponse.toCommonDataAnswerResponse()
    }
}

private fun CommonDataAnswer.toJSDataAnswerPayload(): JSDataAnswerPayload = JSDataAnswerPayload(
    answers.map { it.toJSDataAnswerPerQuestion() },
    author,
    created_at,
)

private fun CommonDataAnswerPerQuestion.toJSDataAnswerPerQuestion(): JSDataAnswerPerQuestion =
    JSDataAnswerPerQuestion(question, order, rating)

private fun JSDataAnswerResponse.toCommonDataAnswerResponse(): CommonDataAnswerResponse {
    return CommonDataAnswerResponse(
        answer.toCommonDataAnswer(),
        path,
        query,
        cookies,
    )
}

private fun JSDataAnswer.toCommonDataAnswer(): CommonDataAnswer = CommonDataAnswer(
    answers.map { it.toCommonDataAnswerPerQuestion() },
    author,
    created_at,
)

private fun JSDataAnswerPerQuestion.toCommonDataAnswerPerQuestion(): CommonDataAnswerPerQuestion =
    CommonDataAnswerPerQuestion(question, order, rating)

private fun JSDataPageResponse.toCommonDataQuestionResponse(): CommonDataQuestionResponse =
    CommonDataQuestionResponse(
        pages.map { it.toCommonDataPage() },
        path,
        query,
        cookies,
    )

private fun JSDataPage.toCommonDataPage(): CommonDataPage =
    CommonDataPage(order, text, image, rating.toCommonDataRateStar(), type.toCommonDataPageType())

fun JSDataRateStar.toCommonDataRateStar(): CommonDataRateStar = when (this) {
    JSDataRateStar.UNSELECTED -> CommonDataRateStar.UNSELECTED
    JSDataRateStar.ONE -> CommonDataRateStar.ONE
    JSDataRateStar.TWO -> CommonDataRateStar.TWO
    JSDataRateStar.THREE -> CommonDataRateStar.THREE
    JSDataRateStar.FOUR -> CommonDataRateStar.FOUR
    JSDataRateStar.FIVE -> CommonDataRateStar.FIVE
}

fun String.toCommonDataPageType(): CommonDataPageType = when (this) {
    "MESSAGE" -> CommonDataPageType.MESSAGE
    "QUESTION" -> CommonDataPageType.QUESTION
    else -> CommonDataPageType.UNKNOWN
}
