package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

@Composable
@Preview
fun App() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CanvasToDrawView(Modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CanvasToDrawView(
    modifier: Modifier,
    fieldColor: Color = Color.LightGray,
    edgesColor: Color = Color.White
) {
    var points by remember { mutableStateOf(emptyList<Offset>()) }
    var draggingPointIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isPolygonClosed by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()
        .background(fieldColor)
        .pointerInput(Unit) {
            detectTapGestures (
                onTap = { offset ->
                    if (!isPolygonClosed) {
                        if (points.isNotEmpty() && (offset - points.first()).getDistance() < 20.dp.toPx()) {
                            // If the new point is close to the first point, close the polygon
                            isPolygonClosed = true
                        } else {
                            points = points + offset
                        }
                    }
            })
        }
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        draggingPointIndex = points.indexOfFirst {
                            (offset - it).getDistance() < 20.dp.toPx()
                        }.takeIf { it != -1 }
                        if (draggingPointIndex != null) {
                            dragOffset = offset - points[draggingPointIndex!!]
                        }
                    },
                    onDrag = { change, dragAmount ->
                        if (draggingPointIndex != null) {
                            val index = draggingPointIndex!!
                            points = points.toMutableList().also {
                                it[index] = change.position - dragOffset
                            }
                        }
                        change.consume()
                    },
                    onDragEnd = {
                        draggingPointIndex = null
                        dragOffset = Offset.Zero
                    }
                )
            }
        ) {
            // Draw lines between points
            if (points.size > 1) {
                for (i in 0 until points.size - 1) {
                    drawLine(
                        color = Color.Black,
                        start = points[i],
                        end = points[i + 1],
                        strokeWidth = 2.dp.toPx()
                    )
                }
                if (isPolygonClosed) {
                    // Draw the closing line if the polygon is closed
                    drawLine(
                        color = Color.Green,
                        start = points.last(),
                        end = points.first(),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }

            // Draw points
            points.forEach { point ->
                drawCircle(
                    color = Color.Red,
                    center = point,
                    radius = 4.dp.toPx()
                )
            }
        }
    }
}
