package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset


fun calculateNewPointC1(
    start: Offset,
    end: Offset
) : Offset{
    val length = (end - start).getDistance()

    return end + (end - start).normalize() * (3*length)
}

fun calculateNewPointG1(
    start: Offset,
    end: Offset,
    oldPoint: Offset
) : Offset {
    val length = (oldPoint - end).getDistance()

    return end + (end - start).normalize() * length
}
