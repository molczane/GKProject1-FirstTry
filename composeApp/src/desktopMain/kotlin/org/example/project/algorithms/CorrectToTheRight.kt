package org.example.project.algorithms

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.ui.geometry.Offset
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations

fun correctToTheRight(
    index: Int,
    dragAmount: Offset,
    lines: List<LineSegment>,
    points: List<Offset>,
    bezierSegments: List<CubicBezierSegment>,
    bezierControlPoints: List<BezierControlPoint>,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPointsChange: (List<Offset>) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
    onBezierControlPointsChange: (List<BezierControlPoint>) -> Unit,
) {
    var currentOffset = dragAmount
    var indexCurrent = index
    while(true) {
        // Secondly, adjusting lines list
        when(lines[indexCurrent].relation) {
            Relations.None -> {
                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        updatedPoints[indexCurrent],
                        it[indexCurrent].end,
                        relation = it[indexCurrent].relation
                    )
                }

                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                break
            }
            Relations.Bezier -> {
                val updatedLines = lines.toMutableList().also {
                    val updatedPoints = points.toMutableList().also {
                        it[indexCurrent] = it[indexCurrent] + currentOffset
                    }

                    onPointsChange(updatedPoints)

                    val newControlPoint = calculateNewControlPointC1(it[if(indexCurrent == 0) lines.size - 1 else indexCurrent - 1].start, points[indexCurrent])
                    it[indexCurrent] = LineSegment( points[indexCurrent], it[indexCurrent].end, relation = it[indexCurrent].relation,
                        bezierSegment = CubicBezierSegment(
                            points[indexCurrent],
                            newControlPoint,
                            it[indexCurrent].bezierSegment!!.control2,
                            it[indexCurrent].bezierSegment!!.end,
                            indexCurrent
                        )
                    )
                }
                onLinesChange(updatedLines)
                break
            }
            Relations.Vertical -> {
                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        start = points[indexCurrent],
                        end = it[indexCurrent].end + Offset(currentOffset.x, 0F),
                        relation = it[indexCurrent].relation
                    )
                }

                onLinesChange(updatedLines)
                currentOffset = Offset(currentOffset.x, 0F)
                indexCurrent = (indexCurrent + 1)%lines.size
            }
            Relations.Horizontal -> {
                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent],
                        it[indexCurrent].end + Offset(0F, dragAmount.y),
                        relation = it[indexCurrent].relation
                    )
                }
                onLinesChange(updatedLines)
                currentOffset = Offset(0F, dragAmount.y)
                indexCurrent = (indexCurrent + 1)%lines.size
            }
            Relations.FixedLength -> {
                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent],
                        it[indexCurrent].end + currentOffset,
                        relation = it[indexCurrent].relation
                    )
                }
                onLinesChange(updatedLines)
                indexCurrent = (indexCurrent + 1)%lines.size
            }
        }
    }
}