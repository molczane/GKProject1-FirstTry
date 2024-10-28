package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.ceil

fun DrawScope.drawWuLine(start: Offset, end: Offset, color: Color, width: Float = 1f) {
    fun plot(x: Int, y: Int, c: Float) {
        drawCircle(
            color = color.copy(alpha = c),
            radius = width / 2,
            center = Offset(x.toFloat(), y.toFloat())
        )
    }

    val steep = abs(end.y - start.y) > abs(end.x - start.x)
    var x0 = start.x
    var y0 = start.y
    var x1 = end.x
    var y1 = end.y

    if (steep) {
        x0 = start.y
        y0 = start.x
        x1 = end.y
        y1 = end.x
    }
    if (x0 > x1) {
        val tempX = x0
        val tempY = y0
        x0 = x1
        y0 = y1
        x1 = tempX
        y1 = tempY
    }

    val dx = x1 - x0
    val dy = y1 - y0
    val gradient = if (dx == 0f) 1f else dy / dx

    // pierwsze końcowe punkty
    var xEnd = round(x0)
    var yEnd = y0 + gradient * (xEnd - x0)
    val xGap = 1 - frac(x0 + 0.5f)
    val xPixel1 = xEnd.toInt()
    val yPixel1 = floor(yEnd).toInt()

    if (steep) {
        plot(yPixel1, xPixel1, (1 - frac(yEnd)) * xGap)
        plot(yPixel1 + 1, xPixel1, frac(yEnd) * xGap)
    } else {
        plot(xPixel1, yPixel1, (1 - frac(yEnd)) * xGap)
        plot(xPixel1, yPixel1 + 1, frac(yEnd) * xGap)
    }

    var intery = yEnd + gradient

    // drugie końcowe punkty
    xEnd = round(x1)
    yEnd = y1 + gradient * (xEnd - x1)
    val xPixel2 = xEnd.toInt()
    val yPixel2 = floor(yEnd).toInt()
    val xGap2 = frac(x1 + 0.5f)

    if (steep) {
        plot(yPixel2, xPixel2, (1 - frac(yEnd)) * xGap2)
        plot(yPixel2 + 1, xPixel2, frac(yEnd) * xGap2)
    } else {
        plot(xPixel2, yPixel2, (1 - frac(yEnd)) * xGap2)
        plot(xPixel2, yPixel2 + 1, frac(yEnd) * xGap2)
    }

    // główna pętla rysowania
    if (steep) {
        for (x in (xPixel1 + 1) until xPixel2) {
            val y = floor(intery).toInt()
            plot(y, x, 1 - frac(intery))
            plot(y + 1, x, frac(intery))
            intery += gradient
        }
    } else {
        for (x in (xPixel1 + 1) until xPixel2) {
            val y = floor(intery).toInt()
            plot(x, y, 1 - frac(intery))
            plot(x, y + 1, frac(intery))
            intery += gradient
        }
    }
}

private fun frac(x: Float) = x - floor(x)

//package org.example.project.algorithms
//
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.drawscope.DrawScope
//import kotlin.math.abs
//import kotlin.math.floor
//import kotlin.math.round
//import kotlin.math.sqrt
//
//// Funkcja pomocnicza do rysowania piksela z określoną przezroczystością
//private fun DrawScope.plot(x: Float, y: Float, color: Color, alpha: Float) {
//    drawCircle(color.copy(alpha = alpha), radius = 1f, center = Offset(x, y))
//}
//
//fun DrawScope.drawWuLine(start: Offset, end: Offset, color: Color) {
//    val dx = end.x - start.x
//    val dy = end.y - start.y
//    val steep = abs(dy) > abs(dx)
//
//    // Zmiana współrzędnych dla linii nachylonej
//    val (x0, y0, x1, y1) = if (steep) {
//        arrayOf(start.y, start.x, end.y, end.x)
//    } else {
//        arrayOf(start.x, start.y, end.x, end.y)
//    }
//
//    val (xStart, yStart, xEnd, yEnd) = if (x0 > x1) {
//        arrayOf(x1, y1, x0, y0)
//    } else {
//        arrayOf(x0, y0, x1, y1)
//    }
//
//    val gradient = if (xEnd - xStart != 0f) dy / dx else 1f
//
//    // Punkt początkowy
//    var xpxl1 = floor(xStart + 0.5f).toInt()
//    var ypxl1 = yStart + gradient * (xpxl1 - xStart)
//    val xgap1 = 1 - (xStart + 0.5f - floor(xStart + 0.5f))
//
//    if (steep) {
//        plot(ypxl1.toFloat(), xpxl1.toFloat(), color, (1 - (ypxl1 - floor(ypxl1))) * xgap1)
//        plot(ypxl1.toFloat() + 1, xpxl1.toFloat(), color, (ypxl1 - floor(ypxl1)) * xgap1)
//    } else {
//        plot(xpxl1.toFloat(), ypxl1.toFloat(), color, (1 - (ypxl1 - floor(ypxl1))) * xgap1)
//        plot(xpxl1.toFloat(), ypxl1.toFloat() + 1, color, (ypxl1 - floor(ypxl1)) * xgap1)
//    }
//
//    // Punkt końcowy
//    val xpxl2 = floor(xEnd + 0.5f).toInt()
//    var ypxl2 = yEnd + gradient * (xpxl2 - xEnd)
//    val xgap2 = xEnd + 0.5f - floor(xEnd + 0.5f)
//
//    if (steep) {
//        plot(ypxl2.toFloat(), xpxl2.toFloat(), color, (1 - (ypxl2 - floor(ypxl2))) * xgap2)
//        plot(ypxl2.toFloat() + 1, xpxl2.toFloat(), color, (ypxl2 - floor(ypxl2)) * xgap2)
//    } else {
//        plot(xpxl2.toFloat(), ypxl2.toFloat(), color, (1 - (ypxl2 - floor(ypxl2))) * xgap2)
//        plot(xpxl2.toFloat(), ypxl2.toFloat() + 1, color, (ypxl2 - floor(ypxl2)) * xgap2)
//    }
//
//    // Rysowanie pikseli między początkowym a końcowym
//    for (x in (xpxl1 + 1) until xpxl2) {
//        val intery = yStart + gradient * (x - xStart)
//        if (steep) {
//            plot(floor(intery).toFloat(), x.toFloat(), color, 1 - (intery - floor(intery)))
//            plot(floor(intery + 1).toFloat(), x.toFloat(), color, intery - floor(intery))
//        } else {
//            plot(x.toFloat(), floor(intery).toFloat(), color, 1 - (intery - floor(intery)))
//            plot(x.toFloat(), floor(intery + 1).toFloat(), color, intery - floor(intery))
//        }
//    }
//}
