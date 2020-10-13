package com.siiri.record.video

import android.os.Bundle
import android.view.View
import com.shuyu.gsyvideoplayer.GSYBaseActivityDetail
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.siiri.record.R


/**
 * @author: dinglei
 * @date: 2020/8/20 17:13
 */
class VideoPlayActivity : GSYBaseActivityDetail<StandardGSYVideoPlayer>() {

    private val mPlayerView by lazy { findViewById<StandardGSYVideoPlayer>(R.id.detail_player) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_player)

        mPlayerView.titleTextView.visibility = View.GONE
        mPlayerView.backButton.visibility = View.GONE

        initVideoBuilderMode()

        showFull()

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

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        onBackPressed()
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
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setSeekRatio(1f)
    }

}