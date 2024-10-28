package org.example.project

import OffsetSerializer
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer

import generateLineMenuItems
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import moveAll

import org.example.project.algorithms.calculateEndPointFixedLength
import org.example.project.algorithms.correctToTheLeft
import org.example.project.algorithms.correctToTheRight
import org.example.project.algorithms.drawBresenhamLine
import org.example.project.algorithms.drawCubicBezier
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.drawRelation
import org.example.project.utils.generatePointMenuItems
import org.example.project.utils.handleClickEvents
import org.example.project.utils.removePointMenuItem
import java.io.File

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
    /* Reading serialized polygon */
    val file = File("points.json")
    val file2 = File("lines.json")
    val file3 = File("bezierSegments.json")
    val file4 = File("bezierControlPoints.json")

    val pointsJson = file.readText()
    val linesJson = file2.readText()
    val bezierSegmentsJson = file3.readText()
    val bezierControlPointsJson = file4.readText()

    var pointsRead = Json.decodeFromString(ListSerializer(OffsetSerializer), pointsJson)
    var linesRead = Json.decodeFromString<List<LineSegment>>(linesJson)
    var bezierSegmentsRead = Json.decodeFromString<List<CubicBezierSegment>>(bezierSegmentsJson)
    var bezierControlPointsRead = Json.decodeFromString<List<BezierControlPoint>>(bezierControlPointsJson)

    var points by remember { mutableStateOf(pointsRead) }
    var lines by remember { mutableStateOf(linesRead) }

    var draggingPointIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isPolygonClosed by remember { mutableStateOf(true) } // zaczynamy od true bo domyslnie Polygon jest zamknięty

    var showContextMenu by remember { mutableStateOf(false) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }

    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }

    var showLengthWindow by remember { mutableStateOf(false) }
    var showErrorWindow by remember { mutableStateOf(false) }
    var inputText by remember { mutableStateOf(TextFieldValue("600")) } // Przechowuje wartość tekstu
    var selectedLength by remember { mutableStateOf<Float?>(null) } // Zapisuje wybraną długość boku

    var bezierSegments by remember { mutableStateOf(bezierSegmentsRead) }
    var bezierControlPoints by remember { mutableStateOf(bezierControlPointsRead) }

    var draggingBezierControlPointIndex by remember { mutableStateOf<Int?>(null) }

    var fixedLengthLineIndex by remember { mutableStateOf<Int?>(null) }

    val resetCanvas: () -> Unit = {
        points = emptyList()
        lines = emptyList()
        bezierSegments = emptyList()
        bezierControlPoints = emptyList()
        isPolygonClosed = false
        selectedPointIndex = null
        selectedLineIndex = null
        draggingPointIndex = null
        draggingBezierControlPointIndex = null
        fixedLengthLineIndex = null
        showLengthWindow = false
        inputText = TextFieldValue("")
        selectedLength = null
        showContextMenu = false
        clickPosition = Offset.Zero
        dragOffset = Offset.Zero
    }

    ContextMenuArea(items = {
        val menuItems = mutableListOf<ContextMenuItem>()
        if(selectedLineIndex == null && selectedPointIndex == null) {
            menuItems.add(ContextMenuItem("Nie wybrano żadnego elementu") { /*DO NOTHING*/ })
        }
        if(selectedPointIndex != null) {
            menuItems.apply {
                // Dodaj menu usuwania punktu
                removePointMenuItem(
                    selectedPointIndex = selectedPointIndex,
                    lines = lines,
                    points = points,
                    updateLines = { lines = it },
                    updatePoints = { points = it },
                    menuItems = this
                )

                // Dodaj menu ustawienia ciągłości
                generatePointMenuItems(
                    selectedPointIndex = selectedPointIndex,
                    lines = lines,
                    updateLines = { lines = it },
                    menuItems = this
                )
            }
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
                fixedLengthLineIndex = fixedLengthLineIndex,
                showErrorWindow = showErrorWindow,
                onLinesChange = { lines = it },
                onPointsChange = { points = it },
                onShowLengthWindowChange = { showLengthWindow = it },
                onBezierSegmentsChange = { bezierSegments = it },
                onBezierControlPointsChange = { bezierControlPoints = it },
                onFixedLengthLineIndexChange = { fixedLengthLineIndex = it },
                onShowErrorWindow = { showErrorWindow = it },
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
            val textMeasurer = rememberTextMeasurer()

            // testowe wypisanie punktow kotrolnych beziera
            for (bezierControlPoint in bezierControlPoints) {
                println(bezierControlPoint)
            }

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
                                    (offset - it.offset).getDistance() < 20.dp.toPx()
                                }.takeIf { it != -1 }
                                if(draggingBezierControlPointIndex != null) {
                                    dragOffset = offset - bezierControlPoints[draggingBezierControlPointIndex!!].offset
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (draggingPointIndex != null) {
                                val index = draggingPointIndex!!
                                if(lines.isNotEmpty()) {
                                    correctToTheRight(
                                        index,
                                        dragAmount,
                                        lineSegmentsIn = lines,
                                        pointsListIn = points,
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
                                        lineSegmentsIn = lines,
                                        pointsListIn = points,
                                        bezierSegments = bezierSegments,
                                        bezierControlPoints = bezierControlPoints,
                                        onPointsChange = { points = it },
                                        onLinesChange = { lines = it },
                                        onBezierSegmentsChange = { bezierSegments = it },
                                        onBezierControlPointsChange = { bezierControlPoints = it }
                                    )
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
                            if(draggingPointIndex == null && draggingBezierControlPointIndex == null) // jesli zlapiemy ggdzielokwiek indziej przesuwamy wielokat
                            {
                                moveAll(
                                    dragAmount,
                                    points,
                                    bezierControlPoints,
                                    bezierSegments,
                                    lines,
                                    onPointsChange = { points = it },
                                    onLinesChange = { lines = it },
                                    onBezierSegmentsChange = { bezierSegments = it },
                                    onBezierControlPointsChange = { bezierControlPoints = it }
                                )
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
                            drawRelation(lines[i], textMeasurer)
                        }
                        else {
                            val cubicBezierSegment = lines[i].bezierSegment!!
                            drawCubicBezier(cubicBezierSegment.start, cubicBezierSegment.control1, cubicBezierSegment.control2, cubicBezierSegment.end)
                            drawRelation(lines[i], textMeasurer)
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
                            drawRelation(lines.last(), textMeasurer)
                        }
                        else {
                            val cubicBezierSegment = lines.last().bezierSegment!!
                            drawCubicBezier(cubicBezierSegment.start, cubicBezierSegment.control1, cubicBezierSegment.control2, cubicBezierSegment.end)
                            drawRelation(lines.last(), textMeasurer)
                        }
                    }
                }

                if(points.size == 1) {
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
                    bezierControlPoints.forEach { bezierControlPoint ->
                        drawCircle(
                            color = Color.Yellow,
                            center = bezierControlPoint.offset,
                            radius = 4.dp.toPx()
                        )
                    }
                }
            }
        }
    }


    // helper function to serialize polygon if needed
    val serializePolygon: () -> Unit = {
        val jsonPoints = Json.encodeToString(ListSerializer(OffsetSerializer), points)
        val file = File("points.json")
        file.writeText(jsonPoints)
        val jsonLines = Json.encodeToString(lines)
        val file2 = File("lines.json")
        file2.writeText(jsonLines)
        val jsonBezierSegments = Json.encodeToString(bezierSegments)
        val file3 = File("bezierSegments.json")
        file3.writeText(jsonBezierSegments)
        val jsonBezierControlPoints = Json.encodeToString(bezierControlPoints)
        val file4 = File("bezierControlPoints.json")
        file4.writeText(jsonBezierControlPoints)
    }

    Column(
        Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f, false)
        ) {
            //...
        }
        Button(
            onClick = resetCanvas,
            modifier = Modifier
                .padding(vertical = 2.dp)
        ) {
            Text("Clear Polygon")
        }

