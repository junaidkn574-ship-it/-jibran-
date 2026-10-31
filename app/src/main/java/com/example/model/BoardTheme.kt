package com.example.model

import androidx.compose.ui.graphics.Color

data class BoardTheme(
    val id: String,
    val name: String,
    val boardBackground: Color,
    val gridBorderColor: Color,
    val cellEmptyBackground: Color,
    val screenBackground: Color
) {
    companion object {
        val CLASSIC_DARK = BoardTheme(
            id = "classic_dark",
            name = "Classic Dark",
            boardBackground = Color(0xFF0F1115),
            gridBorderColor = Color(0xFF262A34),
            cellEmptyBackground = Color(0xFF14171E),
            screenBackground = Color(0xFF0A0C0F)
        )

        val CYBER_NEON = BoardTheme(
            id = "cyber_neon",
            name = "Cyber Neon",
            boardBackground = Color(0xFF080D1A),
            gridBorderColor = Color(0xFF1D3557),
            cellEmptyBackground = Color(0xFF0F172A),
            screenBackground = Color(0xFF050811)
        )

        val DEEP_OCEAN = BoardTheme(
            id = "deep_ocean",
            name = "Deep Ocean",
            boardBackground = Color(0xFF091E28),
            gridBorderColor = Color(0xFF154050),
            cellEmptyBackground = Color(0xFF0D2836),
            screenBackground = Color(0xFF05131A)
        )

        val AMOLED_BLACK = BoardTheme(
            id = "amoled_black",
            name = "AMOLED Black",
            boardBackground = Color(0xFF000000),
            gridBorderColor = Color(0xFF202020),
            cellEmptyBackground = Color(0xFF080808),
            screenBackground = Color(0xFF000000)
        )

        val ALL = listOf(CLASSIC_DARK, CYBER_NEON, DEEP_OCEAN, AMOLED_BLACK)

        fun getById(id: String): BoardTheme = ALL.find { it.id == id } ?: CLASSIC_DARK
    }
}
