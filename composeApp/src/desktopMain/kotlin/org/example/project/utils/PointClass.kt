package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class PointClass(
    val offset: Offset,
    val color: Color = Color.Red,
    val radius: Float = 4F
)
