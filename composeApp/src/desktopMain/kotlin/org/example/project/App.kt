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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import generateLineMenuItems
import org.example.project.algorithms.calculateNewControlPointC1
import org.example.project.algorithms.correctToTheLeft
import org.example.project.algorithms.correctToTheRight
import org.example.project.algorithms.drawBresenhamLine
import org.example.project.algorithms.drawCubicBezier
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.drawRelation
import org.example.project.utils.handleClickEvents

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

    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }

    var showLengthWindow by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(TextFieldValue("")) } // Przechowuje wartość tekstu
    var selectedLength by remember { mutableStateOf<Float?>(null) } // Zapisuje wybraną długość boku

    var bezierSegments by remember { mutableStateOf(emptyList<CubicBezierSegment>()) }
    var bezierControlPoints by remember { mutableStateOf(emptyList<BezierControlPoint>()) }
    var draggingBezierControlPointIndex by remember { mutableStateOf<Int?>(null) }

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
            // Generowanie elementów menu
            generateLineMenuItems(
                selectedLineIndex = index,
                lines = lines,
                points = points,
                bezierSegments = bezierSegments,
                bezierControlPoints = bezierControlPoints,
                onLinesChange = { lines = it },
                onPointsChange = { points = it },
                onShowLengthWindowChange = { showLengthWindow = it },
                onBezierSegmentsChange = { bezierSegments = it },
                onBezierControlPointsChange = { bezierControlPoints = it },
                menuItems = menuItems
            )
        }
        selectedLineIndex = null
        menuItems
    }) {
        Box(modifier = modifier.fillMaxSize()
            .background(fieldColor)
            .onPointerEvent(PointerEventType.Press) { pointerEvent ->
                handleClickEvents(
                    pointerEvent,
                    points,
                    lines,
                    isPolygonClosed,
                    draggingPointIndex,
                    selectedPointIndex,
                    selectedLineIndex,
                    showContextMenu,
                    clickPosition,
                    onPointsChange = { points = it },
                    onLinesChange = { lines = it },
                    onPolygonClosedChange = { isPolygonClosed = it },
                    onDraggingPointChange = { draggingPointIndex = it },
                    onSelectedPointChange = { selectedPointIndex = it },
                    onSelectedLineChange = { selectedLineIndex = it },
                    onShowContextMenuChange = { showContextMenu = it },
                    onClickPositionChange = { clickPosition = it }
                )
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
                                    println("Point selected: $draggingPointIndex")
                                }
                            }
                            if(draggingPointIndex == null) {
                                draggingBezierControlPointIndex = bezierControlPoints.indexOfFirst {
                                    (offset - it.offset).getDistance() < 10.dp.toPx()
                                }.takeIf { it != -1 }
                                if(draggingBezierControlPointIndex != null) {
                                    dragOffset = offset - bezierControlPoints[draggingBezierControlPointIndex!!].offset
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (draggingPointIndex != null) {
                                val index = draggingPointIndex!!
                                //(points as MutableList<Offset>)[index] = change.position - dragOffset
                                //(points as MutableList<Offset>)[index] = points[index] + dragAmount
                                if(lines.isNotEmpty()) {
                                    correctToTheRight(
                                        index,
                                        dragAmount,
                                        lines = lines,
                                        points = points,
                                        bezierSegments = bezierSegments,
                                        bezierControlPoints = bezierControlPoints,
                                        onPointsChange = { points = it },
                                        onLinesChange = { lines = it },
                                        onBezierSegmentsChange = { bezierSegments = it },
                                        onBezierControlPointsChange = { bezierControlPoints = it }
                                    )
                                    correctToTheLeft(
                                        if(index == 0) lines.size - 1 else index - 1,
                                        dragAmount,
                                        lines = lines,
                                        points = points,
                                        bezierSegments = bezierSegments,
                                        bezierControlPoints = bezierControlPoints,
                                        onPointsChange = { points = it },
                                        onLinesChange = { lines = it },
                                        onBezierSegmentsChange = { bezierSegments = it },
                                        onBezierControlPointsChange = { bezierControlPoints = it }
                                    )
//                                    if(index == 0) {
//                                        if(isPolygonClosed) {
//                                            lines = lines.toMutableList().also {
//                                                it[lines.size - 1] =
//                                                    LineSegment(it[lines.size - 1].start, points[index], relation = it[lines.size - 1].relation)
//                                                it[index] = LineSegment(points[index], it[index].end, relation = it[index].relation)
//                                            }
//                                        }
//                                    }
//                                    else {
//                                        lines = lines.toMutableList().also {
//                                            if(it[index - 1].relation == Relations.Bezier) {
//                                                val newControlPoint = calculateNewControlPointC1(it[index].end, points[index])
//                                                it[index - 1] = LineSegment(it[index - 1].start, points[index], relation = it[index - 1].relation,
//                                                    bezierSegment = CubicBezierSegment(
//                                                        it[index - 1].bezierSegment!!.start,
//                                                        it[index - 1].bezierSegment!!.control1,
//                                                        newControlPoint,
//                                                        points[index],
//                                                        index - 1
//                                                    )
//                                                )
//                                                it[index] = LineSegment(
//                                                    points[index],
//                                                    it[index].end,
//                                                    relation = it[index].relation
//                                                )
//                                            }
//                                            else if(it[index].relation == Relations.Bezier) {
//                                                val newControlPoint = calculateNewControlPointC1(it[index - 1].start, points[index])
//                                                it[index - 1] =
//                                                    LineSegment(
//                                                        it[index - 1].start,
//                                                        points[index],
//                                                        relation = it[index - 1].relation
//                                                    )
//                                                it[index] = LineSegment( points[index], it[index].end, relation = it[index].relation,
//                                                    bezierSegment = CubicBezierSegment(
//                                                        points[index],
//                                                        newControlPoint,
//                                                        it[index].bezierSegment!!.control2,
//                                                        it[index].bezierSegment!!.end,
//                                                        index
//                                                    )
//                                                )
//                                            }
//                                            else {
//                                                it[index - 1] =
//                                                    LineSegment(
//                                                        it[index - 1].start,
//                                                        points[index],
//                                                        relation = it[index - 1].relation
//                                                    )
//                                                it[index] = LineSegment(
//                                                    points[index],
//                                                    it[index].end,
//                                                    relation = it[index].relation
//                                                )
//                                            }
//                                        }
//                                    }
                                }
                            }
                            if(draggingBezierControlPointIndex != null) {
                                val index = draggingBezierControlPointIndex!!
                                val lineIndex = bezierControlPoints[index].lineIndex
                                val line = lines[lineIndex]
                                val oldBezierSegment = line.bezierSegment!!
                                bezierControlPoints[index].offset = change.position - dragOffset
                                if(bezierControlPoints[index].index == 1) {
                                    lines = lines.toMutableList().also {
                                        it[lineIndex] = LineSegment(start = line.start, end = line.end, relation = line.relation,
                                            bezierSegment = CubicBezierSegment(oldBezierSegment.start, bezierControlPoints[index].offset, oldBezierSegment.control2, oldBezierSegment.end, lineIndex))
                                    }
                                }
                                else {
                                    lines = lines.toMutableList().also {
                                        it[lineIndex] = LineSegment(start = line.start, end = line.end, relation = line.relation,
                                            bezierSegment = CubicBezierSegment(oldBezierSegment.start, oldBezierSegment.control1, bezierControlPoints[index].offset, oldBezierSegment.end, lineIndex))
                                    }
                                }
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            draggingPointIndex = null
                            draggingBezierControlPointIndex = null
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

                if(points.size != 0) {
                    // Draw points
                    points.forEach { point ->
                        drawCircle(
                            color = Color.DarkGray,
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

    if(showLengthWindow) {
        AlertDialog(
            modifier = Modifier
                .background(Color.White)
                .width(300.dp)
                .height(200.dp),
            onDismissRequest = { showLengthWindow = false }, // Zamknięcie dialogu
            title = { Text("Ustal długość boku") },
            text = {
                Column {
                    Text("Wprowadź długość:")
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        label = { Text("Długość") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val enteredValue = inputText.text.toFloatOrNull()
                        if (enteredValue != null) {
                            selectedLength = enteredValue // Zapisujemy wprowadzaną długość
                            showLengthWindow = false // Zamykamy dialog
                        } else {
                            // Obsługa błędu, np. wartość niepoprawna
                            println("Wprowadź prawidłową liczbę!")
                        }
                    }
                ) {
                    Text("Zatwierdź")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showLengthWindow = false // Zamknięcie dialogu bez zapisania wartości
                    }
                ) {
                    Text("Anuluj")
                }
            }
        )
    }
}