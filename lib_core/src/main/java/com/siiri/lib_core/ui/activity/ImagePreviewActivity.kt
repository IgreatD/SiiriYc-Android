package com.siiri.lib_core.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davemorrissey.labs.subscaleview.ImageSource
import com.jess.arms.http.imageloader.glide.GlideArms
import com.siiri.lib_core.R
import kotlinx.android.synthetic.main.activity_image_preview.*

/**
 * @author: dinglei
 * @date: 2020/10/16 16:34
 */
class ImagePreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        val url = intent.extras?.getString("url", "") ?: ""
        if (url.isNotEmpty())
            GlideArms.with(this)
                .load(url)
                .into(photo_view)
    }

}