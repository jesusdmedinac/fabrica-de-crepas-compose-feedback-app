package model

import kotlinx.serialization.Serializable

@Serializable
data class Answer(
    val answers: List<AnswerPerQuestion> = listOf(),
    val author: String = "",
    val created_at: Long = 0L,
)

@Serializable
data class AnswerPerQuestion(
    val question: String = "",
    val order: Int = -1,
    val rating: Int = 0,
)
