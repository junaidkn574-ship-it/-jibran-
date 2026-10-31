package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GamePreferences
import com.example.model.BoardTheme

@Composable
fun SettingsDialog(
    prefs: GamePreferences,
    onDismiss: () -> Unit,
    onThemeChanged: (BoardTheme) -> Unit,
    onProgressReset: () -> Unit
) {
    var soundEnabled by remember { mutableStateOf(prefs.soundEnabled) }
    var vibrationEnabled by remember { mutableStateOf(prefs.vibrationEnabled) }
    var fullBoardRequired by remember { mutableStateOf(prefs.fullBoardRequired) }
    var currentThemeId by remember { mutableStateOf(prefs.themeId) }
    var showResetConfirmation by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_settings_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sound Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Sound Effects", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Melodic chimes when connecting", color = Color(0xFF8B949E), fontSize = 12.sp)
                    }
                    Switch(
                        checked = soundEnabled,
                        onCheckedChange = {
                            soundEnabled = it
                            prefs.soundEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF238636)
                        ),
                        modifier = Modifier.testTag("sound_toggle")
                    )
                }

                // Vibration Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Vibration", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Haptic clicks on cell moves", color = Color(0xFF8B949E), fontSize = 12.sp)
                    }
                    Switch(
                        checked = vibrationEnabled,
                        onCheckedChange = {
                            vibrationEnabled = it
                            prefs.vibrationEnabled = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF238636)
                        ),
                        modifier = Modifier.testTag("vibration_toggle")
                    )
                }

                // Full Board Required Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Full Board Mode", color = Color.White, fontWeight = FontWeight.Medium)
                        Text("Require 100% cells filled to win", color = Color(0xFF8B949E), fontSize = 12.sp)
                    }
                    Switch(
                        checked = fullBoardRequired,
                        onCheckedChange = {
                            fullBoardRequired = it
                            prefs.fullBoardRequired = it
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF238636)
                        ),
                        modifier = Modifier.testTag("full_board_toggle")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Board Theme Selection
                Text(
                    text = "Board Theme",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BoardTheme.ALL.forEach { theme ->
                        val isSelected = theme.id == currentThemeId
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .background(theme.boardBackground, RoundedCornerShape(10.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF00E5FF) else theme.gridBorderColor,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    currentThemeId = theme.id
                                    prefs.themeId = theme.id
                                    onThemeChanged(theme)
                                }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = theme.name.split(" ").first(),
                                color = if (isSelected) Color(0xFF00E5FF) else Color(0xFF8B949E),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Reset Progress Button
                Button(
                    onClick = { showResetConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA3633)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("reset_progress_button")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Reset All Progress", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Reset Progress?", fontWeight = FontWeight.Bold, color = Color.White) },
            text = {
                Text(
                    "This will clear all unlocked levels, best scores, and stars. This cannot be undone.",
                    color = Color(0xFF8B949E)
                )
            },
            containerColor = Color(0xFF161B22),
            confirmButton = {
                Button(
                    onClick = {
                        prefs.resetAllProgress()
                        soundEnabled = prefs.soundEnabled
                        vibrationEnabled = prefs.vibrationEnabled
                        fullBoardRequired = prefs.fullBoardRequired
                        showResetConfirmation = false
                        onProgressReset()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDA3633))
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) {
                    Text("Cancel", color = Color.White)
                }
            }
        )
    }
}
