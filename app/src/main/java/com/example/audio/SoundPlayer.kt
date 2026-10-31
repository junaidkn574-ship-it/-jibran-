package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundPlayer {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    fun playTone(frequency: Float, durationMs: Int = 180) {
        if (!isEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val numSamples = (durationMs * sampleRate) / 1000
                val samples = ShortArray(numSamples)
                val attackSamples = (sampleRate * 0.015).toInt()
                val decaySamples = (sampleRate * 0.05).toInt()

                for (i in 0 until numSamples) {
                    val time = i.toDouble() / sampleRate
                    var amp = sin(2.0 * Math.PI * frequency * time)
                    // Envelope shaping to prevent clicking
                    val envelope = when {
                        i < attackSamples -> i.toDouble() / attackSamples
                        i > numSamples - decaySamples -> (numSamples - i).toDouble() / decaySamples
                        else -> 1.0
                    }
                    samples[i] = (amp * envelope * Short.MAX_VALUE * 0.6).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(samples.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(samples, 0, samples.size)
                track.play()
                // Auto release after sound finishes
                scope.launch {
                    kotlinx.coroutines.delay((durationMs + 50).toLong())
                    track.release()
                }
            } catch (_: Exception) {
                // Ignore audio track init failures on unsupported platforms
            }
        }
    }

    fun playVictoryFanfare() {
        if (!isEnabled) return
        scope.launch {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f) // C5 - E5 - G5 - C6
            for (note in notes) {
                playTone(note, 140)
                kotlinx.coroutines.delay(100)
            }
        }
    }

    fun playStep() {
        if (!isEnabled) return
        playTone(330f, 40)
    }

    fun playUndo() {
        if (!isEnabled) return
        playTone(260f, 80)
    }
}
