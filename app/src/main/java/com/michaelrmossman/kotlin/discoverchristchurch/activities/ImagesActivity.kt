package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityImagesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FULLSCREEN_IMAGE_ID_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FULLSCREEN_IMAGE_LANDSCAPE_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FULLSCREEN_IMAGE_NAME_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isXLargeScreen

// Displays full-screen image for any full-size image (see SysUtils.goFullscreen())
class ImagesActivity: AppCompatActivity() {

    lateinit var binding: ActivityImagesBinding

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = when (intent?.extras?.getBoolean(FULLSCREEN_IMAGE_LANDSCAPE_EXTRA,
                true)) {
            true -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            else -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }

        binding = ActivityImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lifecycleOwner = this

        binding.imagesActivity = this

        intent?.extras?.let {
            binding.imageTitle =
                it.getString(FULLSCREEN_IMAGE_NAME_EXTRA)

            val imageId = it.getInt(FULLSCREEN_IMAGE_ID_EXTRA)
            GlideApp.with(this)
                .load(imageId).into(binding.fullscreenImage)
        }
    }
}
