package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations

fun correctToTheRight(
    index: Int,
    dragAmount: Offset,
    lineSegmentsIn: List<LineSegment>,
    pointsListIn: List<Offset>,
    bezierSegments: List<CubicBezierSegment>,
    bezierControlPoints: List<BezierControlPoint>,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPointsChange: (List<Offset>) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
    onBezierControlPointsChange: (List<BezierControlPoint>) -> Unit,
) {
    var currentOffset = dragAmount
    var indexCurrent = index

    var pointsList = pointsListIn.toMutableList()
    var lineSegments = lineSegmentsIn.toMutableList()

    while(true) {
        // Secondly, adjusting lines list
        when(lineSegments[indexCurrent].relation) {
            Relations.None -> {
                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                pointsList = updatedPoints

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        updatedPoints[indexCurrent],
                        it[indexCurrent].end,
                        relation = it[indexCurrent].relation
                    )
                }

                lineSegments = updatedLines

                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                break
            }
            Relations.Bezier -> {
                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                pointsList = updatedPoints
                onPointsChange(updatedPoints)

                val updatedLines = lineSegments.toMutableList().also {
                    val newControlPoint = calculateNewControlPointC1(it[if(indexCurrent == 0) lineSegments.size - 1 else indexCurrent - 1].start, pointsList[indexCurrent])
                    it[indexCurrent] = LineSegment( pointsList[indexCurrent], it[indexCurrent].end, relation = it[indexCurrent].relation,
                        bezierSegment = CubicBezierSegment(
                            updatedPoints[indexCurrent],
                            newControlPoint,
                            it[indexCurrent].bezierSegment!!.control2,
                            it[indexCurrent].bezierSegment!!.end,
                            indexCurrent
                        )
                    )
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                break
            }
            Relations.Vertical -> {
                val updatedPoints = pointsList.toMutableList().also {
                    // Przesuwaj punkt w pełni zgodnie z currentOffset, czyli zarówno x, jak i y
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                pointsList = updatedPoints
                onPointsChange(updatedPoints)

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        start = updatedPoints[indexCurrent],
                        end = it[indexCurrent].end + Offset(currentOffset.x, 0F),
                        relation = it[indexCurrent].relation
                    )
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                currentOffset = Offset(currentOffset.x, 0F)
                indexCurrent = (indexCurrent + 1)%lineSegments.size
            }
            Relations.Horizontal -> {
                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                pointsList = updatedPoints
                onPointsChange(updatedPoints)

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        pointsList[indexCurrent],
                        it[indexCurrent].end + Offset(0F, dragAmount.y),
                        relation = it[indexCurrent].relation
                    )
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                currentOffset = Offset(0F, dragAmount.y)
                indexCurrent = (indexCurrent + 1)%lineSegments.size
            }
            Relations.FixedLength -> {
                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                pointsList = updatedPoints
                onPointsChange(updatedPoints)

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        pointsList[indexCurrent],
                        it[indexCurrent].end + currentOffset,
                        relation = it[indexCurrent].relation
                    )
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                indexCurrent = (indexCurrent + 1)%lineSegments.size
            }
        }
    }
}