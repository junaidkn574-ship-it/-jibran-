package com.example.data

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("asad_flow_prefs", Context.MODE_PRIVATE)

    var highestUnlockedLevel: Int
        get() = prefs.getInt(KEY_HIGHEST_UNLOCKED, 1)
        set(value) = prefs.edit().putInt(KEY_HIGHEST_UNLOCKED, value).apply()

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND, value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION, value).apply()

    var fullBoardRequired: Boolean
        get() = prefs.getBoolean(KEY_FULL_BOARD, true)
        set(value) = prefs.edit().putBoolean(KEY_FULL_BOARD, value).apply()

    var themeId: String
        get() = prefs.getString(KEY_THEME, "classic_dark") ?: "classic_dark"
        set(value) = prefs.edit().putString(KEY_THEME, value).apply()

    var hintsRemaining: Int
        get() = prefs.getInt(KEY_HINTS, 5)
        set(value) = prefs.edit().putInt(KEY_HINTS, value).apply()

    fun isLevelUnlocked(levelId: Int): Boolean {
        return levelId == 1 || levelId <= highestUnlockedLevel
    }

    fun isLevelCompleted(levelId: Int): Boolean {
        return prefs.getBoolean(KEY_COMPLETED_PREFIX + levelId, false)
    }

    fun getBestMoves(levelId: Int): Int {
        return prefs.getInt(KEY_BEST_MOVES_PREFIX + levelId, 0)
    }

    fun getStars(levelId: Int): Int {
        return prefs.getInt(KEY_STARS_PREFIX + levelId, 0)
    }

    fun saveLevelCompletion(levelId: Int, moves: Int, stars: Int): Boolean {
        val wasAlreadyCompleted = isLevelCompleted(levelId)
        val oldBestMoves = getBestMoves(levelId)
        val oldStars = getStars(levelId)

        val editor = prefs.edit()
        editor.putBoolean(KEY_COMPLETED_PREFIX + levelId, true)
        if (oldBestMoves == 0 || moves < oldBestMoves) {
            editor.putInt(KEY_BEST_MOVES_PREFIX + levelId, moves)
        }
        if (stars > oldStars) {
            editor.putInt(KEY_STARS_PREFIX + levelId, stars)
        }

        // Unlock next level if this is current highest
        if (levelId >= highestUnlockedLevel && levelId < 100) {
            editor.putInt(KEY_HIGHEST_UNLOCKED, levelId + 1)
        }
        editor.apply()
        return !wasAlreadyCompleted
    }

    fun useHint(): Boolean {
        val current = hintsRemaining
        return if (current > 0) {
            hintsRemaining = current - 1
            true
        } else {
            false
        }
    }

    fun addHint() {
        hintsRemaining = hintsRemaining + 1
    }

    fun getTotalStarsEarned(): Int {
        var total = 0
        for (i in 1..100) {
            total += getStars(i)
        }
        return total
    }

    fun getTotalCompletedLevels(): Int {
        var count = 0
        for (i in 1..100) {
            if (isLevelCompleted(i)) count++
        }
        return count
    }

    fun resetAllProgress() {
        prefs.edit().clear().apply()
        highestUnlockedLevel = 1
        hintsRemaining = 5
        soundEnabled = true
        vibrationEnabled = true
        fullBoardRequired = true
    }

    companion object {
        private const val KEY_HIGHEST_UNLOCKED = "highest_unlocked_level"
        private const val KEY_COMPLETED_PREFIX = "level_completed_"
        private const val KEY_BEST_MOVES_PREFIX = "level_best_moves_"
        private const val KEY_STARS_PREFIX = "level_stars_"
        private const val KEY_SOUND = "sound_enabled"
        private const val KEY_VIBRATION = "vibration_enabled"
        private const val KEY_FULL_BOARD = "full_board_required"
        private const val KEY_THEME = "theme_id"
        private const val KEY_HINTS = "hints_remaining"
    }
}
