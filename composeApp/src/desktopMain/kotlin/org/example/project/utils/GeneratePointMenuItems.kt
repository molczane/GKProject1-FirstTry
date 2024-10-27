package org.example.project.utils

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.ui.geometry.Offset

fun generatePointMenuItems(
    selectedPointIndex: Int?,
    lines: List<LineSegment>,
    updateLines: (List<LineSegment>) -> Unit,
    menuItems: MutableList<ContextMenuItem>
) {
    val prevIndex = if (selectedPointIndex == 0) lines.size - 1 else selectedPointIndex!! - 1
    val currentLine = lines[selectedPointIndex!!]
    val prevLine = lines[prevIndex]

    if (prevLine.relation == Relations.Bezier && currentLine.relation == Relations.Bezier) {
        // Dodaj opcje dla ciągłości C1, G1, G0
        listOf(
            "Ustaw Ciągłość C1" to ContinuityClass.C1,
            "Ustaw Ciągłość G1" to ContinuityClass.G1,
            "Ustaw Ciągłość G0" to ContinuityClass.G0
        ).forEach { (label, continuityClass) ->
            menuItems.add(ContextMenuItem(label) {
                updateLines(
                    lines.toMutableList().apply {
                        this[prevIndex] = prevLine.copy(bezierSegment = prevLine.bezierSegment!!.copy(endPointContinuityClass = continuityClass))
                        this[selectedPointIndex] = currentLine.copy(bezierSegment = currentLine.bezierSegment!!.copy(startPointContinuityClass = continuityClass))
                    }
                )
            })
        }
    } else if (currentLine.relation == Relations.Bezier) {
        // Dodaj opcje dla aktualnej linii, jeśli ma ona tylko segment Bezier
        listOf(
            "Ustaw Ciągłość C1" to ContinuityClass.C1,
            "Ustaw Ciągłość G1" to ContinuityClass.G1,
            "Ustaw Ciągłość G0" to ContinuityClass.G0
        ).forEach { (label, continuityClass) ->
            menuItems.add(ContextMenuItem(label) {
                updateLines(
                    lines.toMutableList().apply {
                        this[selectedPointIndex] = currentLine.copy(bezierSegment = currentLine.bezierSegment!!.copy(startPointContinuityClass = continuityClass))
                    }
                )
            })
        }
    } else if (prevLine.relation == Relations.Bezier) {
        // Dodaj opcje dla poprzedniego segmentu, jeśli ma segment Bezier
        listOf(
            "Ustaw Ciągłość C1" to ContinuityClass.C1,
            "Ustaw Ciągłość G1" to ContinuityClass.G1,
            "Ustaw Ciągłość G0" to ContinuityClass.G0
        ).forEach { (label, continuityClass) ->
            menuItems.add(ContextMenuItem(label) {
                updateLines(
                    lines.toMutableList().apply {
                        this[prevIndex] = prevLine.copy(bezierSegment = prevLine.bezierSegment!!.copy(endPointContinuityClass = continuityClass))
                    }
                )
            })
        }
    }
}

fun removePointMenuItem(
    selectedPointIndex: Int?,
    lines: List<LineSegment>,
    points: List<Offset>,
    updateLines: (List<LineSegment>) -> Unit,
    updatePoints: (List<Offset>) -> Unit,
    menuItems: MutableList<ContextMenuItem>
) {
    menuItems.add(ContextMenuItem("Usuń punkt") {
        val index = selectedPointIndex!!
        val updatedLines = lines.toMutableList().apply {
            if (index != 0) {
                this[index - 1] = LineSegment(this[index - 1].start, this[index].end)
                removeAt(index)
            } else {
                this[lines.size - 1] = LineSegment(this[lines.size - 1].start, this[index].end)
                removeAt(index)
            }
        }
        val updatedPoints = points.toMutableList().apply { removeAt(index) }

        // Aktualizacja punktów i linii
        updateLines(updatedLines)
        updatePoints(updatedPoints)

        println("Usunięto punkt $index!")
    })
}
