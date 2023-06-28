package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.model.*
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ParkSearchAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ParkSearchListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ParkSearchDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PolyPoint
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.BitmapHelper.vectorToBitmap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ParksActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common),
    OnInfoWindowClickListener, OnPolygonClickListener {

    private lateinit var hiddenMarkers: MutableList<Marker>
    private lateinit var parks: List<ParkKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_10

        if (viewModel.currentParkId == 0L) {
            setTitle(null)
            setSubtitle(null)
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if (!this::hiddenMarkers.isInitialized) {
            hiddenMarkers = mutableListOf()
        }

        marker?.apply {
            hiddenMarkers.add(this)
            hideInfoWindow()
            isVisible = false
            showOptionalPolygon(this.position)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        allPolyPoints = mutableListOf()
        allPolygons = HashMap()
        map.setOnPolygonClickListener(this)
        lifecycleScope.launch {
            when (val parkId = viewModel.currentParkId) {
                0L -> {
                    parks = viewModel.getParksList()
                    if (parks.isNotEmpty()) {
                        val polyPoints = parks.map { PolyPoint(it.id, it.latLng) }
                        allPolyPoints.addAll(polyPoints)
                        addClusteredMarkers()
                    }
                }
                else -> {
                    viewModel.getParkById(parkId).let { park ->
                        parks = listOf(park)
                        setTitle(park.name)
                        setSubtitle(park.type)
                        showSinglePolygon(park,true)
                        with(park) {
                            setFavouriteButton(binding.toggleFaveButton,
                            id, ITEM_VIEW_TYPE_ITEM_10, fave,true)
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomOutPolygons(false)
                true
            }
            R.id.menu_hide_polygons -> {
                hidePolygons()
                true
            }
            R.id.menu_search -> {
                getParkName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPolygonClick(polygon: Polygon?) {
        polygon?.let { poly ->
            val context = this@ParksActivity
            /* Debug, as some markers appear out at sea,
               e.g. Southshore 1/2 RRZ Land. Any markers
               that do NOT have their original coords are
               marked as such in park_environments_table */
            val polyCentre = poly.getCenter()
            val parkId = poly.tag as Long
            val polyPark = getPark(parkId)
            polyPark?.let { park ->
                clearSelectedMarker()
                selectedMarker = map.addMarker(MarkerOptions().apply {
                    position(polyCentre)
                    title(park.name)
                    snippet(park.type)
                    icon(
                        vectorToBitmap(
                            context,
                            R.drawable.ic_baseline_circle_default_8,
                            ContextCompat.getColor(
                                context,
                                android.R.color.transparent
                            )
                        )
                    )
                })
                selectedMarker?.showInfoWindow()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu) // Init default

        return when (viewModel.currentParkId) {
            0L -> {
                menu.apply {
                    findItem(R.id.menu_search)?.apply {
                        isVisible = true
                    }
                    findItem(R.id.menu_hide_polygons)?.apply {
                        isVisible = true
                    }
                    allPolygons?.let { polygons ->
                        for (poly in polygons.values) {
                            if (poly.isVisible) {
                                actionMenu?.findItem(R.id.menu_hide_polygons)?.isEnabled =
                                    true
                                // Exit loop if found
                                return true
                            }
                        }
                    } // Else disable "Hide Polygons" menuItem
                    actionMenu?.findItem(R.id.menu_hide_polygons)?.isEnabled = false
                }
                true
            }
            else -> false
        }
    }

    private fun addClusteredMarkers() {
        val context = this@ParksActivity
        clusterRenderer = ColorMultiIconRenderer(
            context,
            R.drawable.ic_baseline_nature_default_30,
            map, clusterManager
        )
        clusterManager.renderer = clusterRenderer
        for (i in parks.indices) {
            val markerItem = getMarkerItem(
                parks[i].latLng, parks[i].name,
                parks[i].type, parks[i].color
            )
            allMarkerItems[parks[i].id] = markerItem
            clusterManager.addItem(
                markerItem
            )
            if (i == parks.size.minus(1)) {
                initInfoWindowAdapter()
                zoomOutPolygons(true)
            }
        }
    }

    private fun getPark(itemId: Long): ParkKt? {
        return parks.stream()
            .filter { item ->
                item.id == itemId
            }
            .findFirst()
            .orElse(null)
    }

    private fun getParkName() {
        val adapter = ParkSearchAdapter(
            ParkSearchListener { park ->
                alertDialog?.dismiss()
                zoomInOutMarker(park.id)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        val parkSearchDialogBinding = ParkSearchDialogBinding.inflate(layoutInflater)
        parkSearchDialogBinding.apply {
            recyclerView.adapter = adapter
            parkSearchContainer.editText?.doOnTextChanged { cs, _, _, _ ->
                val filterTerm =
                    when (cs?.length) {
                        in 0..1 -> null
                        else -> cs?.trim()?.toString()
                    }
                if (viewModel.parkFilterTerm.value != filterTerm) {
                    viewModel.setParkFilterTerm(filterTerm)
                }
            }
        }

        viewModel.parksFilteredBy.observe(this) { parks ->
            adapter.submitList(parks)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_nature_people_black_24)
            setTitle(R.string.search_park_name_title)
            setView(parkSearchDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setOnDismissListener { viewModel.setParkFilterTerm(null) }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    private suspend fun getPolyPoints(park: ParkKt): List<LatLng> {
        val polyPoints = mutableListOf<LatLng>()

        val environmentsSplit = park.environmentIds.split(",")
        for (i in environmentsSplit.indices) {
            val environmentId = environmentsSplit[i].trim().toLong()
            val environmentList =
                viewModel.getEnvironmentListByParkId(environmentId)
            val points = environmentList.map { it.latLng }
            polyPoints.addAll(points)
        }

        return polyPoints
    }

    private fun hidePolygons() {
        allPolygons?.let { polygons ->
            for (poly in polygons.values) {
                poly.isVisible = false
            }
        }

        if (this::hiddenMarkers.isInitialized) {
            hiddenMarkers.forEach { marker ->
                marker.isVisible = true
            }

            hiddenMarkers = mutableListOf()
        }
    }

    private fun initInfoWindowAdapter() {
        initClusterManager(true)

        clusterManager.markerCollection.apply {
            setInfoWindowAdapter(
                TextIconInfoWindowAdapter(
                    this@ParksActivity,
                    R.drawable.ic_baseline_shape_line_default_24,
                    R.string.show_polygon
                )
            )
        }
    }

    private fun setSubtitle(type: String?) {
        lifecycleScope.launch {
            val subtitle = when (type) {
                null -> {
                    val notSelected =
                        viewModel.getParkTypesNotSelectedCount()
                            .plus(1) // Note: NOT and PLUS
                    resources.getQuantityString( // All/Selected
                        R.plurals.parks_map_subtitle,
                        notSelected
                    )
                }
                else -> {
                    String.format(
                        getString(
                            R.string.common_map_subtitle // Single
                        ),
                        type
                    )
                }
            }
            supportActionBar?.subtitle = subtitle
        }
    }

    private fun setTitle(environment: String?) {
        val title = String.format(
            getString(R.string.app_title),
            when (environment) {
                null -> getString(R.string.public_parks)
                else -> environment
            }
        )
        supportActionBar?.title = title
    }

    private fun showOptionalPolygon(latLng: LatLng) {
        val park: ParkKt? = parks.stream()
            .filter { park ->
                latLng == park.latLng
            }
            .findFirst()
            .orElse(null)
        park?.let { filtered ->
            lifecycleScope.launch {
                showSinglePolygon(filtered,false)

                val polyPoints = getPolyPoints(filtered)
                val bounds = LatLngBounds.builder()
                polyPoints.forEach { polyPoint ->
                    bounds.include(polyPoint)
                }

                zoomInPolygon(
                    bounds.build(),false, CAMERA_SMALL_PADDING
                )
            }
        }
    }

    private suspend fun showSinglePolygon(park: ParkKt, init: Boolean) {
        val markerItem = getMarkerItem(
            park.latLng, park.name,
            park.type, park.color
        )
        allMarkerItems[park.id] = markerItem

        val environmentsSplit = park.environmentIds.split(",")
        val parkTypeBorder = Color.parseColor(park.border)
        val parkTypeColor = Color.parseColor(park.color)
        for (i in environmentsSplit.indices) {
            val environmentId = environmentsSplit[i].trim().toLong()
            val environmentList =
                viewModel.getEnvironmentListByParkId(environmentId)
            val polyPoints = environmentList.map { PolyPoint(park.id, it.latLng) }
            allPolyPoints.addAll(polyPoints)

            val polygonOptions = PolygonOptions()
            polyPoints.forEach { polyPoint ->
                polygonOptions.add(polyPoint.latLng)
            }

            allPolygons?.let { polygons ->
                polygons[environmentId] =
                    map.addPolygon(polygonOptions).apply {
                        isClickable = true
                        fillColor = parkTypeColor
                        strokeColor = parkTypeBorder
                        strokeWidth = 14F
                        tag = park.id
                    }
            }

            if (init && i == environmentsSplit.size.minus(1)) {
                zoomInOutPolygons(true, CAMERA_SMALL_PADDING)
            }
        }
    }
}
