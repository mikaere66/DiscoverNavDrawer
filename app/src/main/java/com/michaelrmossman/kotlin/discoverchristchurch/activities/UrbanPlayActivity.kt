package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.CAMERA_MEDIUM_PADDING
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorSingleIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UrbanPlayActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    private lateinit var urbanPlayItems: List<UrbanPlayKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_12

        setTitleSubtitle(R.string.urban_play_activity_title)

        lifecycleScope.launch {
            urbanPlayItems = viewModel.getUrbanPlayItems()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (urbanPlayItems.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    private fun addClusteredMarkers() {
        val context = this@UrbanPlayActivity
        clusterManager.apply {
            setRenderer(
                ColorSingleIconRenderer(
                    R.color.urban_play_gold,
                    context,
                    R.drawable.ic_baseline_whatshot_default_30,
                    map,this
                )
            )
        }
        for (i in urbanPlayItems.indices) {
            val snippet = getSnippet(urbanPlayItems[i].address)
            val markerItem = getMarkerItem(
                urbanPlayItems[i].latLng, urbanPlayItems[i].name, snippet
            )
            allMarkerItems[urbanPlayItems[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == urbanPlayItems.size.minus(1)) {
                initClusterManager()
                when (val urbanPlayItemId = viewModel.currentUrbanPlayId) {
                    0L -> zoomInOutMarkers(true, CAMERA_MEDIUM_PADDING)
                    else -> {
                        val urbanPlayItem: UrbanPlayKt? = urbanPlayItems.stream()
                            .filter { urbanPlay ->
                                urbanPlay.id == urbanPlayItemId
                            }
                            .findFirst()
                            .orElse(null)
                        urbanPlayItem?.let { urbanPlay ->
                            zoomInMarker(urbanPlay.latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getSnippet(address: String): String {
        return when (val end = address.indexOf(",")) {
            -1 -> address
            else -> address.substring(0, end)
        }
    }
}
