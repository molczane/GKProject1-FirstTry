package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope

// Funkcja rysująca przerywaną linię (wielobok kontrolny)
fun DrawScope.drawDashedLine(start: Offset, end: Offset, color: Color) {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), phase = 0f)
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = 2f,
        pathEffect = pathEffect
    )
}