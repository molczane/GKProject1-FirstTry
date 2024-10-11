package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.pow

// Funkcja rysująca krzywą kubiczną Beziera
fun DrawScope.drawCubicBezier(start: Offset, control1: Offset, control2: Offset, end: Offset, color: Color) {
    val steps = 200 // Im więcej kroków, tym bardziej gładka krzywa
    var previousPoint = start

    for (i in 1..steps) {
        val t = i / steps.toFloat()
        val x = (1 - t).pow(3) * start.x + 3 * (1 - t).pow(2) * t * control1.x + 3 * (1 - t) * t.pow(2) * control2.x + t.pow(3) * end.x
        val y = (1 - t).pow(3) * start.y + 3 * (1 - t).pow(2) * t * control1.y + 3 * (1 - t) * t.pow(2) * control2.y + t.pow(3) * end.y
        val currentPoint = Offset(x, y)

        //drawLine(color = color, start = previousPoint, end = currentPoint, strokeWidth = 2f)
        drawBresenhamLine(previousPoint, currentPoint, color)
        previousPoint = currentPoint
    }
}