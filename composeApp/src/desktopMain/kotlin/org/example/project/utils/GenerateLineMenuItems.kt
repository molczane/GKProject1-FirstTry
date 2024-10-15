import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import org.example.project.algorithms.calculateCubicBezierControlPoints
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.midpoint

fun generateLineMenuItems(
    selectedLineIndex: Int?,
    lines: List<LineSegment>,
    points: List<Offset>,
    bezierSegments: List<CubicBezierSegment>,
    bezierControlPoints: List<Offset>,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPointsChange: (List<Offset>) -> Unit,
    onShowLengthWindowChange: (Boolean) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
    onBezierControlPointsChange: (List<Offset>) -> Unit,
    menuItems: MutableList<ContextMenuItem>
) {
    if (selectedLineIndex != null) {
        val index = selectedLineIndex
        val line = lines[index]

        // Dodaj punkt w środku linii
        menuItems.add(ContextMenuItem("Dodaj punkt w środku linii") {
            val midPoint = line.start.midpoint(line.end)
            val updatedPoints = points.toMutableList().also {
                it.add(index + 1, midPoint)
            }
            onPointsChange(updatedPoints)

            val updatedLines = lines.toMutableList().also {
                val lineCopy = it[index].copy()
                it[index] = LineSegment(
                    start = it[index].start,
                    end = midPoint,
                    relation = Relations.None
                )
                it.add(index + 1, LineSegment(midPoint, lineCopy.end))
            }
            onLinesChange(updatedLines)
        })

        // Ustal bok na pionowy
        if (line.relation != Relations.Vertical) {
            menuItems.add(
                ContextMenuItem("Ustal bok na pionowy") {
                    val updatedLines = lines.toMutableList().also {
                        it[index] = LineSegment(
                            start = it[index].start,
                            end = it[index].end,
                            relation = Relations.Vertical
                        )
                    }
                    onLinesChange(updatedLines)
                    println("Ustalono linię $index na pionową!")
                }
            )
        }

        // Ustal bok na poziomy
        if (line.relation != Relations.Horizontal) {
            menuItems.add(ContextMenuItem("Ustal bok na poziomy") {
                val updatedLines = lines.toMutableList().also {
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = it[index].end,
                        relation = Relations.Horizontal
                    )
                }
                onLinesChange(updatedLines)
                println("Ustalono linię $index na poziomą!")
            })
        }

        // Ustal bok na stała długość
        if (line.relation != Relations.FixedLength) {
            menuItems.add(ContextMenuItem("Ustal bok na stała długość") {
                val updatedLines = lines.toMutableList().also {
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = it[index].end,
                        relation = Relations.FixedLength
                    )
                }
                onLinesChange(updatedLines)
                onShowLengthWindowChange(true)
                println("Ustalono linię $index na stałą długość!")
            })
        }

        // Zrób z boku krzywą Beziera 3-go stopnia
        if (line.relation != Relations.Bezier) {
            menuItems.add(ContextMenuItem("Zrób z boku krzywą Beziera 3-go stopnia") {
                val updatedLines = lines.toMutableList().also {
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = it[index].end,
                        relation = Relations.Bezier,
                        bezierSegment = calculateCubicBezierControlPoints(
                            start = it[index].start,
                            end = it[index].end,
                            lineIndex = index
                        )
                    )
                }
                onLinesChange(updatedLines)
                println("Ustalono linię $index na segment Beziera 3-go stopnia!")
            })
        }

        // Usuń ograniczenia
        if (line.relation != Relations.None) {
            menuItems.add(ContextMenuItem("Usuń ograniczenia") {
                val updatedLines = lines.toMutableList().also {
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = it[index].end,
                        relation = Relations.None
                    )
                }
                onLinesChange(updatedLines)
                println("Usunięto ograniczenia z linii $index!")
            })
        }
    }
}
