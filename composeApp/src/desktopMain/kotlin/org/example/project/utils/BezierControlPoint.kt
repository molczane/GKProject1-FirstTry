package org.example.project.utils

import ColorSerializer
import OffsetSerializer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class BezierControlPoint(
    @Serializable(with = OffsetSerializer::class) var offset: Offset,
    @Serializable(with = ColorSerializer::class) var color: Color = Color.Blue,
    var radius: Float = 4F,
    var lineIndex: Int,
    var index: Int
)
