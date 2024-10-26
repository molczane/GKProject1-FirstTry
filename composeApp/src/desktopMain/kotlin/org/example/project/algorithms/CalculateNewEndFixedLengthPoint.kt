package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import org.example.project.utils.LineSegment

fun calculateNewEndFixedLengthPoint(line: LineSegment, dragAmount: Offset): Offset {
    val start = line.start
    val end = line.end
    val length = (end - start).getDistance()
    val newStart = start + dragAmount
    return newStart + (end - newStart).normalize() * length
}