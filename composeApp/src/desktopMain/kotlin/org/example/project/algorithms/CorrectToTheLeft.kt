package org.example.project.algorithms

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.ui.geometry.Offset
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations

fun correctToTheLeft(
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
                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent],
                        points[(indexCurrent + 1)%lines.size],
                        relation = it[indexCurrent].relation
                    )
                }

                onLinesChange(updatedLines)
                // not updating points here
                break
            }
            Relations.Bezier -> {
                val updatedLines = lines.toMutableList().also {
                    val newControlPoint = calculateNewControlPointC1(it[(indexCurrent + 1)%lines.size].end, points[(indexCurrent + 1)%lines.size])
                    it[indexCurrent] = LineSegment( points[indexCurrent], points[(indexCurrent + 1)%lines.size], relation = it[indexCurrent].relation,
                        bezierSegment = CubicBezierSegment(
                            points[indexCurrent],
                            it[indexCurrent].bezierSegment!!.control1,
                            newControlPoint,
                            points[(indexCurrent + 1)%lines.size],
                            indexCurrent
                        )
                    )
                }

                onLinesChange(updatedLines)
                break
            }
            Relations.Vertical -> {
                currentOffset = Offset(currentOffset.x, 0F)

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent] + currentOffset,
                        it[indexCurrent].end,
                        relation = it[indexCurrent].relation
                    )
                }

                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lines.size - 1 else indexCurrent - 1
            }
            Relations.Horizontal -> {
                currentOffset = Offset(0F, currentOffset.y)

                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent] + currentOffset,
                        it[indexCurrent].end,
                        relation = it[indexCurrent].relation
                    )
                }

                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lines.size - 1 else indexCurrent - 1
            }
            Relations.FixedLength -> {
                val updatedLines = lines.toMutableList().also {
                    it[indexCurrent] = LineSegment(
                        points[indexCurrent] + currentOffset,
                        it[indexCurrent].end,
                        relation = it[indexCurrent].relation
                    )
                }

                val updatedPoints = points.toMutableList().also {
                    it[indexCurrent] = it[indexCurrent] + currentOffset
                }

                onPointsChange(updatedPoints)
                onLinesChange(updatedLines)
                indexCurrent = if(indexCurrent == 0) lines.size - 1 else indexCurrent - 1
            }
        }
    }
}