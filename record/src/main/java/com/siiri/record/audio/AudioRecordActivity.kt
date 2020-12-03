package com.siiri.record.audio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.siiri.record.R
import com.siiri.record.audio.model.AudioChannel
import com.siiri.record.audio.model.AudioSampleRate
import com.siiri.record.audio.model.AudioSource
import com.siiri.record.utils.RecordUtils
import com.siiri.record.utils.TimeUtils
import omrecorder.AudioChunk
import omrecorder.OmRecorder
import omrecorder.PullTransport
import omrecorder.Recorder
import java.io.File
import java.util.*
import kotlin.concurrent.timerTask

/**
 * @author: dinglei
 * @date: 2020/10/12 13:21
 */
class AudioRecordActivity : AppCompatActivity(), PullTransport.OnAudioChunkPulledListener,
    MediaPlayer.OnCompletionListener {

    companion object {
        const val AUDIO_RECORD_RESULT_PATH = "audio_record_result_path"
    }

    private var filePath: String? = null
    private var source: AudioSource? = null
    private var channel: AudioChannel? = null
    private var sampleRate: AudioSampleRate? = null
    private var color = 0
    private var autoStart = false
    private var autoPlay = false
    private var keepDisplayOn = false
    private var player: ExoPlayer? = null
    private var recorder: Recorder? = null
    private var saveMenuItem: MenuItem? = null

    private var isRecording = false
    private var recorderSecondsElapsed = 0
    private var playerSecondsElapsed = 0
    private var timer: Timer? = null

    private val contentLayout by lazy { findViewById<RelativeLayout>(R.id.content) }
    private val statusView by lazy { findViewById<TextView>(R.id.status) }
    private val timerView by lazy { findViewById<TextView>(R.id.timer) }
    private val restartView by lazy { findViewById<AppCompatImageButton>(R.id.restart) }
    private val recordView by lazy { findViewById<AppCompatImageButton>(R.id.record) }
    private val playView by lazy { findViewById<AppCompatImageButton>(R.id.play) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)
        initState(savedInstanceState)
        initView()
        initClick()
        initActionBar()
        initData()
    }

    private fun initData() {
        if (autoPlay && filePath.isNullOrEmpty().not()) {
            startPlaying()
            saveMenuItem?.isVisible = false
            recordView.visibility = View.INVISIBLE
        }
    }

    private fun initActionBar() {
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.elevation = 0f
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(RecordUtils.getDarkerColor(color))
        )
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(this, R.drawable.aar_ic_clear)
        )
    }

    private fun initClick() {
        recordView.setOnClickListener {
            toggleRecording()
        }
        restartView.setOnClickListener {
            restartRecording()
        }
        playView.setOnClickListener {
            togglePlaying()
        }
    }

    private fun initView() {
        contentLayout.setBackgroundColor(RecordUtils.getDarkerColor(color))
        restartView.visibility = View.INVISIBLE
        playView.visibility = View.INVISIBLE
        if (RecordUtils.isBrightColor(color)) {
            ContextCompat.getDrawable(this, R.drawable.aar_ic_clear)?.setColorFilter(
                Color.BLACK,
                PorterDuff.Mode.SRC_ATOP
            )
            ContextCompat.getDrawable(this, R.drawable.aar_ic_check)
                ?.setColorFilter(
                    Color.BLACK,
                    PorterDuff.Mode.SRC_ATOP
                )
            statusView.setTextColor(Color.BLACK)
            timerView.setTextColor(Color.BLACK)
            restartView.setColorFilter(Color.BLACK)
            recordView.setColorFilter(Color.BLACK)
            playView.setColorFilter(Color.BLACK)
        }
    }

    private fun initState(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString(AndroidAudioRecorder.EXTRA_FILE_PATH)
            source =
                savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SOURCE) as AudioSource?
            channel =
                savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_CHANNEL) as AudioChannel?
            sampleRate =
                savedInstanceState.getSerializable(AndroidAudioRecorder.EXTRA_SAMPLE_RATE) as AudioSampleRate?
            color = savedInstanceState.getInt(AndroidAudioRecorder.EXTRA_COLOR)
            autoStart = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_AUTO_START)
            autoPlay = savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_AUTO_PLAY)
            keepDisplayOn =
                savedInstanceState.getBoolean(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON)
        } else {
            filePath = intent.getStringExtra(AndroidAudioRecorder.EXTRA_FILE_PATH)
            source = intent.getSerializableExtra(AndroidAudioRecorder.EXTRA_SOURCE) as AudioSource
            channel =
                intent.getSerializableExtra(AndroidAudioRecorder.EXTRA_CHANNEL) as AudioChannel
            sampleRate =
                intent.getSerializableExtra(AndroidAudioRecorder.EXTRA_SAMPLE_RATE) as AudioSampleRate
            color = intent.getIntExtra(
                AndroidAudioRecorder.EXTRA_COLOR,
                Color.BLACK
            )
            autoStart = intent.getBooleanExtra(AndroidAudioRecorder.EXTRA_AUTO_START, false)
            autoPlay = intent.getBooleanExtra(AndroidAudioRecorder.EXTRA_AUTO_PLAY, false)
            keepDisplayOn =
                intent.getBooleanExtra(AndroidAudioRecorder.EXTRA_KEEP_DISPLAY_ON, false)
        }

        if (keepDisplayOn) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (autoStart && !isRecording) {
            toggleRecording()
        }
    }

    override fun onPause() {
        restartRecording()
        super.onPause()
    }

    override fun onDestroy() {
        restartRecording()
        setResult(Activity.RESULT_CANCELED)
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(AndroidAudioRecorder.EXTRA_FILE_PATH, filePath)
        outState.putInt(AndroidAudioRecorder.EXTRA_COLOR, color)
        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.aar_audio_recorder, menu)
        saveMenuItem = menu.findItem(R.id.action_save)
        saveMenuItem?.icon = ContextCompat.getDrawable(this, R.drawable.aar_ic_check)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val i = item.itemId
        if (i == android.R.id.home) {
            if (!autoPlay)
                try {
                    deleteFile(filePath)
                } catch (e: java.lang.Exception) {

                }
            finish()
        } else if (i == R.id.action_save) {
            selectAudio()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAudioChunkPulled(audioChunk: AudioChunk) {

    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        stopPlaying()
    }

    private fun selectAudio() {
        stopRecording()
        val intent = Intent()
        intent.putExtra(AUDIO_RECORD_RESULT_PATH, filePath)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun toggleRecording() {
        stopPlaying()
        recordView?.handler?.postDelayed({
            when {
                isRecording -> pauseRecording()
                else -> resumeRecording()
            }
        }, 100)
    }

    private fun togglePlaying() {
        pauseRecording()
        playView?.handler?.postDelayed({
            if (isPlaying()) {
                stopPlaying()
            } else {
                startPlaying()
            }
        }, 100)
    }

    @SuppressLint("SetTextI18n")
    private fun restartRecording() {
        when {
            isRecording -> {
                stopRecording()
            }
            isPlaying() -> {
                stopPlaying()
            }
        }
        saveMenuItem?.isVisible = false
        statusView.visibility = View.INVISIBLE
        restartView.visibility = View.INVISIBLE
        playView.visibility = View.INVISIBLE
        recordView.setImageResource(R.drawable.aar_ic_rec)
        timerView.text = "00:00"
        recorderSecondsElapsed = 0
        playerSecondsElapsed = 0
    }

    @SuppressLint("SetTextI18n")
    private fun resumeRecording() {
        isRecording = true
        saveMenuItem?.isVisible = false
        statusView.text = getString(R.string.arr_recording)
        statusView.visibility = View.VISIBLE
        restartView.visibility = View.INVISIBLE
        recordView.setImageResource(R.drawable.aar_ic_pause)
        playView.setImageResource(R.drawable.aar_ic_play)
        if (recorder == null &&
            filePath.isNullOrEmpty().not() &&
            source != null &&
            channel != null &&
            sampleRate != null
        ) {
            timerView.text = "00:00"
            recorder = OmRecorder.wav(
                PullTransport.Default(
                    RecordUtils.getMic(source!!, channel!!, sampleRate!!), this
                ), File(filePath!!)
            )
        }
        recorder?.resumeRecording()
        startTimer()
    }

    private fun startTimer() {
        stopTimer()
        timer = Timer()
        timer?.scheduleAtFixedRate(timerTask {
            updateTimer()
        }, 0, 1000)
    }

    private fun updateTimer() {
        runOnUiThread {
            when {
                isRecording -> {
                    recorderSecondsElapsed++
                    timerView.text = TimeUtils.secondToTime(recorderSecondsElapsed.toLong())
                }
                isPlaying() -> {
                    playerSecondsElapsed++
                    timerView.text = TimeUtils.secondToTime(playerSecondsElapsed.toLong())
                }
            }
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timer?.purge()
        timer = null
    }

    @SuppressLint("SetTextI18n")
    private fun startPlaying() {
        try {
            stopRecording()
            player = SimpleExoPlayer.Builder(this)
                .setUseLazyPreparation(true)
                .build()
            if (player != null) {
                val defaultDataSourceFactory = DefaultDataSourceFactory(
                    this,
                    Util.getUserAgent(this, this.application.packageName)
                )
                val factory = ProgressiveMediaSource.Factory(
                    defaultDataSourceFactory
                ).createMediaSource(MediaItem.fromUri(Uri.parse(filePath)))
                player?.setMediaSource(factory)
                player?.prepare()
                player?.playWhenReady = true
                timerView.text = "00:00"
                statusView.visibility = View.VISIBLE
                playView.setImageResource(R.drawable.aar_ic_stop)
                playerSecondsElapsed = 0
                player?.addListener(object : Player.EventListener {
                    override fun onPlayerError(error: ExoPlaybackException) {
                        statusView.text = "播放出错"
                    }

                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_ENDED -> statusView.text = "播放完成"
                            Player.STATE_READY -> statusView.text = getString(R.string.aar_playing)
                            Player.STATE_IDLE -> statusView.text = "音频文件错误"
                            else -> {
                                statusView.text = "准备播放"
                            }
                        }
                    }
                })
                startTimer()
            }
        } catch (e: Exception) {
        }

    }

    private fun isPlaying(): Boolean {
        return try {
            player != null && player?.isPlaying == true && !isRecording
        } catch (e: Exception) {
            false
        }
    }

    private fun stopPlaying() {
        statusView.text = ""
        statusView.visibility = View.INVISIBLE
        playView.setImageResource(R.drawable.aar_ic_play)
        try {
            player?.stop()
            player?.release()
        } catch (e: Exception) {
        }
        stopTimer()
    }

    private fun pauseRecording() {
        isRecording = false
        if (isFinishing.not()) {
            saveMenuItem?.isVisible = true
        }
        statusView.setText(R.string.aar_paused)
        statusView.visibility = View.VISIBLE
        restartView.visibility = View.VISIBLE
        playView.visibility = View.VISIBLE
        recordView.setImageResource(R.drawable.aar_ic_rec)
        playView.setImageResource(R.drawable.aar_ic_play)
        recorder?.pauseRecording()

        stopTimer()
    }

    private fun stopRecording() {
        recorderSecondsElapsed = 0
        recorder?.stopRecording()
        recorder = null
        stopTimer()
    }


}