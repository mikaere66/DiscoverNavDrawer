package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.Context
import android.graphics.Color
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.ClusterManager
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.android.gms.maps.model.MarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.BitmapHelper.vectorToBitmap
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getColorId

class ColorMultiIconRenderer(
    private val context: Context,
    @DrawableRes private val drawableId: Int,
    map: GoogleMap,
    clusterManager: ClusterManager<MarkerItem>
) : DefaultClusterRenderer<MarkerItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: MarkerItem, markerOptions: MarkerOptions) {
        markerOptions.title(item.title)
        markerOptions.snippet(item.snippet)

        item.color?.let {
            markerOptions.icon(
                vectorToBitmap(
                    context,
                    drawableId,
                    when (it) {
                        "#000000" -> {
                            val colorId = getColorId()
                            ContextCompat.getColor(context, colorId)
                        }
                        else -> Color.parseColor(it)
                    }
                )
            )
        }

        super.onBeforeClusterItemRendered(item, markerOptions)
    }

//    override fun onClusterItemRendered(clusterItem: MarkerItem, marker: Marker) {
//        super.onClusterItemRendered(clusterItem, marker)
//        marker.tag = clusterItem.itemId
//    }
}
