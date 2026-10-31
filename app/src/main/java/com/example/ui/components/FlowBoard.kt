package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.engine.FlowGameEngine
import com.example.model.BoardTheme
import com.example.model.FlowColor
import com.example.model.GridPos

@Composable
fun FlowBoard(
    engine: FlowGameEngine,
    theme: BoardTheme,
    modifier: Modifier = Modifier,
    onStepMade: () -> Unit = {},
    onFlowConnected: (FlowColor) -> Unit = {},
    onDragFinished: () -> Unit = {}
) {
    val level = engine.level
    val gridSize = level.size

    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var lastGridPos by remember { mutableStateOf<GridPos?>(null) }

    BoxWithConstraints(
        modifier = modifier
            .padding(12.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        val boardPixelSize = minOf(constraints.maxWidth, constraints.maxHeight).toFloat()
        val cellSize = boardPixelSize / gridSize

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("flow_board_canvas")
                .pointerInput(gridSize, engine) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val c = (offset.x / cellSize).toInt().coerceIn(0, gridSize - 1)
                            val r = (offset.y / cellSize).toInt().coerceIn(0, gridSize - 1)
                            val pos = GridPos(r, c)
                            touchOffset = offset
                            lastGridPos = pos

                            val colorStarted = engine.startPath(pos)
                            if (colorStarted != null) {
                                onStepMade()
                            }
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val offset = change.position
                            touchOffset = offset
                            val c = (offset.x / cellSize).toInt().coerceIn(0, gridSize - 1)
                            val r = (offset.y / cellSize).toInt().coerceIn(0, gridSize - 1)
                            val targetPos = GridPos(r, c)

                            val prev = lastGridPos
                            if (prev != null && targetPos != prev) {
                                // Step through intermediate cells if fast swipe skipped a cell
                                val dr = targetPos.r - prev.r
                                val dc = targetPos.c - prev.c

                                if (kotlin.math.abs(dr) + kotlin.math.abs(dc) == 1) {
                                    val moved = engine.moveTo(targetPos)
                                    if (moved) {
                                        lastGridPos = targetPos
                                        onStepMade()
                                        // Check if this move just completed the color
                                        val active = engine.activeColor
                                        if (active == null) {
                                            // Means it just connected!
                                            val connectedColor = level.endpoints.entries.firstOrNull {
                                                engine.isColorConnected(it.key) &&
                                                        (it.value.first == targetPos || it.value.second == targetPos)
                                            }?.key
                                            if (connectedColor != null) {
                                                onFlowConnected(FlowColor.getById(connectedColor))
                                            }
                                        }
                                    }
                                } else {
                                    // Move horizontally first then vertically
                                    var currR = prev.r
                                    var currC = prev.c
                                    val stepC = if (dc > 0) 1 else if (dc < 0) -1 else 0
                                    val stepR = if (dr > 0) 1 else if (dr < 0) -1 else 0

                                    while (currC != targetPos.c) {
                                        currC += stepC
                                        val intermediate = GridPos(currR, currC)
                                        if (engine.moveTo(intermediate)) {
                                            lastGridPos = intermediate
                                            onStepMade()
                                        }
                                    }
                                    while (currR != targetPos.r) {
                                        currR += stepR
                                        val intermediate = GridPos(currR, currC)
                                        if (engine.moveTo(intermediate)) {
                                            lastGridPos = intermediate
                                            onStepMade()
                                        }
                                    }
                                    lastGridPos = targetPos
                                }
                            }
                        },
                        onDragEnd = {
                            touchOffset = null
                            lastGridPos = null
                            engine.endPath()
                            onDragFinished()
                        },
                        onDragCancel = {
                            touchOffset = null
                            lastGridPos = null
                            engine.endPath()
                            onDragFinished()
                        }
                    )
                }
        ) {
            val pipeStrokeWidth = cellSize * 0.38f
            val endpointRadius = cellSize * 0.34f

            // 1. Draw Board background
            drawRect(
                color = theme.boardBackground,
                topLeft = Offset.Zero,
                size = Size(size.width, size.height)
            )

            // 2. Draw cell tints for cells traversed by pipes
            for ((colorId, path) in engine.paths) {
                val flowColor = FlowColor.getById(colorId)
                for (pos in path) {
                    drawRect(
                        color = flowColor.darkTint,
                        topLeft = Offset(pos.c * cellSize, pos.r * cellSize),
                        size = Size(cellSize, cellSize)
                    )
                }
            }

            // Also tint endpoints
            for ((colorId, pair) in level.endpoints) {
                val flowColor = FlowColor.getById(colorId)
                drawRect(
                    color = flowColor.darkTint,
                    topLeft = Offset(pair.first.c * cellSize, pair.first.r * cellSize),
                    size = Size(cellSize, cellSize)
                )
                drawRect(
                    color = flowColor.darkTint,
                    topLeft = Offset(pair.second.c * cellSize, pair.second.r * cellSize),
                    size = Size(cellSize, cellSize)
                )
            }

            // 3. Draw grid lines
            for (i in 0..gridSize) {
                val offset = i * cellSize
                // Vertical lines
                drawLine(
                    color = theme.gridBorderColor,
                    start = Offset(offset, 0f),
                    end = Offset(offset, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                // Horizontal lines
                drawLine(
                    color = theme.gridBorderColor,
                    start = Offset(0f, offset),
                    end = Offset(size.width, offset),
                    strokeWidth = 2.dp.toPx()
                )
            }

            // 4. Draw Pipes
            for ((colorId, path) in engine.paths) {
                if (path.size >= 2) {
                    val flowColor = FlowColor.getById(colorId)
                    val composePath = Path().apply {
                        val firstCenter = Offset(
                            (path[0].c + 0.5f) * cellSize,
                            (path[0].r + 0.5f) * cellSize
                        )
                        moveTo(firstCenter.x, firstCenter.y)
                        for (i in 1 until path.size) {
                            val nextCenter = Offset(
                                (path[i].c + 0.5f) * cellSize,
                                (path[i].r + 0.5f) * cellSize
                            )
                            lineTo(nextCenter.x, nextCenter.y)
                        }
                    }

                    // Outer pipe line
                    drawPath(
                        path = composePath,
                        color = flowColor.mainColor,
                        style = Stroke(
                            width = pipeStrokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Inner bright core shine
                    drawPath(
                        path = composePath,
                        color = Color.White.copy(alpha = 0.22f),
                        style = Stroke(
                            width = pipeStrokeWidth * 0.35f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            // 5. Draw Endpoints
            for ((colorId, pair) in level.endpoints) {
                val flowColor = FlowColor.getById(colorId)
                drawEndpoint(pair.first, cellSize, endpointRadius, flowColor)
                drawEndpoint(pair.second, cellSize, endpointRadius, flowColor)
            }

            // 6. Draw active finger touch aura if dragging
            val activeColorId = engine.activeColor
            val currentTouch = touchOffset
            if (activeColorId != null && currentTouch != null) {
                val flowColor = FlowColor.getById(activeColorId)
                // Large translucent aura circle under finger
                drawCircle(
                    color = flowColor.glowColor.copy(alpha = 0.28f),
                    radius = cellSize * 0.85f,
                    center = currentTouch
                )
                drawCircle(
                    color = flowColor.mainColor.copy(alpha = 0.5f),
                    radius = cellSize * 0.45f,
                    center = currentTouch
                )
            }
        }
    }
}

private fun DrawScope.drawEndpoint(
    pos: GridPos,
    cellSize: Float,
    radius: Float,
    color: FlowColor
) {
    val center = Offset((pos.c + 0.5f) * cellSize, (pos.r + 0.5f) * cellSize)

    // Outer glow halo
    drawCircle(
        color = color.glowColor.copy(alpha = 0.35f),
        radius = radius * 1.35f,
        center = center
    )

    // Main solid circle
    drawCircle(
        color = color.mainColor,
        radius = radius,
        center = center
    )

    // Inner bright center dot for 3D look
    drawCircle(
        color = Color.White.copy(alpha = 0.4f),
        radius = radius * 0.4f,
        center = center - Offset(radius * 0.2f, radius * 0.2f)
    )
}
