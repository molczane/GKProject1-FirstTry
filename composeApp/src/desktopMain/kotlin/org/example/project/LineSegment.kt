package org.example.project

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class LineSegment(val start: Offset, val end: Offset, val color: Color = Color.Red)
