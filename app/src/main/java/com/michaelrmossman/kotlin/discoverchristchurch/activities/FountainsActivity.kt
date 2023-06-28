package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorSingleIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FountainsActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    private lateinit var fountains: List<DrinkFountainKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_4

        setTitleSubtitle(R.string.fountains_activity_title)

        lifecycleScope.launch {
            fountains = viewModel.getFountainsList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (fountains.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    private fun addClusteredMarkers() {
        val context = this@FountainsActivity
        clusterManager.apply {
            setRenderer(
                ColorSingleIconRenderer(
                    R.color.fountain_blue,
                    context,
                    R.drawable.ic_baseline_water_drop_default_30,
                    map,this
                )
            )
        }
        for (i in fountains.indices) {
            val snippet = getSnippet(fountains[i].extra)
            val markerItem = getMarkerItem(
                fountains[i].latLng, fountains[i].name, snippet
            )
            allMarkerItems[fountains[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == fountains.size.minus(1)) {
                initClusterManager()
                when (val fountainId = viewModel.currentFountainId) {
                    0L -> zoomOutMarkers(true)
                    else -> {
                        val fountainKt: DrinkFountainKt? = fountains.stream()
                            .filter { fountain ->
                                fountain.id == fountainId
                            }
                            .findFirst()
                            .orElse(null)
                        fountainKt?.let { fountain ->
                            zoomInMarker(fountain.latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getSnippet(extra: String?): String {
        return when (extra) {
            null -> getString(R.string.snippet_no_info)
            else -> {
                when (val end = extra.indexOf(",")) {
                    -1 -> extra
                    else -> {
                        extra.substring(0, end)
                    }
                }
            }
        }
    }
}
