package model

import kotlinx.serialization.Serializable

@Serializable
data class AnswerPayload(
    val answers: List<AnswerPerQuestion> = listOf(),
    val author: String = "",
    val created_at: Double = 0.0,
)

@Serializable
data class AnswerPerQuestion(
    val question: String = "",
    val order: Int = -1,
    val rating: Int = 0,
)