//        Button(
//            onClick = serializePolygon,
//            modifier = Modifier
//                .padding(vertical = 2.dp)
//        ) {
//            Text("Zserializuj wielokąt")
//        }
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
                            selectedLength = enteredValue// Zapisujemy wprowadzaną długość
                            val newEndPoint = calculateEndPointFixedLength(lines[fixedLengthLineIndex!!].start, lines[fixedLengthLineIndex!!].end, enteredValue)
                            val offset = newEndPoint - lines[fixedLengthLineIndex!!].end
                            lines = lines.toMutableList().also {
                                it[fixedLengthLineIndex!!] = LineSegment(start = lines[fixedLengthLineIndex!!].start, end = newEndPoint, relation = Relations.FixedLength)
                            }
                            correctToTheRight(
                                (fixedLengthLineIndex!! + 1)%lines.size,
                                offset,
                                lineSegmentsIn = lines,
                                pointsListIn = points,
                                bezierSegments = bezierSegments,
                                bezierControlPoints = bezierControlPoints,
                                onPointsChange = { points = it },
                                onLinesChange = { lines = it },
                                onBezierSegmentsChange = { bezierSegments = it },
                                onBezierControlPointsChange = { bezierControlPoints = it }
                            )
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

    if(showErrorWindow) {
        AlertDialog(
            modifier = Modifier
                .background(Color.White)
                .width(200.dp)
                .height(100.dp),
            onDismissRequest = { showLengthWindow = false }, // Zamknięcie dialogu
            title = { Text("Niedozwolona operacja") },
            confirmButton = {
                Column(
                    Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .weight(1f, false)
                    ) {
                        /* DO NOTHING */
                    }
                    Button(
                        onClick = {
                            showErrorWindow = false
                        }
                    ) {
                        Text("OK")
                    }
                }
            }
        )
    }
}