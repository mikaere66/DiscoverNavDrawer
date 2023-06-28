package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getRoutePolyline
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MULTI_DAY_COASTAL_ID
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MULTI_DAY_CRATER_ID
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MULTI_DAY_HEADS_ID
import kotlinx.coroutines.launch

// "Coastal Pathway", "Crater Rim", and "Head to Head" routes
class MultiDayActivity: RoutesBaseActivity() {

    private lateinit var allWaypoints: MutableList<WaypointKt>
    private lateinit var multiRouteIds: List<Long>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            waypoints = viewModel.getRoutesListAsWaypoints()

            adapter.submitList(waypoints)

            if (viewModel.currentRouteId > 0L) {
                val index = waypoints.indexOfFirst { route ->
                    route.id == viewModel.currentRouteId
                }

                if (index != -1) {
                    recyclerView.scrollToPosition(index)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        multiRouteIds = viewModel.multiRouteIds

        if (multiRouteIds.isNotEmpty()) {
            val context = this@MultiDayActivity
            googleMap.setOnPolylineClickListener(context)

            if (waypoints.isNotEmpty()) {
                lifecycleScope.launch {
                    allWaypoints = mutableListOf()

                    for (i in waypoints.indices) {
                        val routeWaypoints =
                            // Skip start waypoint when NOT FIRST crater rim track
                            viewModel.getWaypointsListByRouteId(
                                waypoints[i].id,i == 0
                            )
                        allWaypoints.addAll(routeWaypoints)

                        val route =
                            viewModel.getRouteKtById(multiRouteIds[i])
                        val polyline = getRoutePolyline(
                            context, map, route, routeWaypoints,
                            2, viewModel.multiRouteIndex
                        )
                        polylines.value[route.id] = polyline

                        if (i == waypoints.size.minus(1)) {
                            actionMenu?.findItem(R.id.menu_show_all)?.isEnabled =
                                allWaypoints.size > 1
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomInOutWaypoints(false, allWaypoints)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPolylineClick(polyline: Polyline?) {
        polyline?.let {
            val craterRimRouteId = it.tag as Long
            val index = multiRouteIds.indexOf(craterRimRouteId)
            if (index != -1) {
                recyclerView.smoothScrollToPosition(index)
            }
        }
    }

    override fun setTitleSubtitle() {
        val subtitleId = when (viewModel.multiRouteIndex) {
            MULTI_DAY_COASTAL_ID -> {
                R.string.waypoints_coastal_pathway_subtitle
            }
            MULTI_DAY_CRATER_ID -> {
                R.string.waypoints_crater_rim_subtitle
            }
            MULTI_DAY_HEADS_ID -> {
                R.string.waypoints_head_to_head_subtitle
            }
            else -> R.string.waypoints_undefined_subtitle // Debug
        }
        supportActionBar?.subtitle = getString(subtitleId)
    }
}
