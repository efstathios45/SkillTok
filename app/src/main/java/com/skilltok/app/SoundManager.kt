package com.skilltok.app

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

class SoundManager(context: Context) {
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(5)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundMap = mutableMapOf<String, Int>()
    private val package_name = context.packageName
    private val resources = context.resources

    init {
        loadSound("like")
        loadSound("save")
        loadSound("comment")
        loadSound("enroll")
    }

    private fun loadSound(name: String) {
        val resId = resources.getIdentifier(name, "raw", package_name)
        if (resId != 0) {
            soundMap[name] = soundPool.load(resources.openRawResourceFd(resId), 1)
        } else {
            Log.w("SoundManager", "Sound file $name.mp3 not found in res/raw")
        }
    }

    fun playLikeSound() = play("like")
    fun playSaveSound() = play("save")
    fun playCommentSound() = play("comment")
    fun playEnrollSound() = play("enroll")

    private fun play(name: String) {
        soundMap[name]?.let { id ->
            soundPool.play(id, 1f, 1f, 1, 0, 1f)
        }
    }
}
