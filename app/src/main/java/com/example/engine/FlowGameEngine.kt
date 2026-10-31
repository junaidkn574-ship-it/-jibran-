package com.example.engine

import com.example.model.GridPos
import com.example.model.LevelData

class FlowGameEngine(var level: LevelData, var fullBoardRequired: Boolean = true) {

    // Color ID -> ordered list of GridPos
    var paths: MutableMap<Int, MutableList<GridPos>> = mutableMapOf()
        private set

    var activeColor: Int? = null
        private set

    var moves: Int = 0
        private set

    private val history: ArrayDeque<Map<Int, List<GridPos>>> = ArrayDeque()

    init {
        reset(level)
    }

    fun reset(newLevel: LevelData = level) {
        level = newLevel
        paths.clear()
        activeColor = null
        moves = 0
        history.clear()
    }

    fun isColorConnected(colorId: Int): Boolean {
        val path = paths[colorId] ?: return false
        val endpoints = level.endpoints[colorId] ?: return false
        if (path.size < 2) return false
        val first = path.first()
        val last = path.last()
        return (first == endpoints.first && last == endpoints.second) ||
                (first == endpoints.second && last == endpoints.first)
    }

    val connectedColorsCount: Int
        get() = level.endpoints.keys.count { isColorConnected(it) }

    val occupiedCellsCount: Int
        get() {
            val set = mutableSetOf<GridPos>()
            for (path in paths.values) {
                set.addAll(path)
            }
            return set.size
        }

    val pipePercentage: Int
        get() {
            val total = level.totalCells
            if (total == 0) return 0
            return (occupiedCellsCount * 100) / total
        }

    val isLevelCompleted: Boolean
        get() {
            val allConnected = level.endpoints.keys.all { isColorConnected(it) }
            if (!allConnected) return false
            return if (fullBoardRequired) {
                occupiedCellsCount == level.totalCells
            } else {
                true
            }
        }

    fun calculateStars(): Int {
        if (!isLevelCompleted) return 0
        val isFullBoard = occupiedCellsCount == level.totalCells
        return when {
            moves <= level.parMoves && isFullBoard -> 3
            moves <= level.parMoves + 2 -> 2
            else -> 1
        }
    }

    private fun pushHistory() {
        val snapshot = paths.mapValues { it.value.toList() }
        history.addLast(snapshot)
        if (history.size > 50) {
            history.removeFirst()
        }
    }

    fun startPath(pos: GridPos): Int? {
        val endpointInfo = level.isEndpoint(pos)
        if (endpointInfo != null) {
            val colorId = endpointInfo.first
            pushHistory()
            val existing = paths[colorId]
            if (existing != null && existing.isNotEmpty()) {
                // If touching the head or tail, or restart from touched endpoint
                if (existing.first() == pos) {
                    paths[colorId] = mutableListOf(pos)
                } else if (existing.last() == pos) {
                    // Pick up from the other endpoint: reverse path so last becomes start
                    paths[colorId] = mutableListOf(pos)
                } else {
                    paths[colorId] = mutableListOf(pos)
                }
            } else {
                paths[colorId] = mutableListOf(pos)
            }
            activeColor = colorId
            return colorId
        }

        // If touching along an existing path
        for ((cId, path) in paths) {
            val index = path.indexOf(pos)
            if (index != -1) {
                pushHistory()
                // Trim path up to this pos
                paths[cId] = path.subList(0, index + 1).toMutableList()
                activeColor = cId
                return cId
            }
        }

        return null
    }

