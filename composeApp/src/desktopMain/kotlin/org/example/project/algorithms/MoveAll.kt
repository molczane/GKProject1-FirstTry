import androidx.compose.ui.geometry.Offset
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment

fun moveAll(
    dragAmount: Offset,
    points: List<Offset>,
    bezierControlPoints: List<BezierControlPoint>,
    bezierSegments: List<CubicBezierSegment>,
    lines: List<LineSegment>,
    onPointsChange: (List<Offset>) -> Unit,
    onBezierControlPointsChange: (List<BezierControlPoint>) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
    onLinesChange: (List<LineSegment>) -> Unit
) {

    val updatedPoints = points.toMutableList().also {
        for (i in it.indices) {
            it[i] += dragAmount
        }
    }

    onPointsChange(updatedPoints)

    val updatedBezierControlPoints = bezierControlPoints.toMutableList().also {
        for (i in it.indices) {
            it[i] = it[i].copy(offset = it[i].offset + dragAmount)
        }
    }

    onBezierControlPointsChange(updatedBezierControlPoints)

    val updatedBezierSegments = bezierSegments.toMutableList().also {
        for (i in it.indices) {
            it[i] = it[i].copy(
                start = it[i].start + dragAmount,
                control1 = it[i].control1 + dragAmount,
                control2 = it[i].control2 + dragAmount,
                end = it[i].end + dragAmount
            )
        }
    }

    onBezierSegmentsChange(updatedBezierSegments)

    val updatedLines = lines.toMutableList().also { lineList ->
        for (i in lineList.indices) {
            lineList[i] = lineList[i].copy(
                start = lineList[i].start + dragAmount,
                end = lineList[i].end + dragAmount,
                bezierSegment = lineList[i].bezierSegment?.copy(
                    start = lineList[i].bezierSegment!!.start + dragAmount,
                    control1 = lineList[i].bezierSegment!!.control1 + dragAmount,
                    control2 = lineList[i].bezierSegment!!.control2 + dragAmount,
                    end = lineList[i].bezierSegment!!.end + dragAmount
                ),
                color = lineList[i].color,
                strokeWidth = lineList[i].strokeWidth,
                relation = lineList[i].relation
            )
        }
    }

    onLinesChange(updatedLines)
}