package com.skilltok.app

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember

enum class AppSoundEvent {
    Enroll,
    Like,
    Comment
}

class AppSoundFeedbackPlayer {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 85)

    fun play(event: AppSoundEvent) {
        val tone = when (event) {
            AppSoundEvent.Enroll -> ToneGenerator.TONE_PROP_ACK
            AppSoundEvent.Like -> ToneGenerator.TONE_PROP_BEEP2
            AppSoundEvent.Comment -> ToneGenerator.TONE_PROP_PROMPT
        }
        toneGenerator.startTone(tone, 90)
    }

    fun release() {
        toneGenerator.release()
    }
}

@Composable
fun rememberSoundFeedbackPlayer(): AppSoundFeedbackPlayer {
    val player = remember { AppSoundFeedbackPlayer() }
    DisposableEffect(player) {
        onDispose { player.release() }
    }
    return player
}