    fun moveTo(pos: GridPos): Boolean {
        val color = activeColor ?: return false
        val currentPath = paths[color] ?: return false
        if (currentPath.isEmpty()) return false

        val head = currentPath.last()
        if (pos == head) return false

        // Check if out of bounds
        if (pos.r !in 0 until level.size || pos.c !in 0 until level.size) return false

        // Check backtracking: user dragged back to previous cell in current path
        if (currentPath.size >= 2 && pos == currentPath[currentPath.size - 2]) {
            currentPath.removeAt(currentPath.size - 1)
            return true
        }

        // Check adjacency
        if (!head.isAdjacent(pos)) {
            // Dragged to a cell already in current path -> trim to it
            val idx = currentPath.indexOf(pos)
            if (idx != -1 && idx < currentPath.size - 1) {
                while (currentPath.size > idx + 1) {
                    currentPath.removeAt(currentPath.size - 1)
                }
                return true
            }
            return false
        }

        // Check if target is an endpoint
        val endpointInfo = level.isEndpoint(pos)
        if (endpointInfo != null) {
            val epColor = endpointInfo.first
            if (epColor != color) {
                // Cannot enter another color's endpoint
                return false
            } else {
                // It's the current color's endpoint
                if (pos == currentPath.first()) {
                    // Looped back to start endpoint: clear path to single point
                    currentPath.clear()
                    currentPath.add(pos)
                    return true
                } else {
                    // Reached the matching endpoint! Connect!
                    currentPath.add(pos)
                    activeColor = null // Completed this pipe!
                    return true
                }
            }
        }

        // If target is in current path already -> loop avoidance: trim back to that cell
        val inCurrentIdx = currentPath.indexOf(pos)
        if (inCurrentIdx != -1) {
            while (currentPath.size > inCurrentIdx + 1) {
                currentPath.removeAt(currentPath.size - 1)
            }
            return true
        }

        // Target is an empty or occupied grid cell.
        // If occupied by another color's path, cut that other color's path back to before this cell
        for ((otherColor, otherPath) in paths) {
            if (otherColor != color) {
                val cutIdx = otherPath.indexOf(pos)
                if (cutIdx != -1) {
                    while (otherPath.size > cutIdx) {
                        otherPath.removeAt(otherPath.size - 1)
                    }
                }
            }
        }

        currentPath.add(pos)
        return true
    }

    fun endPath() {
        if (activeColor != null) {
            moves++
            activeColor = null
        } else {
            moves++
        }
    }

    fun undo(): Boolean {
        if (history.isEmpty()) return false
        val previous = history.removeLast()
        paths.clear()
        for ((k, v) in previous) {
            paths[k] = v.toMutableList()
        }
        activeColor = null
        if (moves > 0) moves--
        return true
    }

    fun canUndo(): Boolean = history.isNotEmpty()

    fun restart() {
        if (paths.isNotEmpty() || moves > 0) {
            pushHistory()
            paths.clear()
            activeColor = null
            moves = 0
        }
    }

    fun applyHint(): Int? {
        // Find first unconnected color
        val unconnectedColor = level.endpoints.keys.firstOrNull { !isColorConnected(it) } ?: return null
        val solution = level.solutionPaths[unconnectedColor] ?: return null

        pushHistory()
        // Cut any overlapping paths
        for (pos in solution) {
            for ((otherColor, otherPath) in paths) {
                if (otherColor != unconnectedColor) {
                    val cutIdx = otherPath.indexOf(pos)
                    if (cutIdx != -1) {
                        while (otherPath.size > cutIdx) {
                            otherPath.removeAt(otherPath.size - 1)
                        }
                    }
                }
            }
        }

        paths[unconnectedColor] = solution.toMutableList()
        moves++
        return unconnectedColor
    }

    fun getCellContent(pos: GridPos): CellContent {
        val ep = level.isEndpoint(pos)
        if (ep != null) {
            return CellContent.Endpoint(ep.first)
        }
        for ((colorId, path) in paths) {
            val idx = path.indexOf(pos)
            if (idx != -1) {
                return CellContent.Pipe(colorId)
            }
        }
        return CellContent.Empty
    }

    sealed interface CellContent {
        data class Endpoint(val colorId: Int) : CellContent
        data class Pipe(val colorId: Int) : CellContent
        data object Empty : CellContent
    }
}
