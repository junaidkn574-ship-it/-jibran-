package com.example.model

import androidx.compose.ui.graphics.Color

data class FlowColor(
    val id: Int,
    val name: String,
    val mainColor: Color,
    val darkTint: Color,
    val glowColor: Color,
    val toneFrequency: Float
) {
    companion object {
        val ALL: List<FlowColor> = listOf(
            FlowColor(
                id = 0,
                name = "Red",
                mainColor = Color(0xFFFF2A2A),
                darkTint = Color(0x33FF2A2A),
                glowColor = Color(0x80FF4545),
                toneFrequency = 523.25f // C5
            ),
            FlowColor(
                id = 1,
                name = "Blue",
                mainColor = Color(0xFF2979FF),
                darkTint = Color(0x332979FF),
                glowColor = Color(0x80448AFF),
                toneFrequency = 587.33f // D5
            ),
            FlowColor(
                id = 2,
                name = "Green",
                mainColor = Color(0xFF00E676),
                darkTint = Color(0x3300E676),
                glowColor = Color(0x8069F0AE),
                toneFrequency = 659.25f // E5
            ),
            FlowColor(
                id = 3,
                name = "Yellow",
                mainColor = Color(0xFFFFEA00),
                darkTint = Color(0x33FFEA00),
                glowColor = Color(0x80FFFF00),
                toneFrequency = 698.46f // F5
            ),
            FlowColor(
                id = 4,
                name = "Orange",
                mainColor = Color(0xFFFF9100),
                darkTint = Color(0x33FF9100),
                glowColor = Color(0x80FFAB40),
                toneFrequency = 783.99f // G5
            ),
            FlowColor(
                id = 5,
                name = "Cyan",
                mainColor = Color(0xFF00E5FF),
                darkTint = Color(0x3300E5FF),
                glowColor = Color(0x8018FFFF),
                toneFrequency = 880.00f // A5
            ),
            FlowColor(
                id = 6,
                name = "Purple",
                mainColor = Color(0xFFD500F9),
                darkTint = Color(0x33D500F9),
                glowColor = Color(0x80E040FB),
                toneFrequency = 987.77f // B5
            ),
            FlowColor(
                id = 7,
                name = "Magenta",
                mainColor = Color(0xFFFF4081),
                darkTint = Color(0x33FF4081),
                glowColor = Color(0x80FF80AB),
                toneFrequency = 1046.50f // C6
            ),
            FlowColor(
                id = 8,
                name = "Lime",
                mainColor = Color(0xFF76FF03),
                darkTint = Color(0x3376FF03),
                glowColor = Color(0x80B2FF59),
                toneFrequency = 1174.66f // D6
            ),
            FlowColor(
                id = 9,
                name = "White",
                mainColor = Color(0xFFF5F5F5),
                darkTint = Color(0x33FFFFFF),
                glowColor = Color(0x80FFFFFF),
                toneFrequency = 1318.51f // E6
            )
        )

        fun getById(id: Int): FlowColor = ALL.getOrElse(id) { ALL[id % ALL.size] }
    }
}
