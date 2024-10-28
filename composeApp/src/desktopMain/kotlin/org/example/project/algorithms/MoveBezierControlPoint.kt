import androidx.compose.ui.geometry.Offset
import org.example.project.algorithms.calculateNewPointC1
import org.example.project.algorithms.calculateNewPointG1
import org.example.project.utils.BezierControlPoint
import org.example.project.utils.ContinuityClass
import org.example.project.utils.CubicBezierSegment
import org.example.project.utils.LineSegment
import org.example.project.utils.Relations

fun moveBezierControlPoint(
    index: Int,
    dragAmount: Offset,
    bezierControlPoints: List<BezierControlPoint>,
    lines: List<LineSegment>,
    points: List<Offset>,
    bezierSegments: List<CubicBezierSegment>,
    onBezierControlPointsChange: (List<BezierControlPoint>) -> Unit,
    onLinesChange: (List<LineSegment>) -> Unit,
    onPointsChange: (List<Offset>) -> Unit,
    onBezierSegmentsChange: (List<CubicBezierSegment>) -> Unit,
) : Pair<Offset, Int> {
    val bezierControlPoint = bezierControlPoints[index]
    val controlPointNumber = bezierControlPoint.number
    val lineIndex = bezierControlPoints[index].lineIndex
    val line = lines[lineIndex]
    val oldBezierSegment = line.bezierSegment!!

    // Update control point position
    val updatedControlPoints = bezierControlPoints.toMutableList().also {
        it[index] = it[index].copy(offset = it[index].offset + dragAmount)
    }
    onBezierControlPointsChange(updatedControlPoints)

    if(controlPointNumber == 1)
    {
        val previousIndex = if(lineIndex == 0) lines.size - 1 else lineIndex - 1
        when(lines[previousIndex].relation)
        {
            Relations.None -> {
                when (line.bezierSegment!!.startPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            lines[lineIndex].start,
                            lines[if (lineIndex == 0) lines.size - 1 else lineIndex - 1].start
                        )
                        val updatedPoints = points.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val offset = newPoint - lines[previousIndex].start
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = this[previousIndex].start + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }

                    ContinuityClass.C1 -> {
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            lines[lineIndex].start
                        )
                        val updatedPoints = points.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val offset = newPoint - lines[previousIndex].start
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = this[previousIndex].start + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }
                }
            }
            Relations.FixedLength -> {
                when (line.bezierSegment!!.startPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[previousIndex] = this[previousIndex] + dragAmount
                            this[lineIndex] = this[lineIndex] + dragAmount
                        }
                        onPointsChange(updatedPoints)
                        val offset = dragAmount
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = this[lineIndex].start + offset,
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = oldBezierSegment.start + offset,
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = this[previousIndex].start + offset,
                                end = this[previousIndex].end + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }
                    ContinuityClass.C1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[previousIndex] = this[previousIndex] + dragAmount
                            this[lineIndex] = this[lineIndex] + dragAmount
                        }
                        onPointsChange(updatedPoints)
                        val offset = dragAmount
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = this[lineIndex].start + offset,
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = oldBezierSegment.start + offset,
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = this[previousIndex].start + offset,
                                end = this[previousIndex].end + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }
                }
            }
            Relations.Horizontal -> {
                when (line.bezierSegment!!.startPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPointsOld = points.toMutableList().apply {
                            this[lineIndex] = this[lineIndex] + Offset(0F, dragAmount.y)
                        }
                        onPointsChange(updatedPointsOld)
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            updatedPointsOld[lineIndex],
                            lines[if (lineIndex == 0) lines.size - 1 else lineIndex - 1].start
                        )
                        val offset = newPoint - updatedPointsOld[previousIndex]
                        val updatedPoints = updatedPointsOld.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = updatedPoints[lineIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = updatedPoints[previousIndex],
                                end = updatedPoints[lineIndex]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }

                    ContinuityClass.C1 -> {
                        val updatedPointsOld = points.toMutableList().apply {
                            this[lineIndex] = this[lineIndex] + Offset(0F, dragAmount.y)
                        }
                        onPointsChange(updatedPointsOld)
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            updatedPointsOld[lineIndex]
                        )
                        val offset = newPoint - updatedPointsOld[previousIndex]
                        val updatedPoints = updatedPointsOld.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = updatedPoints[lineIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = updatedPoints[previousIndex],
                                end = updatedPoints[lineIndex]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }
                }
            }
            Relations.Vertical -> {
                when (line.bezierSegment!!.startPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPointsOld = points.toMutableList().apply {
                            this[lineIndex] = this[lineIndex] + Offset(dragAmount.x, 0F)
                        }
                        onPointsChange(updatedPointsOld)
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            updatedPointsOld[lineIndex],
                            lines[if (lineIndex == 0) lines.size - 1 else lineIndex - 1].start
                        )
                        val offset = newPoint - updatedPointsOld[previousIndex]
                        val updatedPoints = updatedPointsOld.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = updatedPoints[lineIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = updatedPoints[previousIndex],
                                end = updatedPoints[lineIndex]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }

                    ContinuityClass.C1 -> {
                        val updatedPointsOld = points.toMutableList().apply {
                            this[lineIndex] = this[lineIndex] + Offset(dragAmount.x, 0F)
                        }
                        onPointsChange(updatedPointsOld)
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            updatedPointsOld[lineIndex]
                        )
                        val offset = newPoint - updatedPointsOld[previousIndex]
                        val updatedPoints = updatedPointsOld.toMutableList().apply {
                            this[previousIndex] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                start = updatedPoints[lineIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[previousIndex] = this[previousIndex].copy(
                                start = updatedPoints[previousIndex],
                                end = updatedPoints[lineIndex]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            if (previousIndex == 0) lines.size - 1 else previousIndex - 1
                        )
                    }
                }
            }
            Relations.Bezier -> {
                /* Do nothing */
            }
        }
    }
    else if(controlPointNumber == 2) {
        val nextIndex = (lineIndex + 1) % lines.size
        when(lines[nextIndex].relation)
        {
            Relations.None -> {
                when (line.bezierSegment!!.endPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            lines[lineIndex].end,
                            lines[nextIndex].end
                        )
                        val updatedPoints = points.toMutableList().apply {
                            this[(nextIndex + 1)%lines.size] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val offset = newPoint - lines[nextIndex].end
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                end = updatedPoints[(nextIndex + 1)%lines.size]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }

                    ContinuityClass.C1 -> {
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            lines[lineIndex].end
                        )
                        val updatedPoints = points.toMutableList().apply {
                            this[(nextIndex + 1)%lines.size] = newPoint
                        }
                        onPointsChange(updatedPoints)
                        val offset = newPoint - lines[nextIndex].end
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                end = updatedPoints[(nextIndex + 1)%lines.size]
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }
                }
            }
            Relations.FixedLength -> {
                when (line.bezierSegment!!.endPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + dragAmount
                        }
                        onPointsChange(updatedPoints)
                        val offset = dragAmount
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = this[lineIndex].end + offset,
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = oldBezierSegment.start + offset,
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = oldBezierSegment.end + offset
                                       )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = this[nextIndex].start + offset,
                                end = this[nextIndex].end + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }
                    ContinuityClass.C1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + dragAmount
                        }
                        onPointsChange(updatedPoints)
                        val offset = dragAmount
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = this[lineIndex].end + offset,
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = oldBezierSegment.start + offset,
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = oldBezierSegment.end + offset
                                    )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = this[nextIndex].start + offset,
                                end = this[nextIndex].end + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }
                }
            }
            Relations.Horizontal -> {
                when (line.bezierSegment!!.endPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + Offset(0F, dragAmount.y)
                        }
                        onPointsChange(updatedPoints)
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            updatedPoints[nextIndex],
                            lines[nextIndex].end
                        )
                        val offset = newPoint - updatedPoints[(nextIndex + 1)%lines.size]
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = updatedPoints[nextIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = updatedPoints[nextIndex]
                                    )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = updatedPoints[nextIndex],
                                end = updatedPoints[(nextIndex+1)%lines.size] + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }

                    ContinuityClass.C1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + Offset(0F, dragAmount.y)
                        }
                        onPointsChange(updatedPoints)
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            updatedPoints[nextIndex]
                        )
                        val offset = newPoint - updatedPoints[(nextIndex + 1)%lines.size]
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = updatedPoints[nextIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = updatedPoints[nextIndex]
                                    )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = updatedPoints[nextIndex],
                                end = updatedPoints[(nextIndex+1)%lines.size] + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }
                }
            }
            Relations.Vertical -> {
                when (line.bezierSegment!!.endPointContinuityClass) {
                    ContinuityClass.G0 -> {
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(control1 = updatedControlPoints[index].offset)
                                } else {
                                    oldBezierSegment.copy(control2 = updatedControlPoints[index].offset)
                                }
                            )
                        }
                        onLinesChange(updatedLines)
                    }

                    ContinuityClass.G1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + Offset(dragAmount.x, 0F)
                        }
                        onPointsChange(updatedPoints)
                        val newPoint = calculateNewPointG1(
                            updatedControlPoints[index].offset,
                            updatedPoints[nextIndex],
                            lines[nextIndex].end
                        )
                        val offset = newPoint - updatedPoints[(nextIndex + 1)%lines.size]
                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = updatedPoints[nextIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = updatedPoints[nextIndex]
                                    )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = updatedPoints[nextIndex],
                                end = updatedPoints[(nextIndex+1)%lines.size] + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }

                    ContinuityClass.C1 -> {
                        val updatedPoints = points.toMutableList().apply {
                            this[nextIndex] = this[nextIndex] + Offset(dragAmount.x, 0F)
                        }
                        onPointsChange(updatedPoints)
                        val newPoint = calculateNewPointC1(
                            updatedControlPoints[index].offset,
                            updatedPoints[nextIndex]
                        )
                        val offset = newPoint - updatedPoints[(nextIndex + 1)%lines.size]

                        val updatedLines = lines.toMutableList().apply {
                            this[lineIndex] = line.copy(
                                end = updatedPoints[nextIndex],
                                bezierSegment = if (bezierControlPoints[index].number == 1) {
                                    oldBezierSegment.copy(
                                        start = updatedPoints[lineIndex],
                                        control1 = updatedControlPoints[index].offset
                                    )
                                } else {
                                    oldBezierSegment.copy(
                                        control2 = updatedControlPoints[index].offset,
                                        end = updatedPoints[nextIndex]
                                    )
                                }
                            )
                            this[nextIndex] = this[nextIndex].copy(
                                start = updatedPoints[nextIndex],
                                end = updatedPoints[(nextIndex+1)%lines.size] + offset
                            )
                        }
                        onLinesChange(updatedLines)
                        return Pair(
                            offset,
                            (nextIndex + 1)%lines.size
                        )
                    }
                }
            }
            Relations.Bezier -> {
                /* Do nothing */
            }
        }
    }
    return Pair(Offset.Zero, 0)
}
