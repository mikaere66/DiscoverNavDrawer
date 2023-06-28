package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.material.button.MaterialButton
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.WaypointsAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityWaypointsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoWaypointFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.enableDisableMyLocation
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getCoords
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getMapType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getRoutePolyline
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.zoomInMarker
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.toggleFullscreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Extended by Basic|ChCh361|Extended activities, for single Maps
@AndroidEntryPoint
abstract class BaseActivity: MapsBaseActivity<ActivityWaypointsBinding>(
    R.layout.activity_waypoints
) , OnInfoWindowClickListener, OnMarkerClickListener {

    private lateinit var firstAndLast: MutableList<Marker> // Show/hide polylines
    lateinit var allMarkers: MutableList<Marker> // Show/hide/recolor/zoom markers
    private lateinit var polyline: Polyline
    lateinit var adapter: WaypointsAdapter
    lateinit var chCh360Item: ChCh360Kt
    lateinit var currentRoute: RouteKt

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = WaypointsAdapter(
            WaypointListener { index, waypoint ->
                when (index) {
                    0 -> scrollToPosition(waypoint)
                    1 -> showInfoFragment(waypoint)
                    else -> zoomInMarker(
                        false, waypoint.latLng, map,
                        CAMERA_DEFAULT_ZOOM
                    ) // 2
                }
            },
            WaypointLongListener { index, waypoint ->
                contentDescrToast(index, waypoint, waypoints)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY
        optPolylines = HashMap() // Just initialise, for now
        recyclerView = binding.recyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(
                this,
                when (isLandscape()) {
                    true -> RecyclerView.VERTICAL
                    else -> RecyclerView.HORIZONTAL
                },
                false
            )
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)
        binding.buttonsLayout.toggleFaveButton.apply {
            setOnLongClickListener {
                contentDescrToast(
                    2.plus(1) // Remember: index - 1
                )
                true
            }
        }
        val currentRouteId = viewModel.currentRouteId
        /* Allow for ChCh361Activity, where
           routeId should be reset to 0L */
        if (currentRouteId > 0L) {
            lifecycleScope.launch {
                currentRoute = viewModel.getRouteKtById(currentRouteId)
                val title =
                    String.format(getString(R.string.app_title),
                        currentRoute.route)
                supportActionBar?.title = title
                // Set the favourite icon state
                setToggleButtonIcon(0, currentRoute.fave)
            }
            binding.apply {
                buttonsLayout.toggleFaveButton.apply {
                    setOnClickListener { toggleFaveRoute() }
                }
                buttonsLayout.togglePolylineButton.apply {
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
            }
        }
        viewModel.showStreetView.observe(this) { show ->
            if (show) {
                goStreetView()
                viewModel.setShowStreetView(false)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_waypoints, menu)
        actionMenu = menu
        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
        marker?.hideInfoWindow()
        clearSelectedMarker()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val context = this@BaseActivity
        map.apply {
            setOnInfoWindowClickListener(context)
            setOnMarkerClickListener(context)
        }
        if (waypoints.size > 1) {
            setMapCameraToView(
                true, map, getMapFragment().view, waypoints
            )
        }
    }

    override fun onMarkerClick(mapMarker: Marker?): Boolean {
         mapMarker?.let { marker ->
            marker.tag?.let { tag ->
                val position = tag as Int
                if (position != -1) { /* Tag is actually -1 for optional
                    markers. Prevents scrollToPosition for same */
                    recyclerView.smoothScrollToPosition(position)
                 }
             }
        }
        // showMarkerDebugMessage(marker?.position)
        return true // Don't allow map to consume event
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                setMapCameraToView(
                    false, map, getMapFragment().view, waypoints
                )
                true
            }
            R.id.menu_layers -> {
                getMapType()
                true
            }
            // R.id.menu_show_location ... see MapsBaseActivity
            else -> super.onOptionsItemSelected(item)
        }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
            when(requestCode) {
                REQUEST_SHOW_MY_LOCATION_PERMISSION -> {
                    enableDisableMyLocation(true)
                }
            }
        }
    }

    private fun addCircle(waypoint: WaypointKt) {
        /* Workaround, since we can't have onClick events for
           polyline start/end Caps. Creates an invisible but
           clickable circle around start/endpoint markers */
        val circle = map.addCircle(
            CircleOptions()
                .center(waypoint.latLng)
                .radius(5.0)
                .strokeWidth(0f)
                .clickable(true)
        )
        circle.tag = waypoint.position
    }

    private fun addMapMarker(index: Int, init: Boolean) {
        val marker =
            map.addMarker(waypoints[index].markerOptions)
        marker.tag = when (init) {
            true -> index
            else -> {
                // Prevent scrollToPosition with optional markers
                -1
            }
        }
        allMarkers.add(marker)
    }

    fun addMapMarkers(init: Boolean) {
        allMarkers = mutableListOf()
        firstAndLast = mutableListOf()
        when (init) {
            true -> {
                for (i in waypoints.indices) {
                    addMapMarker(i,true)
                }
                firstAndLast.add(allMarkers[0])
                if (allMarkers.size > 1) {
                    firstAndLast.add(allMarkers[allMarkers.size.minus(1)])
                }
            }
            else -> {
                /* Show/hide optional markers. Skip first and last,
                   as those markers are already on the polyline */
                for(i in 1 until waypoints.size.minus(1)) {
                    addMapMarker(i,false)
                }
            }
        }
        enableDisableShowAllMenu()
    }

    fun addPolyline(init: Boolean, route: RouteKt) {
        polyline = getRoutePolyline(this, map, route, waypoints)
        addCircle(waypoints[0])
        map.setOnCircleClickListener { circle ->
            val position = circle.tag as Int
            if (position != -1) {
                recyclerView.smoothScrollToPosition(position)
             }
        }

        if (waypoints.size > 1) {
            addCircle(waypoints[waypoints.size.minus(1)])

            if (init) { // Only for BasicActivity, not OPTIONAL polyline
                binding.buttonsLayout.toggleMarkersButton.isVisible =
                    waypoints.size > 2
                setMapCameraToView(
                    true, map, getMapFragment().view, waypoints
                )
            }
            enableDisableShowAllMenu()
        }
    }

    override fun enableDisableMyLocation(enable: Boolean) {
        enableDisableMyLocation(this, enable, map, binding.root)
    }

    fun enableDisableOptions(index: Int) {
        map.uiSettings.apply {
            isMapToolbarEnabled = true
            isZoomControlsEnabled = index != 0 // ChCh360 is -1
        }

        binding.buttonsLayout.apply {
            toggleFaveButton.isVisible = index != 0
            toggleMarkersButton.isVisible = index == 1
            togglePolylineButton.isVisible = index > 2
        }
    }

    open fun getMapType() {
        alertDialog =
            getMapType(this, map, binding.root, ::setMapType)
        alertDialog?.show()
    }

    // Called from viewModel.showStreetView observer (see above)
    private fun goStreetView() {
        val waypoint = viewModel.currentWaypoint
        when (waypoint.angle) {
            // Shouldn't actually be able to get this far, so just for safety
            -1 -> fancyToast(this,4, R.string.no_street_view_a)
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

    private fun setMapType(mapType: Int) { // mapType dialog
        setMapType(mapType, map) // No return value required
    }

    fun setToggleButtonIcon(index: Int, fave: Boolean = false) {
        val button: Button
        val drawableId = when (index) {
            0 -> { // Faves
                button = binding.buttonsLayout.toggleFaveButton
                when (fave) {
                    true -> R.drawable.ic_outline_bookmark_remove_translucent_24
                    else -> R.drawable.ic_outline_bookmark_add_translucent_24
                }
            }
            1 -> { // Markers
                button = binding.buttonsLayout.toggleMarkersButton
                when (allMarkers[0].isVisible) {
                    true -> R.drawable.ic_outline_polyline_translucent_24
                    else -> R.drawable.ic_outline_maps_ugc_translucent_24
                }
            }
            else -> { // Polyline
                button = binding.buttonsLayout.togglePolylineButton
                when (polyline.isVisible) {
                    true -> R.drawable.ic_outline_maps_ugc_translucent_24
                    else -> R.drawable.ic_outline_polyline_translucent_24
                }
            }
        }
        (button as MaterialButton).setIconResource(drawableId)
    }

    private fun showHideFirstAndLast(visible: Boolean) {
        firstAndLast.forEach { marker ->
            marker.isVisible = !visible
        }
    }

    fun showHideMarkers() {
        when (this::allMarkers.isInitialized) {
            false -> {
                addMapMarkers(false)
                viewModel.setOptionalMarkersVisible(true)
            }
            else -> {
                /* Toggle markers button only shown in
                   BasicActivity. Indicates that showing
                   OPTIONAL markers has been triggered */
                viewModel.setOptionalMarkersVisible(!allMarkers[0].isVisible)
                allMarkers.forEach { marker ->
                    marker.isVisible = !marker.isVisible
                }
            }
        }
        setToggleButtonIcon(1)
    }

    open fun showHidePolyline() {
        when (this::polyline.isInitialized) {
            false -> {
                addPolyline(false, currentRoute)
                viewModel.setPolylineVisibility(true)
            }
            else -> {
                /* Toggle polyline button only shown in
                   ExtendedActivity. Indicates that showing
                   OPTIONAL polyline has been triggered */
                viewModel.setPolylineVisibility(!polyline.isVisible)
                polyline.isVisible = !polyline.isVisible
            }
        }
        setToggleButtonIcon(2)
        showHideFirstAndLast(polyline.isVisible)
    }

    open fun showInfoFragment(waypoint: WaypointKt) {
        // Required for Conveniences
        viewModel.setCurrentRoute(currentRoute)
        // Required for Street View
        viewModel.setCurrentWaypoint(waypoint)
        InfoWaypointFragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }

    private fun toggleFaveRoute() {
        val fave = currentRoute.fave
        val itemId = currentRoute.id
        val itemType = ITEM_VIEW_TYPE_ITEM_01
        toggleFave(fave, itemId, itemType)
    }

    fun toggleFave(
        fave: Boolean, itemId: Long, itemType: Int
    ) {
        lifecycleScope.launch {
            val result = toggleFavourite(fave, itemId, itemType,true)
            if (result.first != 0L) {
                /* chCh360Item|currentRoute reinitialised in viewModel. Now
                   whichever one needs to be brought thru to here as well */
                setToggleButtonIcon(0,
                    when (itemType) {
                        in ITEM_VIEW_TYPE_ITEM_02..ITEM_VIEW_TYPE_ITEM_01 -> {
                            currentRoute = viewModel.currentRoute
                            currentRoute.fave
                         }
                        else -> {
                            chCh360Item = viewModel.chCh360Item
                            chCh360Item.fave
                        }
                    }
                )
            }
        }
    }

    override fun toggleFullscreen() {
        toggleFullscreen(
            supportActionBar, binding.recyclerView, binding.root,
            binding.toggleGroup, binding.verticalGuideline
        )
    }
}
