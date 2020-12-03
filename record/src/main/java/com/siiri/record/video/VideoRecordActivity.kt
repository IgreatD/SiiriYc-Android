package com.siiri.record.video

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import com.otaliastudios.cameraview.CameraException
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.VideoResult
import com.siiri.record.R
import com.siiri.record.utils.TimeUtils
import kotlinx.android.synthetic.main.activity_video_record.*
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.concurrent.timerTask


/**
 * @author: dinglei
 * @date: 2020/10/11 15:19
 */
class VideoRecordActivity : AppCompatActivity() {

    companion object {
        const val VIDEO_RECORD_RESULT_PATH = "video_record_result_path"
    }

    private lateinit var cameraView: CameraView

    private lateinit var mRecordButton: AppCompatImageView

    private lateinit var mTvDuration: TextView

    private var isRecording = false

    private val restartButton: AppCompatImageView by lazy {
        findViewById<AppCompatImageView>(
            R.id.restartButton
        )
    }

    private val successButton: AppCompatImageView by lazy {
        findViewById<AppCompatImageView>(
            R.id.successButton
        )
    }

    private var mRecordDuration = 0L

    private val maxDuration = 1000 * 30

    private var mTimer: Timer? = null

    private var mVideoResult: VideoResult? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_record)
        cameraView = findViewById(R.id.camera)
        mRecordButton = findViewById(R.id.recordButton)
        mTvDuration = findViewById(R.id.tvDuration)
        restartButton.setOnClickListener {
            try {
                deleteFile(mVideoResult?.file?.path)
            } catch (e: Exception) {

            }
            mRecordButton.visibility = View.VISIBLE
            toggleRecording()
        }
        successButton.setOnClickListener {
            if (mVideoResult != null) {
                val intent = Intent(this, VideoPlayActivity::class.java)
                intent.putExtra("url", mVideoResult?.file?.path)
                startActivity(intent)
            } else {
                Toast.makeText(this, "视频录制出错，重新录制", Toast.LENGTH_SHORT).show()
                toggleRecording()
            }

        }
        closeButton.setOnClickListener {
            try {
                deleteFile(mVideoResult?.file?.path)
            } catch (e: Exception) {

            }
            finish()
        }
        checkButton.setOnClickListener {
            selectVideo()
        }
        mRecordButton.setOnClickListener {
            if (cameraView.isTakingVideo) {
                stopRecording()
            } else {
                toggleRecording()
            }
        }
        cameraView.setLifecycleOwner(this)
        cameraView.addCameraListener(object : CameraListener() {

            override fun onCameraError(exception: CameraException) {
                isRecording = false
            }

            override fun onVideoTaken(result: VideoResult) {
                isRecording = false
                mVideoResult = result
            }

            override fun onVideoRecordingEnd() {
                isRecording = false
                destroyTimer()
                stopRecording()
            }

            override fun onVideoRecordingStart() {
                isRecording = true
                updateTimer()
            }
        })
    }

    private fun selectVideo() {
        val intent = Intent()
        intent.putExtra(VIDEO_RECORD_RESULT_PATH, mVideoResult?.file?.path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun updateTimer() {
        mTimer = Timer()
        mTimer?.schedule(timerTask {
            runOnUiThread {
                mRecordDuration++
                mTvDuration.text =
                    TimeUtils.secondToTime(
                        mRecordDuration
                    )
            }
        }, 1000, 1000L)
    }

    @SuppressLint("SetTextI18n")
    private fun toggleRecording() {
        if (isRecording) {
            stopRecording()
        }
        cameraView.takeVideoSnapshot(
            File(filesDir, System.currentTimeMillis().toString() + ".mp4"),
            maxDuration
        )
        restartButton.visibility = View.INVISIBLE
        successButton.visibility = View.INVISIBLE
        checkButton.visibility = View.INVISIBLE
        mRecordButton.visibility = View.VISIBLE
        mRecordButton.setImageResource(R.drawable.aar_ic_stop)
        mTvDuration.text = "00:00"
        mRecordDuration = 0
    }

    private fun stopRecording() {
        cameraView.stopVideo()
        mRecordButton.setImageResource(R.drawable.aar_ic_rec)
        restartButton.visibility = View.VISIBLE
        successButton.visibility = View.VISIBLE
        mRecordButton.visibility = View.INVISIBLE
        checkButton.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        cameraView.open()
    }

    override fun onPause() {
        super.onPause()
        cameraView.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView.destroy()
        destroyTimer()
    }

    private fun destroyTimer() {
        mTimer?.cancel()
        mTimer?.purge()
        mTimer = null
    }

}