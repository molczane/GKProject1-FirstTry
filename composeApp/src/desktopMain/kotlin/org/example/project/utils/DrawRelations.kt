package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import kotlin.math.abs

// Funkcja rysująca relację na danym segmencie linii
fun DrawScope.drawRelation(segment: LineSegment, textMeasurer: TextMeasurer) {
    // Oblicz wymiary prostokąta wokół linii (z marginesem)
    val rectWidth = 26f
    val rectHeight = 26f

    when (segment.relation) {
        Relations.Horizontal -> {
            // Rysujemy poziomą kreskę symbolizującą ograniczenie
            val midPoint = segment.start.midpoint(segment.end)
            // Rysowanie szarego prostokąta
            drawRect(
                color = Color.LightGray, // Szary
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize()
            )
            drawRect(
                color = Color.LightGray, // Szary
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
                color = Color.LightGray, // Szary
                topLeft = Offset(midPoint.x - rectWidth / 2, midPoint.y - rectHeight / 2),
                size = IntSize(rectWidth.toInt(), rectHeight.toInt()).toSize()
            )
            drawRect(
                color = Color.LightGray, // Szary
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

            // Step 1: Resolve the default FontFamil
            // Step 2: Define text and style
            val text = "${abs((segment.start - segment.end).getDistance())}"
            val textStyle = TextStyle(
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                shadow = Shadow(Color.Gray, Offset(2f, 2f), 1f),
                textDecoration = TextDecoration.Underline
            )


            val textLayoutResult: TextLayoutResult = textMeasurer.measure(
                text = text,
                style = textStyle
            )

            drawRect(
                color = Color.LightGray, // Szary
                topLeft = Offset(midPoint.x - textLayoutResult.size.width / 2, midPoint.y - textLayoutResult.size.height / 2),
                size = IntSize( textLayoutResult.size.width, textLayoutResult.size.height).toSize()
            )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(midPoint.x - textLayoutResult.size.width / 2, midPoint.y - textLayoutResult.size.height / 2),  // Define the position
                color = Color.Blue,  // Text color
                alpha = 1f,  // Fully opaque
                shadow = Shadow(Color.Gray, Offset(4f, 4f), 4f),  // Optional shadow
                textDecoration = TextDecoration.None  // No decoration
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