package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBresenhamLine(start: Offset, end: Offset, color: Color, width: Float = 2f) {
    var x0 = start.x.toInt()
    var y0 = start.y.toInt()
    val x1 = end.x.toInt()
    val y1 = end.y.toInt()

    val dx = Math.abs(x1 - x0)
    val dy = Math.abs(y1 - y0)
    val sx = if (x0 < x1) 1 else -1
    val sy = if (y0 < y1) 1 else -1
    var err = dx - dy

    while (true) {
        // Rysowanie pojedynczego punktu w obliczonym miejscu
        //drawRect(color, topLeft = Offset(x0.toFloat(), y0.toFloat()), size = androidx.compose.ui.unit.IntSize(8, 8).toSize())

        drawCircle(color, radius = width, center = Offset(x0.toFloat(), y0.toFloat()))

        if (x0 == x1 && y0 == y1) break
        val e2 = 2 * err
        if (e2 > -dy) {
            err -= dy
            x0 += sx
        }
        if (e2 < dx) {
            err += dx
            y0 += sy
        }
    }
}