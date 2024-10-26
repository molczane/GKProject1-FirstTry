package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import org.example.project.utils.LineSegment

fun calculateNewStartFixedLengthPoint(line: LineSegment, dragAmount: Offset): Offset {
    val start = line.start
    val end = line.end
    val length = (end - start).getDistance()
    val newEnd = end + dragAmount
    return newEnd + (start - newEnd).normalize() * length
}