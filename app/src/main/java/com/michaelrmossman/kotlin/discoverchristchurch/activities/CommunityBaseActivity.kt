package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.CancelableCallback
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.button.MaterialButton
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FavouriteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PolyPoint
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.enableDisableMyLocation
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapCameraToView
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapLocation
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setMapLocationAndType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.zoomInMarker
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.initViewTreeObserver
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isXLargeScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

/* Extended by the various community activities ...
   All but BikeTracks, DogParks, HeritageSites and
   StreetArt activities use ActivityCommonBinding */
abstract class CommunityBaseActivity<T: ViewDataBinding>(
    @LayoutRes layoutResId: Int
) : MapsBaseActivity<T>(layoutResId),
    OnClusterItemClickListener<MarkerItem>,
    OnInfoWindowClickListener {

    val clusterManager: ClusterManager<MarkerItem> by lazy {
        ClusterManager<MarkerItem>(this, map)
    }

    lateinit var clusterRenderer: DefaultClusterRenderer<MarkerItem>
    lateinit var allMarkerItems: HashMap<Long, MarkerItem>
    var allPolygons: HashMap<Long, Polygon>? = null
    lateinit var allPolyPoints: MutableList<PolyPoint>
    var communityIndex by Delegates.notNull<Int>()
    lateinit var favourite: FavouriteKt

    override fun onClusterItemClick(item: MarkerItem): Boolean {
        clearSelectedMarker()
        return false // Refer to RoutesActivity re explanation
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        allMarkerItems = HashMap()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_common_activities, menu)
        actionMenu = menu
        return true
    }

    override fun onInfoWindowClick(marker: Marker?) {
        marker?.hideInfoWindow()
        clearSelectedMarker()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap) // New

        when (getCommunityIndexItemId(communityIndex)) {
            0L -> { // 0L is the relevant itemId, not index
                setMapLocation(communityIndex, map)
            }
            else -> setMapLocationAndType(map,null)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomOutMarkers(false)
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

    override fun onRestart() {
        super.onRestart()
        enableDisableMyLocation(sharedPrefs
            .getBoolean(SHOW_MY_LOCATION_PREF,true))
    }

    override fun enableDisableMyLocation(enable: Boolean) {
        enableDisableMyLocation(this, enable, map, binding.root)
    }

    fun enableZoomMenu() {
        actionMenu?.findItem(R.id.menu_show_all)?.isEnabled = true
    }

    private fun getHashMapSize(): Int {
        return when (communityIndex) {
            3 -> {
                when (allPolygons) {
                    null -> 0
                    else -> allPolygons!!.size // Double bang
                }
            }
            else -> {
                when (this::allMarkerItems.isInitialized) {
                    true -> allMarkerItems.size
                    else -> 0
                }
            }
        }
    }

    private fun getCommunityIndexItemId(index: Int): Long {
        with (viewModel) {
            return when (index) {
                ITEM_VIEW_TYPE_ITEM_12 -> currentUrbanPlayId
                ITEM_VIEW_TYPE_ITEM_11 -> currentStreetArtId
                ITEM_VIEW_TYPE_ITEM_10 -> currentParkId
                ITEM_VIEW_TYPE_ITEM_9 -> currentConvenienceId
                ITEM_VIEW_TYPE_ITEM_8 -> currentBikeTrackId
                ITEM_VIEW_TYPE_ITEM_7 -> currentHeritageSiteId
                ITEM_VIEW_TYPE_ITEM_6 -> currentFruitTypeId
                ITEM_VIEW_TYPE_ITEM_5 -> currentFreeWiFiId
                ITEM_VIEW_TYPE_ITEM_4 -> currentFountainId
                ITEM_VIEW_TYPE_ITEM_3 -> currentDogParkId
                ITEM_VIEW_TYPE_ITEM_2 -> currentFacilityId
                ITEM_VIEW_TYPE_ITEM_1 -> currentBatteryRecyclerId
                else -> Long.MIN_VALUE
            }
        }
    }

    fun getMarkerItem(
        latLng: LatLng, title: String,
        snippet: String, color: String? = null
    ) : MarkerItem {
        return MarkerItem(
            latLng,
            title,
            snippet,
            color
        )
    }

    private fun getMarker(markerItem: MarkerItem): Marker? {
        return when (::clusterRenderer.isInitialized) {
            true -> clusterRenderer.getMarker(markerItem)
            else -> null
        }
    }

    private fun getSubtitle(subtitle: String): String {
        return String.format(
            getString(R.string.common_map_subtitle),
            subtitle
        )
    }

    private fun getTitle(title: String): String {
        return String.format(
            getString(R.string.app_title),
            title
        )
    }

    fun initClusterManager(infoWindow: Boolean = false) {
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

        // BikeTracks|DogParks activities don't use clusterManager
        val context = this@CommunityBaseActivity
        clusterManager.setOnClusterItemClickListener(context)

        if (infoWindow) { // Called by FruitTrees|Parks activities
            clusterManager.markerCollection.apply {
                setOnInfoWindowClickListener(context)
            }
        }
    }

    fun setFavouriteButton(
        button: Button,
        itemId: Long, itemType: Int, fave: Boolean,
        init: Boolean = false
    ) {
        favourite = FavouriteKt(itemId, itemType, fave)
        setToggleButtonIcon(button)
        if (init) {
            button.setOnClickListener {
                toggleFave(button)
            }
        }
    }

    open fun setTitleSubtitle(stringId: Int) {
        setTitleSubtitle(getString(stringId))
    }

    open fun setTitleSubtitle(string: String) {
        supportActionBar?.title = String.format(
            getString(R.string.app_title), // Multi
            string
        )

        supportActionBar?.subtitle =
            getString(R.string.community_activity_subtitle)
    }

    fun setTitleSubtitle(title: String, subtitle: String) {
        supportActionBar?.title = getTitle(title) // Single
        supportActionBar?.subtitle = getSubtitle(subtitle)
    }

    private fun setToggleButtonIcon(button: Button, enable: Boolean = true) {
        val drawableId = when (enable) {
            false -> R.drawable.ic_baseline_bookmark_border_selector_24
            else -> {
                when (favourite.fave) {
                    true -> R.drawable.ic_outline_bookmark_remove_translucent_24
                    else -> R.drawable.ic_outline_bookmark_add_translucent_24
                }
            }
        }
        (button as MaterialButton).apply {
            isEnabled = enable
            isVisible = true
            setIconResource(drawableId)
        }
    }

    fun toggleFave(button: Button) {
        lifecycleScope.launch {
            val fave = favourite.fave
            val itemId = favourite.itemId
            val itemType = favourite.itemType

            val result = toggleFavourite(fave, itemId, itemType)
            if (result.first != 0L) {
                setFavouriteButton(
                    button, itemId, itemType,result.first > 0L
                )
            }
        }
    }

    fun zoomInMarker(latLng: LatLng) {
        zoomInMarker(
            true, latLng, map, CAMERA_COMMUNITY_ZOOM
        )
    }

    private fun zoomInMarkers(
        bounds: LatLngBounds,
        init: Boolean,
        padding: Int
    ) {
        setMapCameraToView(
            bounds, init, map, getMapFragment().view, padding
        )
    }

    fun zoomInOutMarker(id: Long, init: Boolean = false) {
        val markerItem = allMarkerItems[id]
        markerItem?.let { item ->
            zoomInMarker(
                init, item.position, map,
                CAMERA_COMMUNITY_ZOOM,
                object: CancelableCallback {
                    override fun onCancel() {}
                    override fun onFinish() {
                        lifecycleScope.launch {
                            // Wait for cluster item to "settle"
                            delay(100L)
                            val marker = getMarker(item)
                            marker?.showInfoWindow()
                        }
                    }
                }
            )
        }
    }

    fun zoomInOutMarkers(init: Boolean, padding: Int) {
        val actualPadding = when (isXLargeScreen()) {
            true -> padding / 2
            else -> padding
        }
        val bounds = LatLngBounds.builder()
        for (markerItem in allMarkerItems.values) {
            bounds.include(markerItem.position)
        }
        try {
            zoomInMarkers(bounds.build(), init, actualPadding)
        } catch (e: IllegalStateException) {
            initViewTreeObserver(
                { zoomInMarkers(bounds.build(), init, actualPadding) },
                supportFragmentManager, R.id.fragment_map
            )
        }
    }

    fun zoomOutMarkers(init: Boolean) {
        val padding = when (init) {
            true -> CAMERA_MEDIUM_PADDING
            else -> CAMERA_SMALL_PADDING
        }
        zoomInOutMarkers(init, padding)
    }

    fun zoomInOutPolygons(init: Boolean, padding: Int) {
        if (!init) { clearSelectedMarker() }

        val bounds = LatLngBounds.builder()
        allPolyPoints.forEach { polyPoint ->
            bounds.include(polyPoint.latLng)
        }
        try {
            zoomInPolygon(bounds.build(), init, padding)
        } catch (e: IllegalStateException) {
            initViewTreeObserver(
                /* As well as map not ready, also allows for possible Runtime
                   Exception: View size is too small after padding is applied */
                { zoomInPolygon(bounds.build(), init, (padding.times(0.75)).toInt()) },
                supportFragmentManager, R.id.fragment_map
            )
        }
    }

    fun zoomInPolygon(
        bounds: LatLngBounds,
        init: Boolean,
        padding: Int
    ) {
        setMapCameraToView(
            bounds, init, map, getMapFragment().view, padding
        )
    }

    fun zoomOutPolygons(init: Boolean) {
        zoomInOutPolygons(init, CAMERA_SMALL_PADDING)
    }
}
