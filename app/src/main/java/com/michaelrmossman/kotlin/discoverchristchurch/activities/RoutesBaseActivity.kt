package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointsAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityRoutesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoRouteFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.enableDisableMyLocation
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getCoords
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.zoomInMarker
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goWaypoints
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.toggleFullscreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Extended by ChCh362|MultiDay|Routes activities, for overview Maps
@AndroidEntryPoint
abstract class RoutesBaseActivity: MapsBaseActivity<ActivityRoutesBinding>(
    R.layout.activity_routes
) , OnPolylineClickListener {

    val polylines = lazy { HashMap<Long, Polyline>() }
    lateinit var adapter: WaypointsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitleSubtitle()

        adapter = WaypointsAdapter(
            WaypointListener { index, waypoint ->
                when (index) {
                    0 -> scrollToPosition(waypoint)
                    1 -> showInfoFragment(index, waypoint)
                    else -> zoomInOutWaypoints(false, listOf(waypoint)) // 2
                }
            },
            WaypointLongListener { index, waypoint ->
                contentDescrToast(index, waypoint, waypoints)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(
            this,
            when (isLandscape()) {
                true -> RecyclerView.VERTICAL
                else -> RecyclerView.HORIZONTAL
            },
            false
        )
        recyclerView.layoutManager = layoutManager
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        viewModel.showStreetView.observe(this) { show ->
            if (show) {
                goStreetView()
                viewModel.setShowStreetView(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_routes_activity, menu)
        val visible = viewModel.routesMenuVisible
        val stringId = when (visible) {
            true -> R.string.menu_map
            else -> R.string.menu_360_leg_map
        }
        menu.apply {
            // Set text for map icon according to current use-case
            findItem(R.id.menu_map).title = getString(stringId)
            // Hide these menu items in ChCh362/MultiDay activities
            findItem(R.id.menu_details).isVisible = visible
            findItem(R.id.menu_show_hide).isVisible = visible
            findItem(R.id.menu_search).isVisible = visible
        }
        actionMenu = menu
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (waypoints.isNotEmpty()) {
            setMapCameraToView(
                true, map, getMapFragment().view, waypoints
            )
        }
    }

    /* Each of the three extended activities overrides goWaypoints() in
       it's own way. For show_all, both ChCh362 & MultiDay activities use
       zoomInOutWaypoints() below, whereas RoutesActivity overrides
       this. All other menuItems are only relevant to RoutesActivity */
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            // R.id.menu_details
            R.id.menu_map -> {
                goWaypoints()
                true
            }
            // R.id.menu_show_all
            // R.id.menu_show_hide
            // R.id.menu_search
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPolylineClick(polyline: Polyline?) {
        /* Applies to ChCh362|Routes activities,
           overridden in MultiDayActivity */
        polyline?.let {
            val waypointId = it.tag as Long
            val index = waypoints.indexOfFirst { waypoint ->
                waypoint.id == waypointId
            }
            if (index != -1) {
                scrollToPosition(index, waypoints.size)
            }
        }
    }

    override fun enableDisableMyLocation(enable: Boolean) {
        enableDisableMyLocation(this, enable, map, binding.root)
    }

    suspend fun getSelectedRoute(): RouteKt {
        val routeId = getSelectedWaypoint().id
        return viewModel.getRouteKtById(routeId)
    }

    fun getSelectedWaypoint(): WaypointKt {
        val position =
            layoutManager.findFirstCompletelyVisibleItemPosition()
        return waypoints[position]
    }

    /* Called from viewModel.showStreetView observer, above.
       Overridden in ChCh362Activity, with custom coords */
    open fun goStreetView() {
        val waypoint = viewModel.currentWaypoint
        when (waypoint.angle) {
            // Shouldn't be able to get this far, so just for safety
            -1 -> fancyToast(
                this,4, R.string.no_street_view_a
            )
            else -> {
                val coords = getCoords(waypoint, waypoints)
                viewModel.setCurrentCoords(coords)
                startActivity(
                    Intent(
                        this, StreetViewActivity::class.java
                    )
                )
            }
        }
    }

    open fun goWaypoints() {
        lifecycleScope.launch {
            val route = getSelectedRoute()
            viewModel.setCurrentRouteId(route.id)
            goWaypoints(this@RoutesBaseActivity, route.round)
        }
    }

    open fun setTitleSubtitle() {}

    open fun showInfoFragment(index: Int, waypoint: WaypointKt) {
        lifecycleScope.launch {
            val routeId = waypoint.id
            val route = viewModel.getRouteKtById(routeId)
            // Required for Conveniences
            viewModel.setCurrentRoute(route)
            /* Required for Intro, Start,
               Finish, and Street View */
            viewModel.setCurrentWaypoint(waypoint)
            InfoRouteFragment().show(
                supportFragmentManager,
                FRAGMENT_INFO_TAG
            )
        }
    }

    override fun toggleFullscreen() {
        toggleFullscreen(
            supportActionBar, binding.recyclerView, binding.root,
            binding.toggleGroup, binding.verticalGuideline
        )
    }

    fun zoomInMarker(latLng: LatLng) {
        zoomInMarker(
            false, latLng, map, CAMERA_START_POINT_ZOOM
        )
    }

    open fun zoomInOutWaypoints(
        init: Boolean, waypointsList: List<WaypointKt>
    ) {
        when (waypointsList.size) {
            1 -> {
                val latLng = waypointsList[0].latLng
                when (polylines.isInitialized()) {
                    true -> {
                        val routeId = waypointsList[0].id
                        when (polylines.value.containsKey(routeId)) {
                            true -> {
                                when (polylines.value[routeId]?.isVisible == true) {
                                    true -> {
                                        lifecycleScope.launch {
                                            val routeWaypoints =
                                                viewModel.getWaypointsListByRouteId(routeId)
                                            zoomInOutWaypoints(init, routeWaypoints)
                                        }
                                    }
                                    else -> zoomInMarker(latLng)
                                }
                            }
                            else -> zoomInMarker(latLng)
                        }
                    }
                    else -> zoomInMarker(latLng)
                }
            }
            else -> setMapCameraToView(
                init, map, getMapFragment().view,
                waypointsList, CAMERA_SMALL_PADDING
            )
        }
    }
}
