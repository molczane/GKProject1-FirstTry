package org.example.project.utils

import OffsetSerializer
import androidx.compose.ui.geometry.Offset
import kotlinx.serialization.Serializable

@Serializable
data class CubicBezierSegment(
    @Serializable(with = OffsetSerializer::class) var start: Offset,
    @Serializable(with = OffsetSerializer::class) var control1: Offset,
    @Serializable(with = OffsetSerializer::class) var control2: Offset,
    @Serializable(with = OffsetSerializer::class) var end: Offset,
    var lineIndex: Int
)
