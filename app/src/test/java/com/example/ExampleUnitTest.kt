package com.example

import com.example.data.LevelsData
import com.example.engine.FlowGameEngine
import com.example.model.GridPos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FlowGameEngineTest {

    @Test
    fun testAll100LevelsAreValidAndSolvable() {
        assertEquals(100, LevelsData.ALL_LEVELS.size)
        for (level in LevelsData.ALL_LEVELS) {
            val totalCells = level.totalCells
            val allVisitedCells = mutableSetOf<GridPos>()

            for ((colorId, path) in level.solutionPaths) {
                // Must have at least 2 cells
                assertTrue("Level ${level.id} color $colorId path too short", path.size >= 2)

                // Must connect the endpoints
                val endpoints = level.endpoints[colorId]
                assertNotNull("Missing endpoints for level ${level.id} color $colorId", endpoints)
                val isMatching = (path.first() == endpoints!!.first && path.last() == endpoints.second) ||
                        (path.first() == endpoints.second && path.last() == endpoints.first)
                assertTrue("Endpoints do not match path for level ${level.id} color $colorId", isMatching)

                // Each step in path must be orthogonal
                for (i in 0 until path.size - 1) {
                    assertTrue(
                        "Path has non-adjacent step in level ${level.id} color $colorId at step $i",
                        path[i].isAdjacent(path[i + 1])
                    )
                }

                // Path must not self-intersect
                assertEquals(
                    "Self-intersecting path in level ${level.id} color $colorId",
                    path.size,
                    path.toSet().size
                )

                // Non-overlapping with other paths
                for (pos in path) {
                    assertFalse(
                        "Overlapping cell $pos in level ${level.id} color $colorId",
                        allVisitedCells.contains(pos)
                    )
                    allVisitedCells.add(pos)
                }
            }

            // Must cover 100% of grid cells
            assertEquals(
                "Level ${level.id} (${level.size}x${level.size}) does not cover 100% of board",
                totalCells,
                allVisitedCells.size
            )
        }
    }

    @Test
    fun testDiagonalMovementRejected() {
        val level = LevelsData.ALL_LEVELS[0]
        val engine = FlowGameEngine(level)

        val firstColor = level.endpoints.keys.first()
        val startPos = level.endpoints[firstColor]!!.first

        engine.startPath(startPos)
        // Diagonal target: (r+1, c+1)
        val diagonalPos = GridPos(startPos.r + 1, startPos.c + 1)
        val moved = engine.moveTo(diagonalPos)

        assertFalse(moved)
    }

    @Test
    fun testOrthogonalMovementAndBacktracking() {
        val level = LevelsData.ALL_LEVELS[0]
        val engine = FlowGameEngine(level)

        val color = 0
        val solution = level.solutionPaths[color]!!
        val p0 = solution[0]
        val p1 = solution[1]

        engine.startPath(p0)
        val moved = engine.moveTo(p1)
        assertTrue(moved)
        assertEquals(2, engine.paths[color]?.size)

        // Backtrack to p0
        val backtracked = engine.moveTo(p0)
        assertTrue(backtracked)
        assertEquals(1, engine.paths[color]?.size)
    }

    @Test
    fun testRestartAndUndo() {
        val level = LevelsData.ALL_LEVELS[0]
        val engine = FlowGameEngine(level)

        val color = 0
        val solution = level.solutionPaths[color]!!
        engine.startPath(solution[0])
        engine.moveTo(solution[1])
        engine.endPath()

        assertTrue(engine.canUndo())
        assertTrue(engine.paths.isNotEmpty())

        engine.restart()
        assertTrue(engine.paths.isEmpty())
        assertEquals(0, engine.moves)
    }

    @Test
    fun testApplyHintConnectsFlow() {
        val level = LevelsData.ALL_LEVELS[0]
        val engine = FlowGameEngine(level)

        val connectedBefore = engine.connectedColorsCount
        val hinted = engine.applyHint()
        assertNotNull(hinted)
        assertEquals(connectedBefore + 1, engine.connectedColorsCount)
    }
}
