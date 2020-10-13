package com.siiri.record.audio.model

import android.media.AudioFormat

/**
 * @author: dinglei
 * @date: 2020/10/12 13:39
 */
enum class AudioChannel {
    STEREO, MONO;

    val getChannel: Int
        get() = when (this) {
            MONO -> AudioFormat.CHANNEL_IN_MONO
            else -> AudioFormat.CHANNEL_IN_STEREO

        }
}