package org.example.project.algorithms

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.ContinuityClass
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations

fun correctToTheLeft(
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
                var updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        pointsList[indexCurrent],
                        pointsList[(indexCurrent + 1)%lineSegments.size],
                        relation = it[indexCurrent].relation
                    )
                }

                val previousIndex = if(indexCurrent == 0) lineSegments.size - 1 else indexCurrent - 1
                if(lineSegments[previousIndex].relation == Relations.Bezier) {
                    val draggingBezierControlPointIndex = bezierControlPoints.indexOfFirst {
                        (lineSegments[previousIndex].bezierSegment!!.control2 - it.offset).getDistance() < 20F
                    }.takeIf { it != -1 }
                    when(lineSegments[previousIndex].bezierSegment!!.endPointContinuityClass)
                    {
                        ContinuityClass.C1 -> {
                            updatedLines = updatedLines.also {
                                val newControlPoint = calculateNewControlPointC1(
                                    it[indexCurrent].end,
                                    it[indexCurrent].start
                                )
                                it[previousIndex] = LineSegment(
                                    it[previousIndex].start,
                                    it[previousIndex].end,
                                    relation = it[previousIndex].relation,
                                    bezierSegment = CubicBezierSegment(
                                        it[previousIndex].start,
                                        it[previousIndex].bezierSegment!!.control1,
                                        newControlPoint,
                                        it[previousIndex].bezierSegment!!.end,
                                        previousIndex,
                                        it[previousIndex].bezierSegment!!.startPointContinuityClass,
                                        it[previousIndex].bezierSegment!!.endPointContinuityClass
                                    )
                                )
                                val updatedBezierControlPoints = bezierControlPoints.toMutableList().apply {
                                    this[draggingBezierControlPointIndex!!] = this[draggingBezierControlPointIndex!!].copy(
                                        offset = newControlPoint
                                    )
                                }
                                onBezierControlPointsChange(updatedBezierControlPoints)
                            }
                        }
                        ContinuityClass.G1 -> {
                            updatedLines = updatedLines.also {
                                val newControlPoint = calculateNewControlPointG1(
                                    it[indexCurrent].end,
                                    it[indexCurrent].start,
                                    it[previousIndex].bezierSegment!!.control2
                                )
                                it[previousIndex] = LineSegment(
                                    it[previousIndex].start,
                                    it[previousIndex].end,
                                    relation = it[previousIndex].relation,
                                    bezierSegment = CubicBezierSegment(
                                        it[previousIndex].start,
                                        it[previousIndex].bezierSegment!!.control1,
                                        newControlPoint,
                                        it[previousIndex].bezierSegment!!.end,
                                        previousIndex,
                                        it[previousIndex].bezierSegment!!.startPointContinuityClass,
                                        it[previousIndex].bezierSegment!!.endPointContinuityClass
                                    )
                                )
                                val updatedBezierControlPoints = bezierControlPoints.toMutableList().apply {
                                    this[draggingBezierControlPointIndex!!] = this[draggingBezierControlPointIndex!!].copy(
                                        offset = newControlPoint
                                    )
                                }
                                onBezierControlPointsChange(updatedBezierControlPoints)
                            }
                        }
                        ContinuityClass.G0 -> {
                            /* DO NOTHING */
                        }
                    }
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                // not updating points here
                break
            }
            Relations.Bezier -> {
                var updatedLines : MutableList<LineSegment> = emptyList<LineSegment>().toMutableList()

                val draggingBezierControlPointIndex = bezierControlPoints.indexOfFirst {
                    (lineSegments[indexCurrent].bezierSegment!!.control2 - it.offset).getDistance() < 20F
                }.takeIf { it != -1 }
                when(lineSegments[indexCurrent].bezierSegment!!.endPointContinuityClass)
                {

                    ContinuityClass.C1 -> {
                        updatedLines = lineSegments.toMutableList().also {
                            val newControlPoint = calculateNewControlPointC1(it[(indexCurrent + 1)%lineSegments.size].end, pointsList[(indexCurrent + 1)%lineSegments.size])
                            it[indexCurrent] = LineSegment( pointsList[indexCurrent], pointsList[(indexCurrent + 1)%lineSegments.size], relation = it[indexCurrent].relation,
                                bezierSegment = CubicBezierSegment(
                                    pointsList[indexCurrent],
                                    it[indexCurrent].bezierSegment!!.control1,
                                    newControlPoint,
                                    pointsList[(indexCurrent + 1)%lineSegments.size],
                                    indexCurrent,
                                    it[indexCurrent].bezierSegment!!.startPointContinuityClass,
                                    it[indexCurrent].bezierSegment!!.endPointContinuityClass
                                )
                            )
                            val updatedBezierControlPoints = bezierControlPoints.toMutableList().apply {
                                this[draggingBezierControlPointIndex!!] = this[draggingBezierControlPointIndex!!].copy(
                                    offset = newControlPoint
                                )
                            }
                            onBezierControlPointsChange(updatedBezierControlPoints)
                        }
                    }
                    ContinuityClass.G1 -> {
                        updatedLines = lineSegments.toMutableList().also {
                            val newControlPoint = calculateNewControlPointG1(
                                it[(indexCurrent + 1)%lineSegments.size].end,
                                pointsList[(indexCurrent + 1)%lineSegments.size],
                                it[indexCurrent].bezierSegment!!.control2
                                )
                            it[indexCurrent] = LineSegment(
                                pointsList[indexCurrent],
                                pointsList[(indexCurrent + 1)%lineSegments.size],
                                relation = it[indexCurrent].relation,
                                bezierSegment = CubicBezierSegment(
                                    pointsList[indexCurrent],
                                    it[indexCurrent].bezierSegment!!.control1,
                                    newControlPoint,
                                    pointsList[(indexCurrent + 1)%lineSegments.size],
                                    indexCurrent,
                                    it[indexCurrent].bezierSegment!!.startPointContinuityClass,
                                    it[indexCurrent].bezierSegment!!.endPointContinuityClass
                                )
                            )
                            val updatedBezierControlPoints = bezierControlPoints.toMutableList().apply {
                                this[draggingBezierControlPointIndex!!] = this[draggingBezierControlPointIndex!!].copy(
                                    offset = newControlPoint
                                )
                            }
                            onBezierControlPointsChange(updatedBezierControlPoints)
                        }
                    }
                    ContinuityClass.G0 -> {
                        updatedLines = lineSegments.toMutableList().also {
                            it[indexCurrent] = LineSegment(
                                pointsList[indexCurrent],
                                pointsList[(indexCurrent + 1)%lineSegments.size],
                                relation = it[indexCurrent].relation,
                                bezierSegment = CubicBezierSegment(
                                    pointsList[indexCurrent],
                                    it[indexCurrent].bezierSegment!!.control1,
                                    it[indexCurrent].bezierSegment!!.control2,
                                    pointsList[(indexCurrent + 1)%lineSegments.size],
                                    indexCurrent,
                                    it[indexCurrent].bezierSegment!!.startPointContinuityClass,
                                    it[indexCurrent].bezierSegment!!.endPointContinuityClass
                                )
                            )
                        }
                    }
                }

                lineSegments = updatedLines
                onLinesChange(updatedLines)
                break
            }
            Relations.Vertical -> {
                currentOffset = Offset(currentOffset.x, 0F)

                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        pointsList[indexCurrent] + currentOffset,
                        updatedPoints[(indexCurrent+1)%lineSegments.size],
                        relation = it[indexCurrent].relation
                    )
                }

                pointsList = updatedPoints
                lineSegments = updatedLines
                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lineSegments.size - 1 else indexCurrent - 1

                if(currentOffset.x == 0F && currentOffset.y == 0F) { break }
            }
            Relations.Horizontal -> {
                currentOffset = Offset(0F, currentOffset.y)

                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        pointsList[indexCurrent] + currentOffset,
                        updatedPoints[(indexCurrent+1)%lineSegments.size],
                        relation = it[indexCurrent].relation
                    )
                }

                pointsList = updatedPoints
                lineSegments = updatedLines
                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lineSegments.size - 1 else indexCurrent - 1

                if(currentOffset.x == 0F && currentOffset.y == 0F) { break }
            }
            Relations.FixedLength -> {
                val newStartPoint = calculateNewStartFixedLengthPoint(lineSegments[indexCurrent], currentOffset)

                currentOffset = newStartPoint - lineSegments[indexCurrent].start

                val updatedPoints = pointsList.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                val updatedLines = lineSegments.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        newStartPoint,
                        updatedPoints[(indexCurrent+1)%lineSegments.size],
                        relation = it[indexCurrent].relation
                    )
                }

                pointsList = updatedPoints
                lineSegments = updatedLines
                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lineSegments.size - 1 else indexCurrent - 1
            }
        }
    }
}