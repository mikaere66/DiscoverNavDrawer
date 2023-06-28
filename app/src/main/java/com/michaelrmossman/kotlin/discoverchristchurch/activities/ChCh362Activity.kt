package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.content.Intent
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360CoordsKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoChCh362Fragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.CAMERA_SMALL_PADDING
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_TAG
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getChCh360Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.POLYLINE_HEAVY_STROKE_WIDTH
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goWaypoints
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import kotlinx.coroutines.launch

class ChCh362Activity: RoutesBaseActivity() {

    private lateinit var polylineLists: HashMap<Long, List<Polyline>>
    private lateinit var allCoordinates: MutableList<ChCh360CoordsKt>
    private lateinit var chCh360Items: List<ChCh360Kt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            chCh360Items = viewModel.getChCh360Items()
            waypoints = viewModel.getChCh360ItemsAsWaypoints(chCh360Items)

            adapter.submitList(waypoints)

            if (viewModel.chCh360ItemId > 0L) {
                val index = waypoints.indexOfFirst { chCh360 ->
                    chCh360.id == viewModel.chCh360ItemId
                }
                if (index != -1) {
                    recyclerView.scrollToPosition(index)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (chCh360Items.isNotEmpty()) {
            lifecycleScope.launch {
                allCoordinates = mutableListOf()
                polylineLists = HashMap()
                chCh360Items.forEach { item ->
                    val polylines = mutableListOf<Polyline>()
                    // Until loop excludes the end index element
                    for (i in 0 until item.trackCount) {
                        val coordinates =
                            viewModel.getChCh360CoordsListByLegId(item.id, i)
                        allCoordinates.addAll(coordinates)
                        val polyline = getChCh360Polyline(
                            item, coordinates, map,
                            POLYLINE_HEAVY_STROKE_WIDTH
                        )
                        polyline.apply {
                            // Refer to RoutesBaseActivity for clickListener
                            isClickable = true
                            /* Find recycler position for
                               OnPolylineClickListener */
                            tag = item.id
                        }
                        /* Avonhead Gardens leg is actually made up
                           of 3 polylines, so rather than mess with
                           the .gpx file, we just work around it */
                        polylines.add(polyline)
                    }

                    polylineLists[item.id] = polylines
                }

                if (polylineLists.isNotEmpty()) {
                    enableDisableShowAllMenu()
                    map.setOnPolylineClickListener(this@ChCh362Activity)
                    /* If map jumps at this stage, check
                       viewModel.setRoutesMenuVisible(false)
                       and RoutesBaseActivity.onMapReady() */
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomInOutWaypoints(false, listOf())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun enableDisableShowAllMenu() {
        if (this::allCoordinates.isInitialized) {
            if (allCoordinates.size > 1) {
                actionMenu?.findItem(R.id.menu_show_all)?.isEnabled = true
            }
        }
    }

    private fun getSelectedChCh360ItemId(): Long {
        val position =
            layoutManager.findFirstCompletelyVisibleItemPosition()
        val chCh360 = waypoints[position]
        return chCh360.id
    }

    /* Called from viewModel.showStreetView observer in
       RoutesActivity. Overridden because of coords */
    override fun goStreetView() {
        val waypoint = viewModel.currentWaypoint
        when (waypoint.angle) {
            // Shouldn't actually be able to get this far, so just for safety
            -1 -> fancyToast(this,4, R.string.no_street_view_a)
            else -> {
                val coords = Coords(
                    String.format(
                        getString(R.string.app_title),
                        waypoint.route
                    ),
                    String.format(
                        getString(R.string.street_view_start_point_subtitle),
                        waypoint.waypoint
                    ),
                    waypoint.latLng,
                    waypoint.angle,
                    getWaypointSubtitleText(1,2, R.string.ch_ch_360_count)
                )
                viewModel.setCurrentCoords(coords)
                startActivity(
                    Intent(
                        this, StreetViewActivity::class.java
                    )
                )
            }
        }
    }

    override fun goWaypoints() {
        val chCh360ItemId = getSelectedChCh360ItemId()
        viewModel.setChCh360ItemId(chCh360ItemId)
        // Reset currentRouteId to 0L to hide Faves button
        viewModel.setCurrentRouteId(0L)
        goWaypoints(this, Int.MIN_VALUE) // ChCh361Activity
    }

    override fun setTitleSubtitle() {
        val title = String.format(
            getString(R.string.app_title),
            getString(R.string.ch_ch_360_title)
        )
        supportActionBar?.title = title

        supportActionBar?.subtitle =
            getString(R.string.waypoints_ch_ch_362_subtitle)
    }

    override fun showInfoFragment(index: Int, waypoint: WaypointKt) {
        lifecycleScope.launch {
            val chCh360Item = chCh360Items.stream()
                .filter { item ->
                    item.id == waypoint.id
                }
                .findFirst()
                .orElse(null)

            chCh360Item?.let { filtered ->
                viewModel.setChCh360Item(filtered)
                // Required for Street View
                viewModel.setCurrentWaypoint(waypoint)
                InfoChCh362Fragment().show(
                    supportFragmentManager,
                    FRAGMENT_INFO_TAG
                )
            }
        }
    }

    override fun zoomInOutWaypoints(
        init: Boolean, waypointsList: List<WaypointKt>
    ) {
        if (allCoordinates.isNotEmpty()) {
            val bounds = LatLngBounds.builder()
            when (waypointsList.size) {
                0 -> { // For when called from above
                    allCoordinates.forEach {
                        bounds.include(it.latLng)
                    }
                    setMapCameraToView(
                        bounds.build(), init, map,
                        getMapFragment().view,
                        CAMERA_SMALL_PADDING
                    )
                }
                else -> { /* Required, for when called from the
                    adapter's clickListener, hence the override */
                    val id = waypointsList[0].id
                    val polylineList = polylineLists[id]
                    if (polylineList != null) {
                        polylineList.forEach { poly ->
                            val points = poly.points
                            points?.let { list ->
                                list.forEach {
                                    bounds.include(
                                        LatLng(it.latitude, it.longitude)
                                    )
                                }
                            }
                        }
                        try {
                            setMapCameraToView(
                                bounds.build(),false, map,
                                getMapFragment().view,
                                CAMERA_SMALL_PADDING
                            )
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                            fancyToast(
                                this,3,
                                R.string.no_polyline
                            )
                        }
                    }
                }
            }
        }
    }
}
