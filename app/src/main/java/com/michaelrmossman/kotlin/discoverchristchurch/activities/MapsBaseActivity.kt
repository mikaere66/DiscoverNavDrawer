package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.showPoiInfoWindow
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getPermissionsState

// Extended by all Maps activities, directly or indirectly
abstract class MapsBaseActivity<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : CommonDialogActivity<T>(layoutResId), OnMapReadyCallback {

    lateinit var optPolylines: HashMap<Long, Polyline> // Base|BikeTracks activities
    lateinit var layoutManager: LinearLayoutManager
    lateinit var waypoints: List<WaypointKt>
    lateinit var recyclerView: RecyclerView
    var selectedMarker: Marker? = null
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val context = this@MapsBaseActivity
        val mapFragment = getMapFragment()
        lifecycleScope.launchWhenCreated {
            map = mapFragment.awaitMap()
            map.awaitMapLoad()
            map.uiSettings.apply {
                isMapToolbarEnabled = true
                isZoomControlsEnabled = true
            }
            enableDisableMyLocation(sharedPrefs
                .getBoolean(SHOW_MY_LOCATION_PREF, true))
            val mapType =
                sharedPrefs.getInt(MAP_TYPE_PREF, MAP_TYPE_DEFAULT)
            setMapType(mapType, map)
            mapFragment.getMapAsync(context)
        }

        onBackPressedDispatcher.addCallback(this,
                object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (supportActionBar?.isShowing == false) {
                    true -> toggleFullscreen()
                    else -> {
                        cancelCoroutine()
                        finish()
                    }
                }
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val context = this@MapsBaseActivity
        map.apply {
            setOnPoiClickListener { poi ->
                clearSelectedMarker()
                selectedMarker = showPoiInfoWindow(
                    context, map, poi
                )
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        /* Relates to menu_common_activities,
           menu_routes_activity and menu_waypoints */
        when (item.itemId) {
            R.id.menu_show_location -> {
                val isChecked = !item.isChecked
                item.isChecked = isChecked
                if (sharedPrefs.edit().putBoolean(SHOW_MY_LOCATION_PREF,
                        isChecked).commit()) {
                    enableDisableMyLocation(isChecked)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean { // Was super
        val mapInitialized = this::map.isInitialized
        val permissions =
            getPermissionsState(this, REQUEST_SHOW_MY_LOCATION_PERMISSION)
        menu.findItem(R.id.menu_show_all)?.isEnabled = mapInitialized
        menu.findItem(R.id.menu_show_location)?.apply {
            isChecked = permissions == 2 &&
                sharedPrefs.getBoolean(SHOW_MY_LOCATION_PREF, true)
            isEnabled = mapInitialized && permissions == 2
        }
        menu.findItem(R.id.menu_show_zoom)?.apply {
            isChecked =
                sharedPrefs.getBoolean(SHOW_OPTIONAL_BUTTONS_PREF, true)
            isEnabled = mapInitialized
        }
        menu.findItem(R.id.menu_layers)?.isEnabled = mapInitialized

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onRestart() {
        super.onRestart()

        enableDisableMyLocation(sharedPrefs
            .getBoolean(SHOW_MY_LOCATION_PREF, true))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putDouble(INSTANCE_STATE_KEY_LAT, map.cameraPosition.target.latitude)
        outState.putDouble(INSTANCE_STATE_KEY_LON, map.cameraPosition.target.longitude)
        outState.putFloat(INSTANCE_STATE_KEY_ZOOM, map.cameraPosition.zoom)

        super.onSaveInstanceState(outState)
    }

    open fun cancelCoroutine() {}

    fun clearSelectedMarker() {
        selectedMarker?.remove()
        selectedMarker = null
    }

    open fun enableDisableMyLocation(enable: Boolean) {}

    open fun enableDisableShowAllMenu() {
        actionMenu?.findItem(R.id.menu_show_all)?.isEnabled =
            waypoints.size > 1
    }

    fun getMapFragment(): SupportMapFragment {
        return supportFragmentManager
            .findFragmentById(R.id.fragment_map) as SupportMapFragment
    }

    open fun restorePolylines() {}

    fun scrollToPosition(waypoint: WaypointKt) {
        recyclerView.smoothScrollToPosition(when (waypoint.position) {
                waypoint.size.minus(1) -> waypoint.position.minus(1)
                else -> waypoint.position.plus(1)
            }
        )
    }

    fun scrollToPosition(position: Int, size: Int) {
        if (position != -1) {
            val current =
                layoutManager.findFirstCompletelyVisibleItemPosition()

            val staggered = when (size) {
                in 1..SCROLL_TO_THRESHOLD.plus(1) -> false
                else -> when (position > current) {
                    true -> position.minus(current) > SCROLL_TO_THRESHOLD
                    else -> current.minus(position) > SCROLL_TO_THRESHOLD
                }
            }

            if (staggered) {
                recyclerView.scrollToPosition(
                    when (position > current) {
                        true -> position.minus(SCROLL_TO_THRESHOLD)
                        else -> position.plus(SCROLL_TO_THRESHOLD)
                    }
                )
            }

            recyclerView.smoothScrollToPosition(position)
        }
    }

    open fun toggleFullscreen() {
        supportActionBar?.let {
            when (it.isShowing) {
                true -> it.hide()
                else -> it.show()
            }
        }
    }
}
