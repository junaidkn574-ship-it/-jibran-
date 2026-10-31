package com.example.data

import com.example.model.LevelData

object LevelsData {
    val ALL_LEVELS: List<LevelData> by lazy {
        Pack1Levels.levels + Pack2Levels.levels + Pack3Levels.levels + Pack4Levels.levels + Pack5Levels.levels
    }
}
