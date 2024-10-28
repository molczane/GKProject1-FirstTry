package org.example.project.utils

fun isEveryLineFixedLength(lines: List<LineSegment>): Boolean
{
    lines.forEach { line ->
        if(line.relation != Relations.FixedLength){
            return false
        }
    }
    return true
}