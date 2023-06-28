package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorSingleIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getColorId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FreeWiFiActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    private lateinit var locations: List<FreeWiFiKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_5

        setTitleSubtitle(R.string.free_wi_fi_activity_title)

        lifecycleScope.launch {
            locations = viewModel.getFreeWiFiLocations()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (locations.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    private fun addClusteredMarkers() {
        val context = this@FreeWiFiActivity
        clusterManager.apply {
            setRenderer(
                ColorSingleIconRenderer(
                    getColorId(),
                    context,
                    R.drawable.ic_baseline_wifi_default_30,
                    map,this
                )
            )
        }
        for (i in locations.indices) {
            val snippet = getSnippet(locations[i].extra)
            val markerItem = getMarkerItem(
                locations[i].latLng, locations[i].name, snippet
            )
            allMarkerItems[locations[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == locations.size.minus(1)) {
                initClusterManager()
                when (val freeWiFiId = viewModel.currentFreeWiFiId) {
                    0L -> zoomOutMarkers(true)
                    else -> {
                        val freeWiFiKt: FreeWiFiKt? = locations.stream()
                            .filter { freeWiFi ->
                                freeWiFi.id == freeWiFiId
                            }
                            .findFirst()
                            .orElse(null)
                        freeWiFiKt?.let { freeWiFi ->
                            zoomInMarker(freeWiFi.latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getSnippet(extra: String?): String {
        return when (extra) {
            null -> getString(R.string.snippet_no_info)
            else -> extra
        }
    }
}
