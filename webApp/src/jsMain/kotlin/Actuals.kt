import com.jesusdmedinac.feedbackapp.data.model.Answer
import com.jesusdmedinac.feedbackapp.data.model.AnswerResponse
import example.imageviewer.model.WrappedHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.js.JsClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import model.AnswerPayload
import model.AnswerPerQuestion
import model.Question
import model.QuestionResponse
import com.jesusdmedinac.feedbackapp.data.model.Answer as DataAnswer
import com.jesusdmedinac.feedbackapp.data.model.AnswerPerQuestion as DataAnswerPerQuestion
import com.jesusdmedinac.feedbackapp.data.model.Question as DataQuestion
import com.jesusdmedinac.feedbackapp.data.model.QuestionResponse as DataQuestionResponse

actual fun createWrappedHttpClient(): WrappedHttpClient {
    return object : WrappedHttpClient {
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

        override suspend fun getAsBytes(urlString: String): ByteArray {
            return ktorClient.get(urlString).readBytes()
        }

        override suspend fun get(urlString: String): DataQuestionResponse {
            val questionResponse: QuestionResponse = ktorClient.get(urlString).body()
            return questionResponse.toDataQuestionResponse()
        }

        override suspend fun post(
            urlString: String,
            answer: DataAnswer,
        ): AnswerResponse {
            val answerPayload = answer.toAnswerPayload()
            val answerPayloadAsString = Json.encodeToString(answerPayload)

            val answerResponse: model.AnswerResponse = ktorClient.get(urlString) {
                url {
                    parameters.append("answer", answerPayloadAsString)
                }
            }.body()

            return answerResponse.toDataAnswerResponse()
        }
    }
}

private fun Answer.toAnswerPayload(): AnswerPayload = AnswerPayload(
    answers.map { it.toAnswerPerQuestion() },
    author,
    created_at,
)

private fun DataAnswerPerQuestion.toAnswerPerQuestion(): AnswerPerQuestion =
    AnswerPerQuestion(question, order, rating)

private fun model.AnswerResponse.toDataAnswerResponse(): AnswerResponse {
    return AnswerResponse(
        answerPayload.toDataAnswer(),
        path,
        query,
        cookies,
    )
}

private fun AnswerPayload.toDataAnswer(): DataAnswer = DataAnswer(
    answers.map { it.toDataAnswerPerQuestion() },
    author,
    created_at,
)

private fun AnswerPerQuestion.toDataAnswerPerQuestion(): DataAnswerPerQuestion =
    DataAnswerPerQuestion(question, order, rating)

private fun QuestionResponse.toDataQuestionResponse(): DataQuestionResponse =
    DataQuestionResponse(
        questions.map { it.toDataQuestion() },
        path,
        query,
        cookies,
    )

private fun Question.toDataQuestion(): DataQuestion = DataQuestion(order, question, image)
