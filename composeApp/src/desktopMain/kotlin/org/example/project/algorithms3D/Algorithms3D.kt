package org.example.project.algorithms3D

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

// Funkcja rzutująca punkt 3D na współrzędne 2D przy użyciu rzutowania perspektywicznego
fun project3DTo2D(point: Triple<Float, Float, Float>, fov: Float, distance: Float, centerX: Float, centerY: Float): Offset {
    val (x, y, z) = point
    val factor = fov / (z + distance)
    val screenX = x * factor + centerX
    val screenY = -y * factor + centerY
    return Offset(screenX, screenY)
}

// Funkcja rysująca linię między dwoma punktami 2D
fun DrawScope.drawLine3D(start: Offset, end: Offset, color: Color) {
    drawLine(color = color, start = start, end = end, strokeWidth = 2f)
}