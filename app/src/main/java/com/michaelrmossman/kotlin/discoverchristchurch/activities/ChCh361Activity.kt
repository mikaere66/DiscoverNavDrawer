package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoChCh361Fragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getAccessPointMarkerColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getChCh360Polyline
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getMapType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapType
import kotlinx.coroutines.launch

class ChCh361Activity: BaseActivity() {

    private lateinit var polylineList: MutableList<Polyline>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val subtitleId = R.string.waypoints_ch_ch_361_subtitle
        supportActionBar?.subtitle = getString(subtitleId)

        binding.buttonsLayout.toggleFaveButton.apply {
            setOnClickListener { toggleFaveChCh360() }
        }

        lifecycleScope.launch {
            val itemId = viewModel.chCh360ItemId
            chCh360Item = viewModel.getChCh360ItemById(itemId)
            supportActionBar?.title = String.format(
                getString(R.string.app_title),
                chCh360Item.leg
            )

            waypoints = viewModel.getAccessPointsListByChCh360Id(itemId)

            adapter.submitList(waypoints)

            if (waypoints.size > 1) {
                setMapCameraToView(
                    true, map, getMapFragment().view, waypoints
                )
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        addChCh360Polyline()
        if (waypoints.isNotEmpty()) {
            addMapMarkers(true)

            enableDisableOptions(
                when (sharedPrefs.getBoolean(SHOW_OPTIONAL_BUTTONS_PREF,
                        true)) {
                    true -> -1 // Note negative
                    else -> 0
                }
            )

            setToggleButtonIcon(0, chCh360Item.fave)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomInOutMarkersAndPolylines() // false
                true
            }
            R.id.menu_show_zoom -> {
                val isChecked = !item.isChecked
                item.isChecked = isChecked
                if (sharedPrefs.edit().putBoolean(SHOW_OPTIONAL_BUTTONS_PREF,
                        isChecked).commit()) {
                    enableDisableOptions(
                        when (isChecked) {
                            true -> -1 // Note negative
                            else -> 0
                        }
                    )
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun addChCh360Polyline() {
        lifecycleScope.launch {
            polylineList = mutableListOf()

            /* Until loop excludes the end index element
               e.g. 0, 1, 2, for Avonhead Gardens */
            for (i in 0 until chCh360Item.trackCount) {
                val coordinates =
                    viewModel.getChCh360CoordsListByLegId(chCh360Item.id, i)
                val poly =
                    getChCh360Polyline(chCh360Item, coordinates,
                        map, POLYLINE_LIGHT_STROKE_WIDTH)
                /* Avonhead Gardens leg is actually made up
                   of 3 polylines, so rather than mess with
                   the .gpx file, we just work around it */
                polylineList.add(poly)

//                if (i == chCh360Item.trackCount.minus(1)) {
//                    zoomInOutMarkersAndPolylines(true)
//                }
            }
        }
    }

    override fun getMapType() {
        alertDialog =
            getMapType(this, map, binding.root, ::setMapType)
        alertDialog?.show()
    }

    private fun setMapType(mapType: Int) {
        val newType = setMapType(mapType, map,true)
        if (newType) { // Change from dark to light markers, or vice-versa
            for (i in allMarkers.indices) {
                val colorId =
                    getAccessPointMarkerColorId(mapType, i, allMarkers.size)
                allMarkers[i].setIcon(
                    BitmapHelper.vectorToBitmap(
                        this,
                        R.drawable.ic_baseline_local_parking_default_30,
                        ContextCompat.getColor(this, colorId)
                    )
                )
            }
        }
    }

    override fun showInfoFragment(waypoint: WaypointKt) {
        // Required for Street View
        viewModel.setCurrentWaypoint(waypoint)
        InfoChCh361Fragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }

    private fun toggleFaveChCh360() {
        val fave = chCh360Item.fave
        val itemId = chCh360Item.id
        val itemType = ITEM_VIEW_TYPE_ITEM_00
        toggleFave(fave, itemId, itemType)
    }

    private fun zoomInOutMarkersAndPolylines() { // init: Boolean
        if (polylineList.isNotEmpty()) {
            val bounds = LatLngBounds.builder()
            // Include all the map markers ...
            waypoints.forEach { waypoint ->
                bounds.include(
                    waypoint.latLng
                )
            }

            // ... plus all of polyline points
            for (i in polylineList.indices) {
                val points = polylineList[i].points
                points?.let { list ->
                    list.forEach {
                        bounds.include(
                            LatLng(it.latitude, it.longitude)
                        )
                    }
                }

                if (i == polylineList.size.minus(1)) {
                    setMapCameraToView(
                        bounds.build(),false, map, getMapFragment().view
                    )
                }
            }
        }
    }
}
