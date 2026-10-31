package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.GamePreferences

@Composable
fun HomeScreen(
    prefs: GamePreferences,
    onPlayClicked: (levelId: Int) -> Unit,
    onLevelSelectClicked: () -> Unit,
    onHowToPlayClicked: () -> Unit,
    onSettingsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCleared = prefs.getTotalCompletedLevels()
    val totalStars = prefs.getTotalStarsEarned()
    val nextLevel = prefs.highestUnlockedLevel

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F141C),
                        Color(0xFF0A0D14),
                        Color(0xFF05070A)
                    )
                )
            )
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Icon Graphic
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(2.dp, Color(0xFF2979FF).copy(alpha = 0.5f), RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground_flow_1792576247168),
                    contentDescription = "Asad Flow Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = "ASAD FLOW",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = 2.sp
            )

            Text(
                text = "Colored Pipe Puzzle Game",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF00E5FF),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Progress Summary Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalCleared / 100",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Levels Cleared",
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 1.dp, height = 30.dp)
                            .background(Color(0xFF30363D))
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.size(4.dp))
                            Text(
                                text = "$totalStars",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = "Stars Earned",
                            fontSize = 12.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // PLAY BUTTON
            Button(
                onClick = { onPlayClicked(nextLevel) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF238636)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp)
                    .testTag("play_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = if (totalCleared == 0) "PLAY NOW" else "CONTINUE (LEVEL $nextLevel)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LEVEL SELECT
            Button(
                onClick = onLevelSelectClicked,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1F6FEB)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("level_select_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ViewModule,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "LEVEL SELECTION (100)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECONDARY BUTTONS (How to Play, Settings)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onHowToPlayClicked,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("how_to_play_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = "Rules", color = Color.White, fontSize = 14.sp)
                }

                OutlinedButton(
                    onClick = onSettingsClicked,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(text = "Settings", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}
