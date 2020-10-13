package com.siiri.test

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.siiri.record.audio.AndroidAudioRecorder
import com.siiri.record.video.VideoRecordActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startAudioRecord(view: View) {
        AndroidAudioRecorder.with(this)
            .record()
    }

    fun startVideoRecord(view: View) {
        startActivity(Intent(this, VideoRecordActivity::class.java))
    }
}
