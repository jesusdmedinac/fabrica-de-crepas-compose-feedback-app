package model

import kotlinx.serialization.Serializable

@Serializable
data class AnswerResponse(
    val answer: Answer = Answer(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
