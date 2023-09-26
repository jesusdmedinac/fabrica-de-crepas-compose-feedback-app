package model

import kotlinx.serialization.Serializable

@Serializable
data class Question(
    val order: Int = -1,
    val question: String = "",
    val image: String = "",
)

enum class RateStar(val value: Int) {
    UNSELECTED(0),
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
}
