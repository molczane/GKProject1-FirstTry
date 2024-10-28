package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import kotlin.math.sqrt

fun calculateCubicBezierSegment(
    start: Offset,
    end: Offset,
    offsetFactor: Float = 0.3f,
    perpendicularOffset: Float = 200f,
    lineIndex: Int,
    previousLineSegment: LineSegment,
    nextLineSegment: LineSegment
): CubicBezierSegment {
    val previousDirection = previousLineSegment.end.let { (it - previousLineSegment.start).normalize() }
    val nextDirection = nextLineSegment.start.let { (it - nextLineSegment.end).normalize() }

    val previousLineLength = (previousLineSegment.start - previousLineSegment.end).getDistance()
    val nextLineLength = (nextLineSegment.start - nextLineSegment.end).getDistance()

    // Ustawienie pierwszego punktu kontrolnego zgodnie z kierunkiem poprzedniej krawędzi
    val control1 = if (previousDirection != null) {
        start + previousDirection * previousLineLength/3F  // Można dostosować długość
    } else {
        start + (end - start).normalize() * previousLineLength/3F
    }

    // Ustawienie drugiego punktu kontrolnego zgodnie z kierunkiem następnej krawędzi
    val control2 = if (nextDirection != null) {
        end + nextDirection * nextLineLength/3F  // Można dostosować długość
    } else {
        end + (end - start).normalize() * nextLineLength/3F
    }

    return CubicBezierSegment(start, control1, control2, end, lineIndex)
}

fun calculateNewControlPointC1(
    start: Offset,
    end: Offset
) : Offset{
    val direction = (end - start).normalize()
    val length = (end - start).getDistance()

    return end + (end - start).normalize() * (length/3)
}

fun calculateNewControlPointG1(
    start: Offset,
    end: Offset,
    controlPoint: Offset
) : Offset{
    val length = (controlPoint - end).getDistance()

    return end + (end - start).normalize() * length
}

// Funkcja do normalizacji wektora (Offset)
fun Offset.normalize(): Offset {
    // Oblicz długość wektora
    val length = sqrt(x * x + y * y)

    // Sprawdzamy, czy długość jest większa niż zero, aby uniknąć dzielenia przez zero
    return if (length > 0f) {
        Offset(x / length, y / length)
    } else {
        Offset.Zero // Zwracamy wektor zerowy, jeśli długość wynosi 0
    }
}