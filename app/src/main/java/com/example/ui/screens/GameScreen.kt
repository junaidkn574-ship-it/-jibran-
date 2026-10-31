package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.HapticsPlayer
import com.example.audio.SoundPlayer
import com.example.data.GamePreferences
import com.example.data.LevelsData
import com.example.engine.FlowGameEngine
import com.example.model.BoardTheme
import com.example.ui.components.FlowBoard
import kotlinx.coroutines.launch

@Composable
fun GameScreen(
    levelId: Int,
    prefs: GamePreferences,
    soundPlayer: SoundPlayer,
    hapticsPlayer: HapticsPlayer,
    onNavigateBack: () -> Unit,
    onNavigateToLevel: (Int) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val level = remember(levelId) {
        LevelsData.ALL_LEVELS.find { it.id == levelId } ?: LevelsData.ALL_LEVELS.first()
    }

    val theme = remember(prefs.themeId) {
        BoardTheme.getById(prefs.themeId)
    }

    val engine = remember(level, prefs.fullBoardRequired) {
        FlowGameEngine(level, prefs.fullBoardRequired)
    }

    // Trigger state recomposition on board updates
    var updateCounter by remember { mutableIntStateOf(0) }
    var showVictoryDialog by remember { mutableStateOf(false) }
    var earnedStars by remember { mutableIntStateOf(0) }
    var hintsCount by remember { mutableIntStateOf(prefs.hintsRemaining) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Check completion
    fun checkWinCondition() {
        if (engine.isLevelCompleted && !showVictoryDialog) {
            val stars = engine.calculateStars()
            earnedStars = stars
            val isFirstTime = prefs.saveLevelCompletion(level.id, engine.moves, stars)
            if (isFirstTime) {
                // Reward hint on first clear
                prefs.addHint()
                hintsCount = prefs.hintsRemaining
            }
            soundPlayer.playVictoryFanfare()
            hapticsPlayer.completeVictory()
            showVictoryDialog = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(theme.screenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // TOP SECTION: Header & Status Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                // 1. Top Bar: Back button, Level title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color(0xFF161B22), CircleShape)
                            .testTag("game_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "level ${level.id}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFEB3B)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "${level.size}x${level.size}",
                            fontSize = 18.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }

                // 2. Stats bar: flows, moves, pipe %
                val connectedFlows = engine.connectedColorsCount
                val totalFlows = level.totalColors
                val movesTaken = engine.moves
                val bestMoves = prefs.getBestMoves(level.id)
                val pipePct = engine.pipePercentage

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF161B22))
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "flows: $connectedFlows/$totalFlows",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (connectedFlows == totalFlows) Color(0xFF00E676) else Color.White
                    )

                    Text(
                        text = "moves: $movesTaken   best: ${if (bestMoves > 0) bestMoves else "-"}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Text(
                        text = "pipe: $pipePct%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (pipePct == 100) Color(0xFF00E5FF) else Color.White
                    )
                }
            }

            // MIDDLE SECTION: FlowBoard Canvas
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                FlowBoard(
                    engine = engine,
                    theme = theme,
                    onStepMade = {
                        hapticsPlayer.click()
                        updateCounter++
                    },
                    onFlowConnected = { flowColor ->
                        soundPlayer.playTone(flowColor.toneFrequency, 180)
                        hapticsPlayer.success()
                        updateCounter++
                        checkWinCondition()
                    },
                    onDragFinished = {
                        updateCounter++
                        checkWinCondition()
                    }
                )
            }

            // BOTTOM ACTION BAR: Controls matching screenshot!
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings button
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF161B22), CircleShape)
                        .testTag("game_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Undo button
                IconButton(
                    onClick = {
                        if (engine.undo()) {
                            soundPlayer.playUndo()
                            hapticsPlayer.click()
                            updateCounter++
                        }
                    },
                    enabled = engine.canUndo(),
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (engine.canUndo()) Color(0xFF161B22) else Color(0xFF0D1117),
                            CircleShape
                        )
                        .testTag("undo_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo",
                        tint = if (engine.canUndo()) Color.White else Color(0xFF484F58),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Previous Level
                IconButton(
                    onClick = {
                        if (level.id > 1) {
                            onNavigateToLevel(level.id - 1)
                        }
                    },
                    enabled = level.id > 1,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (level.id > 1) Color(0xFF161B22) else Color(0xFF0D1117),
                            CircleShape
                        )
                        .testTag("prev_level_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Level",
                        tint = if (level.id > 1) Color.White else Color(0xFF484F58),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Restart button
                IconButton(
                    onClick = {
                        engine.restart()
                        soundPlayer.playUndo()
                        hapticsPlayer.click()
                        updateCounter++
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFF161B22), CircleShape)
                        .testTag("restart_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Next Level button
                val canGoNext = level.id < 100 && prefs.isLevelUnlocked(level.id + 1)
                IconButton(
                    onClick = {
                        if (level.id < 100 && prefs.isLevelUnlocked(level.id + 1)) {
                            onNavigateToLevel(level.id + 1)
                        }
                    },
                    enabled = canGoNext,
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (canGoNext) Color(0xFF161B22) else Color(0xFF0D1117),
                            CircleShape
                        )
                        .testTag("next_level_button_bar")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Level",
                        tint = if (canGoNext) Color.White else Color(0xFF484F58),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Hint button with count
                Row(
                    modifier = Modifier
                        .height(44.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF161B22))
                        .clickable {
                            if (hintsCount > 0) {
                                val hintedColor = engine.applyHint()
                                if (hintedColor != null) {
                                    prefs.useHint()
                                    hintsCount = prefs.hintsRemaining
                                    soundPlayer.playTone(600f, 200)
                                    hapticsPlayer.success()
                                    updateCounter++
                                    checkWinCondition()
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("All flows are already connected!")
                                    }
                                }
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("No hints left! Complete levels for hints.")
                                }
                            }
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$hintsCount x",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Hint",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }

    // Victory Dialog
    if (showVictoryDialog) {
        LevelCompleteDialog(
            levelId = level.id,
            moves = engine.moves,
            parMoves = level.parMoves,
            stars = earnedStars,
            pipeCoverage = engine.pipePercentage,
            hasNextLevel = level.id < 100,
            onNextLevel = {
                showVictoryDialog = false
                onNavigateToLevel(level.id + 1)
            },
            onReplay = {
                showVictoryDialog = false
                engine.restart()
                updateCounter++
            },
            onLevelSelect = {
                showVictoryDialog = false
                onNavigateBack()
            }
        )
    }
}
