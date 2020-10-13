package com.siiri.record.audio

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Environment
import com.siiri.record.audio.model.AudioChannel
import com.siiri.record.audio.model.AudioSampleRate
import com.siiri.record.audio.model.AudioSource

/**
 * @author: dinglei
 * @date: 2020/10/12 13:46
 */
class AndroidAudioRecorder(private val mActivity: Activity) {

    companion object {
        const val EXTRA_FILE_PATH = "filePath"
        const val EXTRA_COLOR = "color"
        const val EXTRA_AUTO_PLAY = "autoPlay"
        const val EXTRA_SOURCE = "source"
        const val EXTRA_CHANNEL = "channel"
        const val EXTRA_SAMPLE_RATE = "sampleRate"
        const val EXTRA_AUTO_START = "autoStart"
        const val EXTRA_KEEP_DISPLAY_ON = "keepDisplayOn"

        fun with(activity: Activity): AndroidAudioRecorder {
            return AndroidAudioRecorder(activity)
        }
    }

    private var requestCode = 0
    private var color = Color.parseColor("#546E7A")
    private var source = AudioSource.MIC
    private var channel = AudioChannel.STEREO
    private var sampleRate = AudioSampleRate.HZ_44100
    private var autoStart = false
    private var autoPlay = false
    private var keepDisplayOn = false

    private var filePath =
        Environment.getExternalStorageDirectory().path + "/" + System.currentTimeMillis() + ".wav"

    fun setRequestCode(requestCode: Int): AndroidAudioRecorder {
        this.requestCode = requestCode
        return this
    }

    fun setColor(color: Int): AndroidAudioRecorder {
        this.color = color
        return this
    }

    fun setFilePath(filePath: String): AndroidAudioRecorder {
        this.filePath = filePath
        return this
    }

    fun setSource(source: AudioSource): AndroidAudioRecorder {
        this.source = source
        return this
    }

    fun setChannel(channel: AudioChannel): AndroidAudioRecorder {
        this.channel = channel
        return this
    }

    fun setSampleRate(sampleRate: AudioSampleRate): AndroidAudioRecorder {
        this.sampleRate = sampleRate
        return this
    }

    fun setAutoPlay(autoPlay: Boolean): AndroidAudioRecorder {
        this.autoPlay = autoPlay
        return this
    }

    fun setAutoStart(autoStart: Boolean): AndroidAudioRecorder {
        this.autoStart = autoStart
        return this
    }

    fun setKeepDisplayOn(keepDisplayOn: Boolean): AndroidAudioRecorder {
        this.keepDisplayOn = keepDisplayOn
        return this
    }

    fun record() {
        val intent = Intent(
            mActivity,
            AudioRecordActivity::class.java
        )
        intent.putExtra(EXTRA_FILE_PATH, filePath)
        intent.putExtra(EXTRA_COLOR, color)
        intent.putExtra(EXTRA_SOURCE, source)
        intent.putExtra(EXTRA_CHANNEL, channel)
        intent.putExtra(EXTRA_SAMPLE_RATE, sampleRate)
        intent.putExtra(EXTRA_AUTO_START, autoStart)
        intent.putExtra(EXTRA_AUTO_PLAY, autoPlay)
        intent.putExtra(EXTRA_KEEP_DISPLAY_ON, keepDisplayOn)
        mActivity.startActivityForResult(intent, requestCode)
    }

}