package com.skilltok.app

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {
    private val scope = CoroutineScope(Dispatchers.IO)
    private const val SAMPLE_RATE = 44100

    fun playLikeSound(context: Context) {
        scope.launch { playTone(frequency = 880.0, durationMs = 80, volume = 0.3f, riseFrequency = 1320.0) }
    }

    fun playCommentSound(context: Context) {
        scope.launch { playTone(frequency = 660.0, durationMs = 100, volume = 0.25f, riseFrequency = 880.0) }
    }

    fun playEnrollSound(context: Context) {
        scope.launch {
            playTone(frequency = 523.0, durationMs = 80, volume = 0.3f)
            playTone(frequency = 659.0, durationMs = 80, volume = 0.3f)
            playTone(frequency = 784.0, durationMs = 120, volume = 0.35f)
        }
    }

    fun playSaveSound(context: Context) {
        scope.launch { playTone(frequency = 1047.0, durationMs = 60, volume = 0.2f, riseFrequency = 784.0) }
    }

    fun playSeekSound(context: Context) {
        scope.launch { playTone(frequency = 440.0, durationMs = 40, volume = 0.15f) }
    }

    private fun playTone(
        frequency: Double,
        durationMs: Int,
        volume: Float = 0.3f,
        riseFrequency: Double? = null
    ) {
        try {
            val numSamples = (SAMPLE_RATE * durationMs / 1000.0).toInt()
            val samples = ShortArray(numSamples)
            val fadeLength = (numSamples * 0.15).toInt().coerceAtLeast(1)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = if (riseFrequency != null) {
                    frequency + (riseFrequency - frequency) * progress
                } else frequency

                var sample = sin(2.0 * PI * freq * t)

                val envelope = when {
                    i < fadeLength -> i.toFloat() / fadeLength
                    i > numSamples - fadeLength -> (numSamples - i).toFloat() / fadeLength
                    else -> 1.0f
                }
                sample *= envelope * volume

                samples[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            val bufferSize = samples.size * 2
            val audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(SAMPLE_RATE)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.stop()
            audioTrack.release()
        } catch (_: Exception) { }
    }
}
