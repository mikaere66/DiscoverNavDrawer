package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.maps.android.clustering.ClusterManager
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.android.gms.maps.model.MarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.BitmapHelper.vectorToBitmap

class ColorSingleIconRenderer(
    @ColorRes private val colorId: Int,
    private val context: Context,
    @DrawableRes private val drawableId: Int,
    map: GoogleMap,
    clusterManager: ClusterManager<MarkerItem>
) : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

    private val markerIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(context, colorId)
        vectorToBitmap(
            context,
            drawableId,
            color
        )
    }

    override fun onBeforeClusterItemRendered(item: MarkerItem, markerOptions: MarkerOptions) {
        markerOptions
            .title(item.title)
            .snippet(item.snippet)
            .icon(markerIcon)

        super.onBeforeClusterItemRendered(item, markerOptions)
    }
}
