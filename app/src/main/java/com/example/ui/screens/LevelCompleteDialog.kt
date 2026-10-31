package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun LevelCompleteDialog(
    levelId: Int,
    moves: Int,
    parMoves: Int,
    stars: Int,
    pipeCoverage: Int,
    hasNextLevel: Boolean,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onLevelSelect: () -> Unit
) {
    val starScale1 = remember { Animatable(0f) }
    val starScale2 = remember { Animatable(0f) }
    val starScale3 = remember { Animatable(0f) }

    LaunchedEffect(stars) {
        if (stars >= 1) {
            starScale1.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (stars >= 2) {
            starScale2.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
        if (stars >= 3) {
            starScale3.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
        }
    }

    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF161B22)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("level_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEVEL COMPLETED!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFD700),
                    letterSpacing = 1.sp
                )

                Text(
                    text = "Level $levelId Cleared",
                    fontSize = 15.sp,
                    color = Color(0xFF8B949E),
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stars row
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    val starScales = listOf(starScale1, starScale2, starScale3)
                    for (i in 1..3) {
                        val isEarned = i <= stars
                        val scale = if (isEarned) starScales[i - 1].value else 1f
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .scale(scale),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = if (isEarned) "Star $i Earned" else "Star $i Empty",
                                tint = if (isEarned) Color(0xFFFFD700) else Color(0xFF30363D),
                                modifier = Modifier.size(if (i == 2) 48.dp else 40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Moves:", color = Color(0xFF8B949E), fontSize = 14.sp)
                            Text(
                                text = "$moves (Par: $parMoves)",
                                color = if (moves <= parMoves) Color(0xFF00E676) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Pipe Coverage:", color = Color(0xFF8B949E), fontSize = 14.sp)
                            Text(
                                text = "$pipeCoverage%",
                                color = if (pipeCoverage == 100) Color(0xFF00E5FF) else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                if (pipeCoverage == 100 && moves <= parMoves) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF238636), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✨ PERFECT FLOW ✨",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action buttons
                if (hasNextLevel) {
                    Button(
                        onClick = onNextLevel,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF238636)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("next_level_button")
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text("Next Level", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onReplay,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("replay_button")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Replay", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = onLevelSelect,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("level_select_dialog_button")
                    ) {
                        Icon(Icons.Default.List, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Levels", color = Color.White)
                    }
                }
            }
        }
    }
}
