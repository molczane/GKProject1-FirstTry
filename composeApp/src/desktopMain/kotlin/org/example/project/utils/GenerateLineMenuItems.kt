import androidx.compose.foundation.ContextMenuItem
import androidx.compose.ui.geometry.Offset
import org.example.project.algorithms.calculateCubicBezierSegment
import org.example.project.algorithms.correctToTheLeft
import org.example.project.algorithms.correctToTheRight
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations
import org.example.project.utils.midpoint

fun generateLineMenuItems(
    selectedLineIndex: Int?,
    lines: List<LineSegment>,
    points: List<Offset>,
    bezierSegments: List<CubicBezierSegment>,
    bezierControlPoints: List<BezierControlPoint>,
    fixedLengthLineIndex: Int?,
    showErrorWindow: Boolean,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPointsChange: (List<Offset>) -> Unit,
    onShowLengthWindowChange: (Boolean) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
    onBezierControlPointsChange: (List<BezierControlPoint>) -> Unit,
    onFixedLengthLineIndexChange: (Int?) -> Unit,
    onShowErrorWindow: (Boolean) -> Unit,
    menuItems: MutableList<ContextMenuItem>,
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
                    val previousIndex = if(index == 0) lines.size - 1 else index - 1
                    val nextIndex = (index + 1)%lines.size

                    if(
                        lines[previousIndex].relation == Relations.Vertical ||
                        lines[nextIndex].relation == Relations.Vertical
                    ) {
                        onShowErrorWindow(true)
                    }
                    else {
                        val offset = Offset(line.start.x - line.end.x, 0F)
                        val updatedLines = lines.toMutableList().also {
                            it[index] = LineSegment(
                                start = it[index].start,
                                end = Offset(it[index].start.x, it[index].end.y),
                                relation = Relations.Vertical
                            )
                        }
                        val updatedPoints = points.toMutableList().also {
                            it[(index + 1) % lines.size] = lines[index].end
                        }
                        onPointsChange(updatedPoints)
                        onLinesChange(updatedLines)
                        correctToTheRight(
                            index = (index + 1) % lines.size,
                            dragAmount = offset,
                            lineSegmentsIn = updatedLines,
                            pointsListIn = updatedPoints,
                            bezierSegments = bezierSegments,
                            bezierControlPoints = bezierControlPoints,
                            onLinesChange = onLinesChange,
                            onPointsChange = onPointsChange,
                            onBezierSegmentsChange = onBezierSegmentsChange,
                            onBezierControlPointsChange = onBezierControlPointsChange
                        )
                        println("Ustalono linię $index na pionową!")
                    }
                }
            )
        }

        // Ustal bok na poziomy
        if (line.relation != Relations.Horizontal) {
            menuItems.add(ContextMenuItem("Ustal bok na poziomy") {
                val previousIndex = if(index == 0) lines.size - 1 else index - 1
                val nextIndex = (index + 1)%lines.size

                if(
                    lines[previousIndex].relation == Relations.Horizontal ||
                    lines[nextIndex].relation == Relations.Horizontal
                ) {
                    onShowErrorWindow(true)
                }
                else {
                    val offset = Offset(0F, line.start.y - line.end.y)
                    val updatedLines = lines.toMutableList().also {
                        it[index] = LineSegment(
                            start = it[index].start,
                            end = it[index].end + offset,
                            relation = Relations.Horizontal
                        )
                    }
                    val updatedPoints = points.toMutableList().also {
                        it[(index + 1) % lines.size] = lines[index].end
                    }
                    onPointsChange(updatedPoints)
                    onLinesChange(updatedLines)
                    correctToTheRight(
                        index = (index + 1) % lines.size,
                        dragAmount = offset,
                        lineSegmentsIn = updatedLines,
                        pointsListIn = points,
                        bezierSegments = bezierSegments,
                        bezierControlPoints = bezierControlPoints,
                        onLinesChange = onLinesChange,
                        onPointsChange = onPointsChange,
                        onBezierSegmentsChange = onBezierSegmentsChange,
                        onBezierControlPointsChange = onBezierControlPointsChange
                    )
//                correctToTheLeft(
//                    if(index == 0) lines.size - 1 else index - 1,
//                    Offset(0F, 0F),
//                    lineSegmentsIn = updatedLines,
//                    pointsListIn = points,
//                    bezierSegments = bezierSegments,
//                    bezierControlPoints = bezierControlPoints,
//                    onLinesChange = onLinesChange,
//                    onPointsChange = onPointsChange,
//                    onBezierSegmentsChange = onBezierSegmentsChange,
//                    onBezierControlPointsChange = onBezierControlPointsChange
//                )
                    println("Ustalono linię $index na poziomą!")
                }
            })
        }

        // Ustal bok na stała długość
        if (line.relation != Relations.FixedLength) {
            menuItems.add(ContextMenuItem("Ustal bok na stała długość") {
                onFixedLengthLineIndexChange(index)
                onShowLengthWindowChange(true)
                println("Ustalono linię $index na stałą długość!")
            })
        }

        // Zrób z boku krzywą Beziera 3-go stopnia
        if (line.relation != Relations.Bezier) {
            menuItems.add(ContextMenuItem("Zrób z boku krzywą Beziera 3-go stopnia") {
                val bezierSegment = calculateCubicBezierSegment(start = line.start,
                    end = line.end,
                    lineIndex = index,
                    previousLineSegment = lines[if(index == 0) lines.size - 1 else index - 1],
                    nextLineSegment = lines [(index + 1)%lines.size]
                )
                val updatedLines = lines.toMutableList().also {
                    it[index] = LineSegment(
                        start = it[index].start,
                        end = it[index].end,
                        relation = Relations.Bezier,
                        bezierSegment = bezierSegment
                    )
                }
                val updatedBezierSegments = bezierSegments.toMutableList().also {
                    it.add(bezierSegment)
                }
                var bezierControlPoint1 = BezierControlPoint(
                    offset = bezierSegment.control1,
                    lineIndex = index,
                    index = 1
                )
                var bezierControlPoint2 = BezierControlPoint(
                    offset = bezierSegment.control2,
                    lineIndex = index,
                    index = 2
                )
                val updatedBezierControlPoints = bezierControlPoints.toMutableList().also {
                    it.add(bezierControlPoint1)
                    it.add(bezierControlPoint2)
                }
                onBezierControlPointsChange(updatedBezierControlPoints)
                onBezierSegmentsChange(updatedBezierSegments)
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
