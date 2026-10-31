package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GamePreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Asad Flow Game", appName)
    }

    @Test
    fun `test game preferences persistence and level progression`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = GamePreferences(context)
        prefs.resetAllProgress()

        assertEquals(1, prefs.highestUnlockedLevel)
        assertTrue(prefs.isLevelUnlocked(1))

        // Save level 1 completion with 3 stars
        val isFirst = prefs.saveLevelCompletion(levelId = 1, moves = 3, stars = 3)
        assertTrue(isFirst)
        assertTrue(prefs.isLevelCompleted(1))
        assertEquals(3, prefs.getBestMoves(1))
        assertEquals(3, prefs.getStars(1))
        assertEquals(2, prefs.highestUnlockedLevel)
        assertTrue(prefs.isLevelUnlocked(2))
    }
}
