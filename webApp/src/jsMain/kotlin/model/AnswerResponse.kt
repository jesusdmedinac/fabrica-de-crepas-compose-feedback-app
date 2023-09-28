package model

import kotlinx.serialization.Serializable

@Serializable
data class AnswerResponse(
    val answerPayload: AnswerPayload = AnswerPayload(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
