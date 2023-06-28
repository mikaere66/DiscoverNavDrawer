package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorMultiIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BatteryRecyclersActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    private lateinit var batteryRecyclers: List<BatteryRecyclerKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_1

        setTitleSubtitle(R.string.battery_recyclers_activity_title)

        lifecycleScope.launch {
            batteryRecyclers = viewModel.getBatteryRecyclersList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (batteryRecyclers.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    private fun addClusteredMarkers() {
        val context = this@BatteryRecyclersActivity
        clusterManager.apply {
            setRenderer(
                ColorMultiIconRenderer(
                    context,
                    R.drawable.ic_baseline_battery_2_bar_default_30,
                    map, this
                )
            )
        }
        for (i in batteryRecyclers.indices) {
            val snippet = batteryRecyclers[i].address
            val markerItem = getMarkerItem(
                batteryRecyclers[i].latLng, batteryRecyclers[i].name,
                snippet, batteryRecyclers[i].color
            )
            allMarkerItems[batteryRecyclers[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == batteryRecyclers.size.minus(1)) {
                initClusterManager()
                when (val batteryRecyclerId = viewModel.currentBatteryRecyclerId) {
                    0L -> zoomOutMarkers(true)
                    else -> {
                        val batteryRecyclerKt: BatteryRecyclerKt? = batteryRecyclers.stream()
                            .filter { batteryRecycler ->
                                batteryRecycler.id == batteryRecyclerId
                            }
                            .findFirst()
                            .orElse(null)
                        batteryRecyclerKt?.let { batteryRecycler ->
                            zoomInMarker(batteryRecycler.latLng)
                        }
                    }
                }
            }
        }
    }
}
