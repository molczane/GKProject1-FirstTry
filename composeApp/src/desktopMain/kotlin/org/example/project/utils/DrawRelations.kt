package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.toSize
import kotlin.math.abs

// Funkcja rysująca relację na danym segmencie linii
fun DrawScope.drawRelation(segment: LineSegment) {
    // Oblicz wymiary prostokąta wokół linii (z marginesem)
    val rectWidth = 26f
    val rectHeight = 26f

    when (segment.relation) {
        Relations.Horizontal -> {
            // Rysujemy poziomą kreskę symbolizującą ograniczenie
            val midPoint = segment.start.midpoint(segment.end)
            // Rysowanie szarego prostokąta
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize()
            )
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize(),
                style = Stroke(width = 4f)
            )
            drawLine(
                color = Color.Black,
                start = Offset(midPoint.x - 10, midPoint.y),
                end = Offset(midPoint.x + 10, midPoint.y),
                strokeWidth = 4F
            )
        }
        Relations.Vertical -> {
            // Rysujemy pionową kreskę symbolizującą ograniczenie
            val midPoint = segment.start.midpoint(segment.end)
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize()
            )
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize(),
                style = Stroke(width = 4f)
            )
            drawLine(
                color = Color.Black,
                start = Offset(midPoint.x, midPoint.y - 10),
                end = Offset(midPoint.x, midPoint.y + 10),
                strokeWidth = 4F
            )
        }
        Relations.FixedLength -> {
            // Rysujemy kółko symbolizujące zadaną długość
            val midPoint = segment.start.midpoint(segment.end)
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize()
            )
            drawRect(
                color = Color.LightGray, // Szary z lekką przezroczystością
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize(),
                style = Stroke(width = 4f)
            )
            drawCircle(
                color = Color.Black,
                radius = 10F,
                center = midPoint,
                style = Stroke(width = 2F)
            )
        }
        Relations.None -> {
            // Brak ograniczenia, nie rysujemy żadnego symbolu
        }

        Relations.Bezier -> {
            // Nie rysujemy żadnego symbolu
        }
    }
}

// Funkcja obliczająca punkt środkowy między dwoma punktami
fun Offset.midpoint(other: Offset): Offset {
    return Offset(
        (this.x + other.x) / 2,
        (this.y + other.y) / 2
    )
}