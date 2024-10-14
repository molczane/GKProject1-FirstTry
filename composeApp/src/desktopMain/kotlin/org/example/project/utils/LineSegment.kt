package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class LineSegment(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Float = 2F,
    val relation: Relations = Relations.None,
    val bezierSegment: CubicBezierSegment? = null
)
