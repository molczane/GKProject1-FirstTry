package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class BezierControlPoint(
    val offset: Offset,
    val color: Color,
    val radius: Float
)
