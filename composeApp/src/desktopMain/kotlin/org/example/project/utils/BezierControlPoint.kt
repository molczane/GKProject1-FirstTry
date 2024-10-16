package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class BezierControlPoint(
    var offset: Offset,
    var color: Color = Color.Blue,
    var radius: Float = 4F,
    var lineIndex: Int,
    var index: Int
)
