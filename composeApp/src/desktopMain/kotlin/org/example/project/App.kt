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
import org.example.project.algorithms.distancePointToLineSegment
import org.example.project.algorithms.drawBresenhamLine
import org.example.project.algorithms.drawCubicBezier
import org.example.project.algorithms.drawDashedLine
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.drawRelation

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
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isPolygonClosed by remember { mutableStateOf(false) }

    var showContextMenu by remember { mutableStateOf(false) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }

    var draggingLineIndex by remember { mutableStateOf<Int?>(null) }
    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }
    var dragLineOffsetStart by remember { mutableStateOf(Offset.Zero) }
    var dragLineOffsetEnd by remember { mutableStateOf(Offset.Zero) }

    ContextMenuArea(items = {
        listOf(
            ContextMenuItem("Ustal bok na pionowy") {
                if(selectedLineIndex != null){
                    val index = selectedLineIndex!!
                    lines = lines.toMutableList().also {
                        it[index] = LineSegment(start=it[index].start, end=it[index].end, relation=Relations.Vertical)
                    }
                    println("Ustalono linię ${index} na pionową!")
                }
                selectedLineIndex = null
            },
            ContextMenuItem("Ustal bok na poziomy") {
                if(selectedLineIndex != null){
                    val index = selectedLineIndex!!
                    lines = lines.toMutableList().also {
                        it[index] = LineSegment(start=it[index].start, end=it[index].end, relation=Relations.Horizontal)
                    }
                    println("Ustalono linię ${index} na poziomą!")
                }
                selectedLineIndex = null
            },
            ContextMenuItem("Ustal bok na stała długość") {
                if(selectedLineIndex != null){
                    val index = selectedLineIndex!!
                    lines = lines.toMutableList().also {
                        it[index] = LineSegment(start=it[index].start, end=it[index].end, relation=Relations.FixedLength)
                    }
                    println("Ustalono linię ${index} na stałą długość!")
                }
                selectedLineIndex = null
            },
            ContextMenuItem("Zrób z boku krzywą Beziera 3-go stopnia") {
                if(selectedLineIndex != null){
                    val index = selectedLineIndex!!
                    lines = lines.toMutableList().also {
                        it[index] = LineSegment(start=it[index].start, end=it[index].end, relation=Relations.Bezier)
                    }
                    println("Ustalono linię ${index} na segment Beziera 3-go stopnia!")
                }
                selectedLineIndex = null
            },
            ContextMenuItem("Usuń ograniczenia") {
                if(selectedLineIndex != null){
                    val index = selectedLineIndex!!
                    lines = lines.toMutableList().also {
                        it[index] = LineSegment(start=it[index].start, end=it[index].end, relation=Relations.None)
                    }
                    println("Usunięto ograniczenia z linii ${index}!")
                }
                selectedLineIndex = null
            }
        )
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
                        selectedLineIndex = lines.indexOfFirst {
                            distancePointToLineSegment(
                                offset,
                                it.start,
                                it.end
                            ) < 20.dp.toPx()
                        }.takeIf { it != -1 }
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
                                if (draggingPointIndex == null) {
                                    draggingLineIndex = lines.indexOfFirst {
                                        distancePointToLineSegment(
                                            offset,
                                            it.start,
                                            it.end
                                        ) < 20.dp.toPx()
                                    }.takeIf { it != -1 }
                                    if (draggingLineIndex != null) {
                                        dragLineOffsetStart =
                                            offset - lines[draggingLineIndex!!].start
                                        dragLineOffsetEnd = offset - lines[draggingLineIndex!!].end
                                        val index = draggingLineIndex!!
                                        lines = lines.toMutableList().also {
                                            it[index] = LineSegment(
                                                it[index].start,
                                                it[index].end,
                                                Color.Blue,
                                                4F,
                                                it[index].relation
                                            )
                                        }
                                        println("Line selected: $draggingLineIndex")
                                    }
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
                            if(draggingLineIndex != null && draggingPointIndex == null) {
                                val index = draggingLineIndex!!
                                lines = lines.toMutableList().also {
                                    it[index] = LineSegment(change.position - dragLineOffsetStart, change.position - dragLineOffsetEnd, Color.Blue, 4F, it[index].relation)
                                    if(index == 0)
                                    {
                                        it[lines.size - 1] = LineSegment(it[lines.size - 1].start, change.position - dragLineOffsetStart, relation = it[lines.size - 1].relation)
                                    }
                                    else {
                                        it[(index - 1) % lines.size] = LineSegment(
                                            it[(index - 1) % lines.size].start,
                                            change.position - dragLineOffsetStart,
                                            relation = it[(index - 1) % lines.size].relation
                                        )
                                    }
                                    it[(index + 1) % lines.size] = LineSegment(change.position - dragLineOffsetEnd , it[(index + 1) % lines.size].end, relation = it[(index + 1) % lines.size].relation)
                                }
                                points = points.toMutableList().also {
                                    it[index] = change.position - dragLineOffsetStart
                                    it[(index + 1) % points.size] = change.position - dragLineOffsetEnd
                                }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            if(draggingLineIndex != null) {
                                val index = draggingLineIndex!!
                                lines = lines.toMutableList().also {
                                    it[index] = LineSegment(it[index].start, it[index].end, Color.Black, 2F, it[index].relation)
                                }
                            }
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
                        drawBresenhamLine(
                            color = lines[i].color,
                            start = lines[i].start,
                            end = lines[i].end,
                            width = lines[i].strokeWidth
                        )
                        drawRelation(lines[i])
                    }
                    if (isPolygonClosed) {
                        drawBresenhamLine(
                            color = Color.Green,
                            start = lines.last().start,
                            end = lines.last().end,
                            width = lines.last().strokeWidth
                        )
                        drawRelation(lines.last())
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


                /* TESTOWE RYSOWANIE NA CANVASIE WŁĄSNYMI ALGORYTMAMI */

                drawBresenhamLine(Offset(0.0F, 0.0F), Offset(200.0F, 200.0F), Color.Blue)

                // Definicja punktów kontrolnych dla krzywej kubicznej Beziera
                val start = Offset(50f, 300f)
                val control1 = Offset(150f, 50f)
                val control2 = Offset(250f, 500f)
                val end = Offset(350f, 300f)

                // Rysowanie przerywanego wieloboku kontrolnego
                drawDashedLine(start, control1, Color.Gray)
                drawDashedLine(control1, control2, Color.Gray)
                drawDashedLine(control2, end, Color.Gray)

                // Rysowanie krzywej kubicznej Beziera
                drawCubicBezier(start, control1, control2, end, Color.Black)
            }
        }
    }
}

