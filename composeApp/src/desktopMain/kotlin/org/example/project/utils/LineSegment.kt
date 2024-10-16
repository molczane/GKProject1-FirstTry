package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class LineSegment(
    var start: Offset,
    var end: Offset,
    var color: Color = Color.Black,
    var strokeWidth: Float = 2F,
    var relation: Relations = Relations.None,
    var bezierSegment: CubicBezierSegment? = null
)
