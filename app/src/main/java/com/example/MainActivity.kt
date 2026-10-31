package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.audio.HapticsPlayer
import com.example.audio.SoundPlayer
import com.example.data.GamePreferences
import com.example.model.BoardTheme
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayDialog
import com.example.ui.screens.LevelSelectScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.AsadFlowTheme

sealed interface Screen {
    data object Home : Screen
    data object LevelSelect : Screen
    data class Game(val levelId: Int) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = GamePreferences(this)
        val soundPlayer = SoundPlayer().apply { isEnabled = prefs.soundEnabled }
        val hapticsPlayer = HapticsPlayer(this).apply { isEnabled = prefs.vibrationEnabled }

        setContent {
            AsadFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF0D1117)
                ) {
                    FlowApp(
                        prefs = prefs,
                        soundPlayer = soundPlayer,
                        hapticsPlayer = hapticsPlayer
                    )
                }
            }
        }
    }
}

@Composable
fun FlowApp(
    prefs: GamePreferences,
    soundPlayer: SoundPlayer,
    hapticsPlayer: HapticsPlayer
) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showHowToPlay by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var preferencesRevision by remember { mutableIntStateOf(0) }

    // Sync audio and haptics setting with prefs
    soundPlayer.isEnabled = prefs.soundEnabled
    hapticsPlayer.isEnabled = prefs.vibrationEnabled

    when (val screen = currentScreen) {
        is Screen.Home -> {
            HomeScreen(
                prefs = prefs,
                onPlayClicked = { levelId ->
                    currentScreen = Screen.Game(levelId)
                },
                onLevelSelectClicked = {
                    currentScreen = Screen.LevelSelect
                },
                onHowToPlayClicked = {
                    showHowToPlay = true
                },
                onSettingsClicked = {
                    showSettings = true
                }
            )
        }

        is Screen.LevelSelect -> {
            BackHandler {
                currentScreen = Screen.Home
            }
            LevelSelectScreen(
                prefs = prefs,
                onLevelSelected = { levelId ->
                    currentScreen = Screen.Game(levelId)
                },
                onBackClicked = {
                    currentScreen = Screen.Home
                }
            )
        }

        is Screen.Game -> {
            BackHandler {
                currentScreen = Screen.LevelSelect
            }
            GameScreen(
                levelId = screen.levelId,
                prefs = prefs,
                soundPlayer = soundPlayer,
                hapticsPlayer = hapticsPlayer,
                onNavigateBack = {
                    currentScreen = Screen.LevelSelect
                },
                onNavigateToLevel = { nextId ->
                    currentScreen = Screen.Game(nextId)
                },
                onOpenSettings = {
                    showSettings = true
                }
            )
        }
    }

    if (showHowToPlay) {
        HowToPlayDialog(onDismiss = { showHowToPlay = false })
    }

    if (showSettings) {
        SettingsDialog(
            prefs = prefs,
            onDismiss = {
                soundPlayer.isEnabled = prefs.soundEnabled
                hapticsPlayer.isEnabled = prefs.vibrationEnabled
                showSettings = false
            },
            onThemeChanged = { _ ->
                preferencesRevision++
            },
            onProgressReset = {
                preferencesRevision++
            }
        )
    }
}
