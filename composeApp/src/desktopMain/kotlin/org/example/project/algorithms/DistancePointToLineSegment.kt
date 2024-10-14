package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset

fun distancePointToLineSegment(point: Offset, start: Offset, end: Offset): Float {
    val lineLengthSquared = (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y)
    if (lineLengthSquared == 0f) return (point - start).getDistance()
    val t = ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) / lineLengthSquared
    return if (t < 0) {
        (point - start).getDistance()
    } else if (t > 1) {
        (point - end).getDistance()
    } else {
        val projection = Offset(start.x + t * (end.x - start.x), start.y + t * (end.y - start.y))
        (point - projection).getDistance()
    }
}