package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Rect
import android.util.DisplayMetrics

/**
 * @author aminography
 */
object DimensionUtils {

    private val displayMetrics: DisplayMetrics by lazy { Resources.getSystem().displayMetrics }

    // https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android?rq=1
    // https://stackoverflow.com/questions/3663665/how-can-i-get-the-current-screen-orientation

    fun getScreenOrientation(): Int { // Added by MM
        val width = screenRectPx.width()
        val height = screenRectPx.height()
        // Numbers shown are corresponding Android constant values
        return when (width == height) {
            true -> Configuration.ORIENTATION_UNDEFINED // 0
            else -> {
                when (width < height) {
                    true -> Configuration.ORIENTATION_PORTRAIT // 1
                    else -> Configuration.ORIENTATION_LANDSCAPE // 2
                }
            }
        }
    }

    /**
     * Returns boundary of the screen in pixels (px)
     */
    private val screenRectPx: Rect
        get() = displayMetrics.run { Rect(0,0, widthPixels, heightPixels) }

    /**
     * Returns boundary of the screen in density independent pixels (dp)
     */
//    val screenRectDp: RectF
//        get() = screenRectPx.run { RectF(0F,0F, right.px2dp, bottom.px2dp) }

    /**
     * Returns boundary of the physical screen including system
     * decor elements (if any) like navigation bar in pixels (px)
     */
//    val Context.physicalScreenRectPx: Rect
//        get() = screenRectPx

    /**
     * Returns boundary of the physical screen, including system decor
     * elements (if any) like navigation bar in density independent pixels (dp)
     */
//    val Context.physicalScreenRectDp: RectF
//        get() = physicalScreenRectPx.run { RectF(0F,0F, right.px2dp, bottom.px2dp) }

    /**
     * Converts any given number from pixels (px) into density independent pixels (dp)
     */
//    val Number.px2dp: Float
//        get() = this.toFloat() / displayMetrics.density

    /**
     * Converts any given number from density independent pixels (dp) into pixels (px)
     */
//    val Number.dp2px: Int
//        get() = (this.toFloat() * displayMetrics.density).roundToInt()
}

