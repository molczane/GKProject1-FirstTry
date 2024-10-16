package org.example.project.utils

import androidx.compose.ui.geometry.Offset

data class CubicBezierSegment(
    var start: Offset,
    var control1: Offset,
    var control2: Offset,
    var end: Offset,
    var lineIndex: Int
)
