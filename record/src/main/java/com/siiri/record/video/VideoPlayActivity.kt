package com.siiri.record.video

import android.os.Bundle
import android.view.View
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.siiri.record.R
import kotlinx.android.synthetic.main.activity_detail_player.*


/**
 * @author: dinglei
 * @date: 2020/8/20 17:13
 */
class VideoPlayActivity : GSYBaseActivityDetail<StandardGSYVideoPlayer>() {

    private val mPlayerView by lazy { findViewById<StandardGSYVideoPlayer>(R.id.videoView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_player)
        closeButton.setOnClickListener {
            onBackPressed()
        }
        mPlayerView.titleTextView.visibility = View.GONE
        mPlayerView.backButton.visibility = View.GONE

        initVideoBuilderMode()

        mPlayerView.startButton.performClick()
    }

    override fun clickForFullScreen() {

    }

    override fun getDetailOrientationRotateAuto(): Boolean {
        return false
    }

    override fun getGSYVideoPlayer(): StandardGSYVideoPlayer {
        return mPlayerView
    }

    override fun onBackPressed() {
        if (isPlay) {
            gsyVideoPlayer.currentPlayer.release()
        }
        if (orientationUtils != null) orientationUtils.releaseListener()
        finish()
    }

    override fun getGSYVideoOptionBuilder(): GSYVideoOptionBuilder {
        val url = intent.extras?.getString("url")
        return GSYVideoOptionBuilder()
            .setUrl(url)
            .setCacheWithPlay(true)
            .setVideoTitle(" ")
            .setFullHideStatusBar(true)
            .setIsTouchWiget(true)
            .setRotateViewAuto(true)
            .setLockLand(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(false)
            .setStartAfterPrepared(true)
            .setSeekRatio(1f)
    }

}