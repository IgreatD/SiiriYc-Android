package com.siiri.record.utils

import android.graphics.Color
import android.media.AudioFormat
import com.siiri.record.audio.model.AudioChannel
import com.siiri.record.audio.model.AudioSampleRate
import com.siiri.record.audio.model.AudioSource
import omrecorder.AudioSource.Smart
import kotlin.math.sqrt

import omrecorder.AudioSource as OmAudioSource

/**
 * @author: dinglei
 * @date: 2020/10/12 13:37
 */
object RecordUtils {

    fun getMic(
        source: AudioSource,
        channel: AudioChannel,
        sampleRate: AudioSampleRate
    ): OmAudioSource? {
        return Smart(
            source.source,
            AudioFormat.ENCODING_PCM_16BIT,
            channel.getChannel,
            sampleRate.sampleRate
        )
    }

    fun isBrightColor(color: Int): Boolean {
        if (android.R.color.transparent == color) {
            return true
        }
        val rgb = intArrayOf(
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
        val brightness = sqrt(
            rgb[0] * rgb[0] * 0.241 + rgb[1] * rgb[1] * 0.691 + rgb[2] * rgb[2] * 0.068
        ).toInt()
        return brightness >= 200
    }

    fun getDarkerColor(color: Int): Int {
        val factor = 0.8f
        val a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        return Color.argb(
            a,
            (r * factor).toInt().coerceAtLeast(0),
            (g * factor).toInt().coerceAtLeast(0),
            (b * factor).toInt().coerceAtLeast(0)
        )
    }

}