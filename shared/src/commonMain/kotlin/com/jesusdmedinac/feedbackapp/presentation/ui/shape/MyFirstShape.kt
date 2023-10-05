package com.jesusdmedinac.feedbackapp.presentation.ui.shape

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class TriangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val trianglePath = Path().apply {
            // Moves to top center position
            moveTo(0f, 0f)
            // Add line to right corner above circle
            lineTo(x = size.width, y = 0f)
            // Add line to left corner above circle
            lineTo(x = 0f, y = size.height)
        }
        return Outline.Generic(path = trianglePath)
    }
}
