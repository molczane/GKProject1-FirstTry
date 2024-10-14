package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import org.example.project.algorithms.calculateCubicBezierControlPoints
import org.example.project.algorithms.distancePointToLineSegment
import org.example.project.algorithms.drawBresenhamLine
import org.example.project.algorithms.drawCubicBezier
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.drawRelation
import org.example.project.utils.midpoint

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CanvasToDrawView(
    modifier: Modifier,
    fieldColor: Color = Color.White
) {
    var points by remember { mutableStateOf(emptyList<Offset>()) }
    var lines by remember { mutableStateOf(emptyList<LineSegment>()) }

    var draggingPointIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isPolygonClosed by remember { mutableStateOf(false) }

    var showContextMenu by remember { mutableStateOf(false) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }

    var draggingLineIndex by remember { mutableStateOf<Int?>(null) }
    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }

    ContextMenuArea(items = {
        val menuItems = mutableListOf<ContextMenuItem>()
        if(selectedLineIndex == null && selectedPointIndex == null) {
            menuItems.add(ContextMenuItem("Nie wybrano żadnego elementu") { /*TODO*/ })
        }
        if(selectedPointIndex != null) {
            menuItems.add(
                ContextMenuItem("Usuń punkt") {
                    val index = selectedPointIndex!!
                    if(index != 0)
                    {
                        lines = lines.toMutableList().also {
                            it[index - 1] = LineSegment(it[index - 1].start, it[index].end)
                            it.removeAt(index)
                        }
                    }
                    else {
                        lines = lines.toMutableList().also {
                            it[lines.size - 1] = LineSegment(it[lines.size - 1].start, it[index].end)
                            it.removeAt(index)
                        }
                    }
                    points = points.toMutableList().also {
                        it.removeAt(index)
                    }
                    println("Usunięto punkt ${index}!")
                }
            )
        }
        if(selectedLineIndex != null) {
            val index = selectedLineIndex!!
            val line = lines[index]
            menuItems.add(ContextMenuItem("Dodaj punkt w środku linii") {
                val midPoint = line.start.midpoint(line.end)
                points = points.toMutableList().also {
                    it.add(index + 1, midPoint)
                }
                lines = lines.toMutableList().also {
                    val line = lines[index].copy()
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = midPoint,
                        relation = Relations.None
                    )
                    it.add(index + 1, LineSegment(midPoint, line.end))
                }
            })
            if(line.relation != Relations.Vertical) {
                menuItems.add(
                    ContextMenuItem("Ustal bok na pionowy") {
                            lines = lines.toMutableList().also {
                                it[index] = LineSegment(
                                    start = it[index].start,
                                    end = it[index].end,
                                    relation = Relations.Vertical
                                )
                            }
                            println("Ustalono linię ${index} na pionową!")
                        selectedLineIndex = null
                    }
                )
            }
            if(line.relation != Relations.Horizontal) {
                menuItems.add(ContextMenuItem("Ustal bok na poziomy") {
                        lines = lines.toMutableList().also {
                            it[index] = LineSegment(
                                start = it[index].start,
                                end = it[index].end,
                                relation = Relations.Horizontal
                            )
                        }
                        println("Ustalono linię ${index} na poziomą!")
                    selectedLineIndex = null
                }
                )
            }
            if(line.relation != Relations.FixedLength) {
                menuItems.add(ContextMenuItem("Ustal bok na stała długość") {
                        lines = lines.toMutableList().also {
                            it[index] = LineSegment(
                                start = it[index].start,
                                end = it[index].end,
                                relation = Relations.FixedLength
                            )
                        }
                        println("Ustalono linię ${index} na stałą długość!")
                    selectedLineIndex = null
                }
                )
            }
            if(line.relation != Relations.Bezier) {
                menuItems.add(ContextMenuItem("Zrób z boku krzywą Beziera 3-go stopnia") {
                        lines = lines.toMutableList().also {
                            it[index] = LineSegment(
                                start = it[index].start,
                                end = it[index].end,
                                relation = Relations.Bezier,
                                bezierSegment = calculateCubicBezierControlPoints(
                                    it[index].start,
                                    it[index].end
                                )
                            )
                        }
                        println("Ustalono linię ${index} na segment Beziera 3-go stopnia!")
                    selectedLineIndex = null
                }
                )
            }
            if(line.relation != Relations.None) {
                menuItems.add(ContextMenuItem("Usuń ograniczenia") {
                        lines = lines.toMutableList().also {
                            it[index] = LineSegment(
                                start = it[index].start,
                                end = it[index].end,
                                relation = Relations.None
                            )
                        }
                        println("Usunięto ograniczenia z linii ${index}!")
                    selectedLineIndex = null
                }
                )
            }
        }
        selectedLineIndex = null
        menuItems
    }) {
        Box(modifier = modifier.fillMaxSize()
            .background(fieldColor)
            .onPointerEvent(PointerEventType.Press) { pointerEvent ->
                // Sprawdź stan przycisków myszy w zdarzeniu
                val offset = pointerEvent.changes.firstOrNull()?.position
                if (offset != null) {
                    if (pointerEvent.buttons.isPrimaryPressed) {
                        println("Left mouse button clicked!")
                        if (!isPolygonClosed) {
                            draggingPointIndex = points.indexOfFirst {
                                (offset - it).getDistance() < 20.dp.toPx()
                            }.takeIf { it != -1 }
                            if (points.isNotEmpty() && (offset - points.first()).getDistance() < 20.dp.toPx()) {
                                // If the new point is close to the first point, close the polygon
                                lines = lines + LineSegment(points.last(), points.first())
                                isPolygonClosed = true
                            } else if (draggingPointIndex == null) {
                                var newLineSegment: LineSegment? = null
                                points = points + offset
                                if (points.size > 1) {
                                    lines = lines + LineSegment(points[points.size - 2], points.last())
                                    newLineSegment = LineSegment(points[points.size - 2], points.last())
                                }
                                println("New point added: $offset!")
                                println("New line added: $newLineSegment")
                            }
                        }
                    }
                    if (pointerEvent.buttons.isSecondaryPressed) {
                        println("Right mouse button clicked!")
                        clickPosition = pointerEvent.changes.first().position
                        selectedPointIndex = points.indexOfFirst {
                            (offset - it).getDistance() < 20.dp.toPx()
                        }.takeIf { it != -1 }
                        if(selectedPointIndex == null) {
                            selectedLineIndex = lines.indexOfFirst {
                                distancePointToLineSegment(
                                    offset,
                                    it.start,
                                    it.end
                                ) < 20.dp.toPx()
                            }.takeIf { it != -1 }
                        }
                        showContextMenu = true
                        println("Selected line index: $selectedLineIndex")
                    }
                }
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
                            if(!isPolygonClosed && draggingPointIndex == points.size - 1) {
                                draggingPointIndex = null
                            }
                            else {
                                println("Point selected: $draggingPointIndex")
                                if (draggingPointIndex != null) {
                                    dragOffset = offset - points[draggingPointIndex!!]
                                    draggingLineIndex = null
                                    println("Point selected: $draggingPointIndex")
                                }
                            }
                        },
                        onDrag = { change, _ ->
                            if (draggingPointIndex != null) {
                                val index = draggingPointIndex!!
                                points = points.toMutableList().also {
                                    it[index] = change.position - dragOffset
                                }
                                if(lines.isNotEmpty()) {
                                    if(index == 0) {
                                        if(isPolygonClosed) {
                                            lines = lines.toMutableList().also {
                                                it[lines.size - 1] =
                                                    LineSegment(it[lines.size - 1].start, points[index], relation = it[lines.size - 1].relation)
                                                it[index] = LineSegment(points[index], it[index].end, relation = it[index].relation)
                                            }
                                        }
                                    }
                                    else {
                                        lines = lines.toMutableList().also {
                                            it[index - 1] =
                                                LineSegment(it[index - 1].start, points[index], relation = it[index - 1].relation)
                                            it[index] = LineSegment(points[index], it[index].end, relation = it[index].relation)
                                        }
                                    }
                                }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            draggingPointIndex = null
                            draggingLineIndex = null
                            dragOffset = Offset.Zero
                        }
                    )
                }
            ) {
                // Draw lines between points
                if (lines.isNotEmpty()) {
                    for (i in 0 until lines.size) {
                        if(lines[i].relation != Relations.Bezier) {
                            drawBresenhamLine(
                                color = lines[i].color,
                                start = lines[i].start,
                                end = lines[i].end,
                                width = lines[i].strokeWidth
                            )
                            drawRelation(lines[i])
                        }
                        else {
                            val cubicBezierSegment = lines[i].bezierSegment!!
                            drawCubicBezier(cubicBezierSegment.start, cubicBezierSegment.control1, cubicBezierSegment.control2, cubicBezierSegment.end)
                        }
                    }
                    if (isPolygonClosed) {
                        if(lines.last().relation != Relations.Bezier) {
                            drawBresenhamLine(
                                color = Color.Green,
                                start = lines.last().start,
                                end = lines.last().end,
                                width = lines.last().strokeWidth
                            )
                            drawRelation(lines.last())
                        }
                        else {
                            val cubicBezierSegment = lines.last().bezierSegment!!
                            drawCubicBezier(cubicBezierSegment.start, cubicBezierSegment.control1, cubicBezierSegment.control2, cubicBezierSegment.end)
                        }
                    }
                }

                if(points.size == 1) {
                    // Draw points
                    points.forEach { point ->
                        drawCircle(
                            color = Color.Red,
                            center = point,
                            radius = 4.dp.toPx()
                        )
                    }
                }
                else {
                    lines.forEach { line ->
                        drawCircle(
                            color = Color.Red,
                            center = line.start,
                            radius = 4.dp.toPx()
                        )
                        drawCircle(
                            color = Color.Red,
                            center = line.end,
                            radius = 4.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

