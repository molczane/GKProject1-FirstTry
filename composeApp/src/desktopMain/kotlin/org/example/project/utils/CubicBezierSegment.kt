package org.example.project.utils

import androidx.compose.ui.geometry.Offset

data class CubicBezierSegment(
    val start: Offset,
    val control1: Offset,
    val control2: Offset,
    val end: Offset
)
