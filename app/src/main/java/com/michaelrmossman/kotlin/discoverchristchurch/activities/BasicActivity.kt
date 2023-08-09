package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ALWAYS_SHOW_MARKERS_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SHOW_OPTIONAL_BUTTONS_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import kotlinx.coroutines.launch

class BasicActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val currentRouteId = viewModel.currentRouteId

            val route = viewModel.getRouteById(currentRouteId)
            val subtitleId =
                when (route.shared) {
                    1 -> R.string.waypoints_shared_subtitle
                    else -> R.string.waypoints_basic_subtitle
                }
            supportActionBar?.subtitle = getString(subtitleId)

            waypoints =
                viewModel.getWaypointsListByRouteId(currentRouteId)

            val sections = mutableListOf<WaypointKt>()
            sections.add(waypoints[0])
            if (waypoints.size > 1) {
                sections.add(waypoints[waypoints.size.minus(1)])
            }

            adapter.submitList(sections)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        addPolyline(true, currentRoute)

        binding.buttonsLayout.apply {
            // For visibility, refer addPolyline() in Base activity
            toggleMarkersButton.setOnClickListener {
                showHideMarkers()
            }
            toggleMarkersButton.setOnLongClickListener {
                // Remember: index - 1
                contentDescrToast(3.plus(1))
                true
            }
        }

        enableDisableOptions(
            when (sharedPrefs.getBoolean(SHOW_OPTIONAL_BUTTONS_PREF, true)) {
                true -> 1
                else -> 0
            }
        )

        when (viewModel.optionalMarkersVisible) {
            true -> showHideMarkers()
            else -> {
                if (sharedPrefs.getBoolean(
                        ALWAYS_SHOW_MARKERS_PREF,
                        false
                    )
                ) { showHideMarkers() }
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
                            true -> 1
                            else -> 0
                        }
                    )
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
