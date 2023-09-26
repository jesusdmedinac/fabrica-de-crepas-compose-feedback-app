package model

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val questions: List<Question> = listOf(),
    val path: String = "",
    val query: String = "",
    val cookies: List<String> = listOf(),
)
