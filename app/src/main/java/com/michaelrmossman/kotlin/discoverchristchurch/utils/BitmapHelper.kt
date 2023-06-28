package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapHelper {
    /**
     * Demonstrates converting a [Drawable] to a [BitmapDescriptor], for use as a marker icon. Taken from ApiDemos on GitHub:
     * https://github.com/googlemaps/android-samples/blob/master/ApiDemos/kotlin/app/src/main/java/com/example/kotlindemos/MarkerDemoActivity.kt
     */
    fun vectorToBitmap(
        context: Context, @DrawableRes drawableId: Int, @ColorInt colorId: Int
    ) : BitmapDescriptor {
        val drawable =
            ResourcesCompat.getDrawable(context.resources, drawableId,null)

        if (drawable == null) {
            Log.e("BitmapHelper","Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(drawable, colorId)
        drawable.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
