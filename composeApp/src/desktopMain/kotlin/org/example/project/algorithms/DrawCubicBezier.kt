package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.pow

// Funkcja rysująca krzywą kubiczną Beziera
fun DrawScope.drawCubicBezierBrasenham(start: Offset, control1: Offset, control2: Offset, end: Offset, color: Color = Color.Black) {
    val steps = 200 // Im więcej kroków, tym bardziej gładka krzywa
    var previousPoint = start

    // Rysowanie przerywanego wieloboku kontrolnego
    drawDashedLine(start, control1, Color.Gray)
    drawDashedLine(control1, control2, Color.Gray)
    drawDashedLine(control2, end, Color.Gray)
    drawDashedLine(start, end, Color.Gray)

    // Rysowanie punktów kontrolnych
    drawCircle(
        color = Color.Blue,
        center = control1,
        radius = 3.dp.toPx()
    )
    drawCircle(
        color = Color.Blue,
        center = control2,
        radius = 3.dp.toPx()
    )

    for (i in 1..steps) {
        val t = i / steps.toFloat()
        val x = (1 - t).pow(3) * start.x + 3 * (1 - t).pow(2) * t * control1.x + 3 * (1 - t) * t.pow(2) * control2.x + t.pow(3) * end.x
        val y = (1 - t).pow(3) * start.y + 3 * (1 - t).pow(2) * t * control1.y + 3 * (1 - t) * t.pow(2) * control2.y + t.pow(3) * end.y
        val currentPoint = Offset(x, y)

        drawBresenhamLine(previousPoint, currentPoint, color)
        previousPoint = currentPoint
    }
}

fun DrawScope.drawCubicBezierBrasenhamIncremental(start: Offset, control1: Offset, control2: Offset, end: Offset, color: Color = Color.Black) {
    val steps = 200
    var previousPoint = start

    // Rysowanie przerywanego wieloboku kontrolnego
    drawDashedLine(start, control1, Color.Gray)
    drawDashedLine(control1, control2, Color.Gray)
    drawDashedLine(control2, end, Color.Gray)
    drawDashedLine(start, end, Color.Gray)

    // Rysowanie punktów kontrolnych
    drawCircle(
        color = Color.Blue,
        center = control1,
        radius = 3.dp.toPx()
    )
    drawCircle(
        color = Color.Blue,
        center = control2,
        radius = 3.dp.toPx()
    )

    // Rysowanie krzywej Beziera
    var t = 0f
    val dt = 1f / steps

    for (i in 1..steps) {
        // Zastosowanie iteracyjnego wzoru de Casteljau do wyznaczania punktów na krzywej
        val p0 = lerp(start, control1, t)
        val p1 = lerp(control1, control2, t)
        val p2 = lerp(control2, end, t)

        val p01 = lerp(p0, p1, t)
        val p12 = lerp(p1, p2, t)

        val currentPoint = lerp(p01, p12, t)

        drawBresenhamLine(previousPoint, currentPoint, color)
        previousPoint = currentPoint
        t += dt
    }
}

// Funkcja pomocnicza do interpolacji liniowej między dwoma punktami
fun lerp(a: Offset, b: Offset, t: Float): Offset {
    return Offset(
        x = a.x + (b.x - a.x) * t,
        y = a.y + (b.y - a.y) * t
    )
}

fun DrawScope.drawCubicBezierWu(start: Offset, control1: Offset, control2: Offset, end: Offset, color: Color = Color.Black) {
    val steps = 200 // Im więcej kroków, tym bardziej gładka krzywa
    var previousPoint = start

    // Rysowanie przerywanego wieloboku kontrolnego
    drawDashedLine(start, control1, Color.Gray)
    drawDashedLine(control1, control2, Color.Gray)
    drawDashedLine(control2, end, Color.Gray)
    drawDashedLine(start, end, Color.Gray)

    // Rysowanie punktów kontrolnych
    drawCircle(
        color = Color.Blue,
        center = control1,
        radius = 3.dp.toPx()
    )
    drawCircle(
        color = Color.Blue,
        center = control2,
        radius = 3.dp.toPx()
    )

    for (i in 1..steps) {
        val t = i / steps.toFloat()
        val x = (1 - t).pow(3) * start.x + 3 * (1 - t).pow(2) * t * control1.x + 3 * (1 - t) * t.pow(2) * control2.x + t.pow(3) * end.x
        val y = (1 - t).pow(3) * start.y + 3 * (1 - t).pow(2) * t * control1.y + 3 * (1 - t) * t.pow(2) * control2.y + t.pow(3) * end.y
        val currentPoint = Offset(x, y)

        drawWuLine(color = color, start = previousPoint, end = currentPoint)
        previousPoint = currentPoint
    }
}