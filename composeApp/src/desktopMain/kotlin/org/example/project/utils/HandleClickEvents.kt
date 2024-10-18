package org.example.project.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.unit.dp
import org.example.project.algorithms.distancePointToLineSegment

// Przeniesiona obsługa zdarzeń wskaźnika
fun handleClickEvents(
    pointerEvent: PointerEvent,
    points: List<Offset>,
    lines: List<LineSegment>,
    isPolygonClosed: Boolean,
    draggingPointIndex: Int?,
    selectedPointIndex: Int?,
    selectedLineIndex: Int?,
    showContextMenu: Boolean,
    clickPosition: Offset,
    onPointsChange: (List<Offset>) -> Unit,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPolygonClosedChange: (Boolean) -> Unit,
    onDraggingPointChange: (Int?) -> Unit,
    onSelectedPointChange: (Int?) -> Unit,
    onSelectedLineChange: (Int?) -> Unit,
    onShowContextMenuChange: (Boolean) -> Unit,
    onClickPositionChange: (Offset) -> Unit
) {
    val offset = pointerEvent.changes.firstOrNull()?.position

    if (offset != null) {
        if (pointerEvent.buttons.isPrimaryPressed) {
            println("Left mouse button clicked!")
            if (!isPolygonClosed) {
                val newDraggingPointIndex = points.indexOfFirst {
                    (offset - it).getDistance() < 20F
                }.takeIf { it != -1 }

                onDraggingPointChange(newDraggingPointIndex)

                if (points.isNotEmpty() && (offset - points.first()).getDistance() < 20F) {
                    // Zamknięcie wielokąta
                    onLinesChange(lines + LineSegment(points.last(), points.first()))
                    onPolygonClosedChange(true)
                } else if (newDraggingPointIndex == null) {
                    var newLineSegment: LineSegment? = null
                    val updatedPoints = points + offset
                    onPointsChange(updatedPoints)

                    if (updatedPoints.size > 1) {
                        val updatedLines = lines + LineSegment(updatedPoints[updatedPoints.size - 2], updatedPoints.last())
                        onLinesChange(updatedLines)
                        newLineSegment = LineSegment(updatedPoints[updatedPoints.size - 2], updatedPoints.last())
                    }
                    println("New point added: $offset!")
                    println("New line added: $newLineSegment")
                }
            }
        }

        if (pointerEvent.buttons.isSecondaryPressed) {
            println("Right mouse button clicked!")
            onClickPositionChange(pointerEvent.changes.first().position)

            val newSelectedPointIndex = points.indexOfFirst {
                (offset - it).getDistance() < 20F
            }.takeIf { it != -1 }

            onSelectedPointChange(newSelectedPointIndex)

            if (newSelectedPointIndex == null) {
                val newSelectedLineIndex = lines.indexOfFirst {
                    distancePointToLineSegment(offset, it.start, it.end) < 20F
                }.takeIf { it != -1 }

                onSelectedLineChange(newSelectedLineIndex)
            }

            onShowContextMenuChange(true)
            println("Selected line index: $selectedLineIndex")
        }
    }
}