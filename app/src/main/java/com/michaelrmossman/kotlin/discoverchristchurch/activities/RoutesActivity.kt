package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.button.MaterialButton
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getRoutePolyline
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import kotlinx.coroutines.*

// Extended by NearestActivity, with live Location updates
open class RoutesActivity: RoutesBaseActivity(),
    OnClusterItemClickListener<MarkerItem> {

    private lateinit var clusterManager: ClusterManager<MarkerItem>
    private lateinit var polylinesScope: CoroutineScope

    override fun onClusterItemClick(item: MarkerItem): Boolean {
        clearSelectedMarker()

        val waypointKt: WaypointKt? = waypoints.stream()
            .filter { waypoint ->
                waypoint.latLng == item.position
            }
            .findFirst()
            .orElse(null)
        waypointKt?.let { waypoint ->
            setSelected(waypoint,false)
        }

        /* Returns: true if the listener consumed the event (i.e. the default behavior should
           NOT occur), false otherwise (i.e. the default behavior SHOULD occur). The default
           behavior is for the camera to move to the marker and an info window to appear */
        return false // Must be false to show infoWindow, otherwise it's just recycler pos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.togglePolylineButton.apply {
            setOnClickListener {
                showHidePolyline()
            }
            setOnLongClickListener {
                contentDescrToast(
                    4.plus(1) // Remember: index - 1
                )
                true
            }
        }

        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recycler: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recycler, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (polylines.isInitialized()) {
                        val position =
                            layoutManager.findFirstCompletelyVisibleItemPosition()
                        if (position != -1) {
                            val waypoint = waypoints[position]
                            setToggleButtonIcon(
                                polylineIsNotVisible(waypoint)
                            )
                            /* Used to determine currently active polyline,
                               if applicable. See also zoomInPolyline() */
                            viewModel.setCurrentPolylineId(0L)
                        }
                    }
                }
            }
        })

        lifecycleScope.launch {
            waypoints = viewModel.getRoutesListAsWaypoints()

            adapter.submitList(waypoints)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        actionMenu?.findItem(R.id.menu_show_all)?.isEnabled =
            waypoints.size > 1
        binding.togglePolylineButton.isVisible = waypoints.size > 2

        val context = this@RoutesActivity
        clusterManager = ClusterManager<MarkerItem>(context, map)
        clusterManager.apply {
            setOnClusterItemClickListener(context)
            setRenderer(
                ColorSingleIconRenderer(
                    R.color.color_green,
                    context,
                    R.drawable.ic_baseline_place_default_27,
                    map,this
                )
            )
        }
        map.apply {
            setOnCameraIdleListener(clusterManager)
            setOnMarkerClickListener(clusterManager)
            setOnPolylineClickListener(context)
        }
        if (waypoints.isNotEmpty()) {
            waypoints.forEach { waypoint ->
                waypoint.extra?.let { snippet ->
                    clusterManager.addItem(
                        MarkerItem(
                            waypoint.latLng,
                            waypoint.waypoint,
                            snippet as String
                        )
                    )
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_details -> {
                goDetails()
                true
            }
            // R.id.menu_map ... refer RoutesBaseActivity
            R.id.menu_show_all -> {
                setMapCameraToView(
                    false, map, getMapFragment().view, waypoints
                )
                true
            }
            R.id.menu_show_hide -> {
                showHidePolylines()
                true
            }
            R.id.menu_search -> {
                getRouteName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)

        if (polylines.isInitialized()) {
            val listBooleans =
                polylines.value.values.map { it.isVisible }
            if (!listBooleans.allFalse()) {
                actionMenu?.findItem(R.id.menu_show_hide)?.title =
                    getString(R.string.menu_hide)
                return true
            }

        } // Else set menu title to "Show All"
        actionMenu?.findItem(R.id.menu_show_hide)?.title =
            getString(R.string.menu_show)
        return true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (polylines.isInitialized()) {
            viewModel.setPolylineVisibility(setPolylineIds())
        }

        super.onSaveInstanceState(outState)
    }

    override fun cancelCoroutine() { // Also called from xml
        if (this::polylinesScope.isInitialized) {
            polylinesScope.cancel()
            val layoutRoot = binding.bannerLayout.root
            if (layoutRoot.isVisible) {
                layoutRoot.visibility = View.GONE
            }
        }
    }

    open fun goDetails() {
        lifecycleScope.launch {
            val route = getSelectedRoute()
            viewModel.setCurrentRouteId(route.id)
            startActivity(
                Intent(
                    this@RoutesActivity,
                    DetailsActivity::class.java
                )
            )
        }
    }

    private fun hideAllPolylines() {
        setToggleButtonIcon(true)

        if (polylines.isInitialized()) { // Just for safety
            for (poly in polylines.value.values) {
                poly.isVisible = false
            }
        }
    }

    private fun polylineIsNotVisible(waypoint: WaypointKt): Boolean {
        return when (polylines.isInitialized()) {
            false -> true
            else -> {
                when (polylines.value.containsKey(waypoint.id)) {
                    false -> true
                    else -> {
                        when (polylines.value[waypoint.id]?.isVisible) {
                            true -> false
                            else -> true
                        }
                    }
                }
            }
        }
    }

    override fun restorePolylines() {
        val polylineIds = viewModel.polylineIds
        if (polylineIds.isNotEmpty()) {
            val currentPolylineId = viewModel.currentPolylineId
            lifecycleScope.launch {
                polylineIds.forEach { id ->
                    val route = viewModel.getRouteKtById(id)
                    showPolyline(
                        route,1,
                        when (currentPolylineId) {
                            id -> 500L
                            else -> -1L
                        }
                    )
                }
            }
        }
    }

    private fun setPolylineIds(): Boolean {
        val polylineIds = mutableListOf<Long>()
        var polylineVisibility = false

        for (key in polylines.value.keys) {
            if (polylines.value[key]?.isVisible == true) {
                polylineIds.add(key)
                polylineVisibility = true
            }
        }

        viewModel.setPolylineIds(polylineIds)

        return polylineVisibility
    }

    override fun setPosition(routeId: Long) {
        val waypoint: WaypointKt? = waypoints.stream()
            .filter { waypoint ->
                waypoint.id == routeId
            }
            .findFirst()
            .orElse(null)
        setSelected(waypoint,true)
    }

    private fun setSelected(waypoint: WaypointKt?, zoom: Boolean) {
        waypoint?.let { filtered ->
            val index = waypoints.indexOfFirst { route ->
                route.id == filtered.id
            }
//            setToggleButtonIcon(
//                polylineIsNotVisible(filtered)
//            )
            if (index != -1) {
                scrollToPosition(index, waypoints.size)

                if (zoom) {
                    zoomInOutWaypoints(false, listOf(filtered))
                }
            }
        }
    }

    override fun setTitleSubtitle() {
        val subtitleId = R.string.waypoints_all_subtitle
        supportActionBar?.subtitle = getString(subtitleId)
    }

    fun setToggleButtonIcon(polylineIsNotVisible: Boolean) {
        val button = binding.togglePolylineButton
        val drawableId = when (polylineIsNotVisible) {
            true -> R.drawable.ic_outline_polyline_translucent_24
            else -> R.drawable.ic_outline_maps_ugc_translucent_24
        }
        (button as MaterialButton).setIconResource(drawableId)
    }

    private fun showAllPolylines() {
        val layout = binding.bannerLayout
        layout.routesActivity = this
        val progressIndicator = layout.bannerProgress.apply {
            // Ensure that value is reset each time
            setProgressCompat(0,false)
        }
        val size = waypoints.size
        polylinesScope = CoroutineScope(Dispatchers.Default)
        polylinesScope.launch {
            for (i in waypoints.indices) {
                if (isActive) {
                    val progress = i.getProgress(size).plus(1)
                    val progressText = String.format(
                        getString(R.string.polylines_progress),
                        i, size
                    )
                    withContext(Dispatchers.Main) {
                        val routeId = waypoints[i].id
                        val route = viewModel.getRouteKtById(routeId)
                        showPolyline(route,1)
                        layout.apply {
                            bannerText.text = progressText
                            root.visibility = when (progress) {
                                in 0..99 -> View.VISIBLE
                                else -> View.GONE
                            }
                        }
                        progressIndicator.apply {
                            setProgressCompat(progress,true)
                        }
                    }
                }
            }
        }
    }

    private fun showHidePolyline() {
        val waypoint = getSelectedWaypoint()
        var polylineIsNotVisible = false
        val polylineIsVisible =
            polylines.value.containsKey(waypoint.id) &&
            polylines.value[waypoint.id]?.isVisible == true
        when (polylineIsVisible) {
            false -> showPolyline(null,1,0L)
            else -> polylines.value[waypoint.id]?.let { polyline ->
                polyline.isVisible = false
                polylineIsNotVisible = true
            }
        }
        setToggleButtonIcon(polylineIsNotVisible)
    }

    private fun showHidePolylines() {
        when (polylines.isInitialized()) {
            false -> showAllPolylines()
            else -> {
                for (poly in polylines.value.values) {
                    if (poly.isVisible) {
                        hideAllPolylines()
                        return
                    }
                }
                showAllPolylines()
            }
        }
    }

    private fun showPolyline(
        currentRoute: RouteKt?, caps: Int, zoom: Long = -1L
    ) {
        setToggleButtonIcon(false)

        lifecycleScope.launch {
            val route = when (currentRoute) {
                null -> getSelectedRoute()
                else -> currentRoute
            }
            when (polylines.value.containsKey(route.id)) {
                true -> polylines.value[route.id]?.let { polyline ->
                    polyline.isVisible = true
                    if (zoom == 0L) { zoomInPolyline(false, polyline) }
                }
                else -> {
                    val context = this@RoutesActivity
                    val routeWaypoints =
                        viewModel.getWaypointsListByRouteId(route.id)
                    val polyline =
                        getRoutePolyline(context, map, route, routeWaypoints, caps)
                    polylines.value[route.id] = polyline
                    polyline.apply {
                        isClickable = true
                        /* Find recycler position for
                           OnPolylineClickListener */
                        tag = route.id
                    }
                    if (zoom != -1L) {
                        when (zoom) {
                            0L -> zoomInPolyline(false, polyline)
                            else -> {
                                delay(zoom) // 500L
                                zoomInPolyline(true, polyline)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun zoomInPolyline(
        init: Boolean = false, polyline: Polyline
    ) {
        val points = polyline.points
        points?.let { list ->
            val bounds = LatLngBounds.builder()
            list.forEach {
                bounds.include(
                    LatLng(it.latitude, it.longitude)
                )
            }
            try {
                setMapCameraToView(
                    bounds.build(), init, map, getMapFragment().view,
                    when (init) {
                        true -> CAMERA_UPDATE_PADDING
                        else -> CAMERA_MEDIUM_PADDING
                    }
                )
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                fancyToast(
                    this@RoutesActivity,3,
                    R.string.no_polyline
                )
            }
        }
        val currentPolylineId = polyline.tag as Long
        viewModel.setCurrentPolylineId(currentPolylineId)
    }
}
