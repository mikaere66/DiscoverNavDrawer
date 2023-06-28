package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ALWAYS_SHOW_POLYLINE_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SHOW_OPTIONAL_BUTTONS_PREF
import kotlinx.coroutines.launch

class ExtendedActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subtitleId = R.string.waypoints_extended_subtitle
        supportActionBar?.subtitle = getString(subtitleId)

        lifecycleScope.launch {
            val currentRouteId = viewModel.currentRouteId
            waypoints = viewModel.getWaypointsListByRouteId(currentRouteId)

            adapter.submitList(waypoints)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (waypoints.isNotEmpty()) {
            addMapMarkers(true)

            enableDisableOptions(
                when (sharedPrefs.getBoolean(
                    SHOW_OPTIONAL_BUTTONS_PREF,
                    true)
                ) {
                    false -> 0
                    else ->{
                        when (waypoints.size) {
                            in 0..3 -> 2
                            else -> 3
                        }
                    }
                }
            )

            if (waypoints.size > 3) {
                // e.g. Te Ara Ihutai Christchurch Coastal Pathway x 3
                when (viewModel.polylineVisibility) {
                    true -> showHidePolyline()
                    else -> {
                        if (sharedPrefs.getBoolean(
                                ALWAYS_SHOW_POLYLINE_PREF,
                                false
                            )
                        ) { showHidePolyline() }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_zoom -> {
                val isChecked = !item.isChecked
                item.isChecked = isChecked
                if (sharedPrefs.edit().putBoolean(SHOW_OPTIONAL_BUTTONS_PREF,
                        isChecked).commit()) {
                    enableDisableOptions(
                        when (isChecked) {
                            false -> 0
                            else ->{
                                when (waypoints.size) {
                                    in 0..3 -> 2
                                    else -> 3
                                }
                            }
                        }
                    )
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
