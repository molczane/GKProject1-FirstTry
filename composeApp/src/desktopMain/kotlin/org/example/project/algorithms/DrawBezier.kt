package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// Funkcja rysująca krzywą kwadratową Beziera
fun DrawScope.drawQuadraticBezier(start: Offset, control: Offset, end: Offset, color: Color) {
    val steps = 100 // Im więcej kroków, tym bardziej gładka krzywa
    var previousPoint = start

    for (i in 1..steps) {
        val t = i / steps.toFloat()
        val x = (1 - t) * (1 - t) * start.x + 2 * (1 - t) * t * control.x + t * t * end.x
        val y = (1 - t) * (1 - t) * start.y + 2 * (1 - t) * t * control.y + t * t * end.y
        val currentPoint = Offset(x, y)

        drawLine(color = color, start = previousPoint, end = currentPoint, strokeWidth = 2f)
        previousPoint = currentPoint
    }
}