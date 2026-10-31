package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GamePreferences
import com.example.data.LevelsData
import com.example.model.LevelData

@Composable
fun LevelSelectScreen(
    prefs: GamePreferences,
    onLevelSelected: (Int) -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIdx by remember { mutableIntStateOf(0) }
    val tabs = listOf("5x5 Starter", "6x6 Regular", "7x7 Medium", "8x8 Advanced", "9x9 Master", "All 100")

    val filteredLevels: List<LevelData> = remember(selectedTabIdx) {
        when (selectedTabIdx) {
            0 -> LevelsData.ALL_LEVELS.filter { it.size == 5 }
            1 -> LevelsData.ALL_LEVELS.filter { it.size == 6 }
            2 -> LevelsData.ALL_LEVELS.filter { it.size == 7 }
            3 -> LevelsData.ALL_LEVELS.filter { it.size == 8 }
            4 -> LevelsData.ALL_LEVELS.filter { it.size == 9 }
            else -> LevelsData.ALL_LEVELS
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .statusBarsPadding()
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClicked,
                modifier = Modifier.testTag("level_select_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "SELECT LEVEL",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            // Total Stars Counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(Color(0xFF161B22), RoundedCornerShape(14.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${prefs.getTotalStarsEarned()}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // Pack Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = Color(0xFF161B22),
            contentColor = Color(0xFF00E5FF),
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
                    color = Color(0xFF00E5FF),
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIdx == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIdx == index) Color(0xFF00E5FF) else Color(0xFF8B949E)
                        )
                    }
                )
            }
        }

        // 100 Levels Grid
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 72.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("levels_grid")
        ) {
            items(filteredLevels, key = { it.id }) { level ->
                val isUnlocked = prefs.isLevelUnlocked(level.id)
                val isCompleted = prefs.isLevelCompleted(level.id)
                val stars = prefs.getStars(level.id)
                val bestMoves = prefs.getBestMoves(level.id)

                LevelGridItem(
                    level = level,
                    isUnlocked = isUnlocked,
                    isCompleted = isCompleted,
                    stars = stars,
                    bestMoves = bestMoves,
                    onClick = {
                        if (isUnlocked) {
                            onLevelSelected(level.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelGridItem(
    level: LevelData,
    isUnlocked: Boolean,
    isCompleted: Boolean,
    stars: Int,
    bestMoves: Int,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> Color(0xFF1F2937)
                isUnlocked -> Color(0xFF161B22)
                else -> Color(0xFF0D1117)
            }
        ),
        modifier = Modifier
            .size(76.dp)
            .border(
                width = if (isCompleted) 1.5.dp else 1.dp,
                color = when {
                    isCompleted -> Color(0xFF238636)
                    isUnlocked -> Color(0xFF30363D)
                    else -> Color(0xFF21262D)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .alpha(if (isUnlocked) 1f else 0.45f)
            .clickable(enabled = isUnlocked, onClick = onClick)
            .testTag("level_item_${level.id}")
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (!isUnlocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color(0xFF8B949E),
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${level.id}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (isCompleted) {
                        // Stars display
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (s in 1..3) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (s <= stars) Color(0xFFFFD700) else Color(0xFF30363D),
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "${level.size}x${level.size}",
                            fontSize = 10.sp,
                            color = Color(0xFF8B949E)
                        )
                    }
                }
            }
        }
    }
}
