package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset

fun calculateEndPointFixedLength(
    start: Offset,
    end: Offset,
    lengthGiven: Float
) : Offset {
    return start + (end - start).normalize() * lengthGiven
}