package com.skilltok.app

import android.media.AudioManager
import android.media.ToneGenerator

enum class SkillTokSoundEffect {
    Enroll,
    Like,
    Comment
}

object SkillTokSoundPlayer {
    private val toneGenerator by lazy { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70) }

    @Synchronized
    fun play(effect: SkillTokSoundEffect) {
        when (effect) {
            SkillTokSoundEffect.Enroll -> {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 140)
            }

            SkillTokSoundEffect.Like -> {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 90)
            }

            SkillTokSoundEffect.Comment -> {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_PROMPT, 110)
            }
        }
    }
}
