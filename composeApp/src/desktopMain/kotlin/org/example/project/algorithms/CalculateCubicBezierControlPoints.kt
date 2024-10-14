package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import org.example.project.utils.CubicBezierSegment
import kotlin.math.sqrt

fun calculateCubicBezierControlPoints(
    start: Offset,
    end: Offset,
    offsetFactor: Float = 0.3f,
    perpendicularOffset: Float = 200f
): CubicBezierSegment {
    // 1. Calculate the direction vector from start to end
    val dx = end.x - start.x
    val dy = end.y - start.y

    // 2. Calculate the length of the direction vector
    val length = sqrt(dx * dx + dy * dy)

    if (length == 0f) {
        // If the start and end points are the same, return the same points for all
        return CubicBezierSegment(start, start, start, end)
    }

    // 3. Compute a perpendicular vector
    val perpendicular = Offset(-dy / length, dx / length)

    // 4. Define the offset magnitude for control points
    val controlOffset = perpendicularOffset

    // 5. Calculate the control points
    val control1 = Offset(
        start.x + dx * offsetFactor + perpendicular.x * controlOffset,
        start.y + dy * offsetFactor + perpendicular.y * controlOffset
    )

    val control2 = Offset(
        end.x - dx * offsetFactor - perpendicular.x * controlOffset,
        end.y - dy * offsetFactor - perpendicular.y * controlOffset
    )

    return CubicBezierSegment(start, control1, control2, end)
}