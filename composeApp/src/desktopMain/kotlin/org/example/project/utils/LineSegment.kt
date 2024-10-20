package org.example.project.utils

import ColorSerializer
import OffsetSerializer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable

@Serializable
data class LineSegment(
    @Serializable(with = OffsetSerializer::class) var start: Offset,
    @Serializable(with = OffsetSerializer::class) var end: Offset,
    @Serializable(with = ColorSerializer::class) var color: Color = Color.Black,
    var strokeWidth: Float = 1F,
    var relation: Relations = Relations.None,
    var bezierSegment: CubicBezierSegment? = null
)
