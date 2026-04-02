package com.skilltok.app

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SoundManager {
    private var toneGenerator: ToneGenerator? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isMuted = false

    private fun ensureToneGenerator() {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
            } catch (e: Exception) {
                // ToneGenerator unavailable on some devices; silently ignore
            }
        }
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun playLike() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                // Two quick high-pitched tones like a "pop" – heart sound
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 60)
                Thread.sleep(80)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 50)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun playComment() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun playEnroll() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                // Rising melody: three ascending tones
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 80)
                Thread.sleep(100)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_S_X4, 100)
                Thread.sleep(120)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_PBX_SLS, 150)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun playSave() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_NETWORK_CALLWAITING, 70)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun playSeek() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 40)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun playComplete() {
        if (isMuted) return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                ensureToneGenerator()
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 80)
                Thread.sleep(100)
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 120)
            } catch (e: Exception) { /* ignore */ }
        }
    }

    fun release() {
        try { toneGenerator?.release() } catch (e: Exception) { /* ignore */ }
        toneGenerator = null
    }
}
