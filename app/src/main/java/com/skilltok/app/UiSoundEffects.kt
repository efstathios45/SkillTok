package com.skilltok.app

import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppUiSound {
    Enroll,
    Like,
    Comment
}

object UiSoundEffects {
    private val scope = CoroutineScope(Dispatchers.Main.immediate)

    fun play(sound: AppUiSound) {
        scope.launch {
            runCatching {
                val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 75)
                when (sound) {
                    AppUiSound.Enroll -> tone.startTone(ToneGenerator.TONE_PROP_ACK, 140)
                    AppUiSound.Like -> tone.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
                    AppUiSound.Comment -> tone.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
                }
                delay(220)
                tone.release()
            }
        }
    }
}
