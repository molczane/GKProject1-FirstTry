package org.example.project

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.PointerMatcher
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

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

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CanvasToDrawView(
    modifier: Modifier,
    fieldColor: Color = Color.LightGray,
    edgesColor: Color = Color.White
) {
    var points by remember { mutableStateOf(emptyList<Offset>()) }
    var lines by remember { mutableStateOf(emptyList<LineSegment>()) }
    var draggingPointIndex by remember { mutableStateOf<Int?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isPolygonClosed by remember { mutableStateOf(false) }

    var showColorPicker by remember { mutableStateOf(false) }
    var selectedLineIndex by remember { mutableStateOf<Int?>(null) }
    var selectedColor by remember { mutableStateOf(Color.Red) }

    var showContextMenu by remember { mutableStateOf(false) }
    var clickPosition by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = modifier.fillMaxSize()
            .background(fieldColor)
            .onPointerEvent(PointerEventType.Press) {
                    pointerEvent ->
                // Sprawdź stan przycisków myszy w zdarzeniu
                val offset = pointerEvent.changes.firstOrNull()?.position
                if (offset != null) {

                    if (pointerEvent.buttons.isPrimaryPressed) {
                        println("Left mouse button clicked!")
                        if (!isPolygonClosed) {
                            if (points.isNotEmpty() && (offset - points.first()).getDistance() < 20.dp.toPx()) {
                                // If the new point is close to the first point, close the polygon
                                isPolygonClosed = true
                            } else {
                                var newLineSegment: LineSegment? = null
                                if (points.isNotEmpty()) {
                                    lines = lines + LineSegment(points.last(), offset)
                                    newLineSegment = LineSegment(points.last(), offset)
                                }
                                points = points + offset
                                println("New point added: $offset!")
                                println("New line added: $newLineSegment")
                            }
                        }
                    }
                    if (pointerEvent.buttons.isSecondaryPressed) {
                        println("Right mouse button clicked!")
                        clickPosition = pointerEvent.changes.first().position
                        showContextMenu = true
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
        if (showContextMenu) {
            Popup(
                offset = IntOffset(clickPosition.x.toInt(), clickPosition.y.toInt()),
                onDismissRequest = { showContextMenu = false },
                properties = PopupProperties(focusable = true)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .size(150.dp)
                ) {
                    Text("Option 1", modifier = Modifier.padding(8.dp))
                    Text("Option 2", modifier = Modifier.padding(8.dp))
                    Text("Option 3", modifier = Modifier.padding(8.dp))
                }
            }
        }
        }
}

fun distancePointToLineSegment(point: Offset, start: Offset, end: Offset): Float {
    val lineLengthSquared = (end.x - start.x) * (end.x - start.x) + (end.y - start.y) * (end.y - start.y)
    if (lineLengthSquared == 0f) return (point - start).getDistance()
    val t = ((point.x - start.x) * (end.x - start.x) + (point.y - start.y) * (end.y - start.y)) / lineLengthSquared
    return if (t < 0) {
        (point - start).getDistance()
    } else if (t > 1) {
        (point - end).getDistance()
    } else {
        val projection = Offset(start.x + t * (end.x - start.x), start.y + t * (end.y - start.y))
        (point - projection).getDistance()
    }
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedColor by remember { mutableStateOf(initialColor) }

    val controller = rememberColorPickerController()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Wybierz kolor") },
        text = {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    // do something
                }
            )
        },
        confirmButton = {
            Button(onClick = { onColorSelected(selectedColor) }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Anuluj")
            }
        }
    )
}
