package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolylineClickListener
import com.google.android.gms.maps.model.*
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BikeTrackMapListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BikeTrackMapLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BikeTracksMapAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityBikeTracksBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeCoordsKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoBikeTrackFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getMarkerPair
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.zoomInOutPolylines
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getBikeTrackColorId
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BikeTracksActivity: CommunityBaseActivity<ActivityBikeTracksBinding>(
    R.layout.activity_bike_tracks), OnPolylineClickListener {

    private lateinit var allBikeCoords: MutableList<BikeCoordsKt>
    private lateinit var polylines: HashMap<Long, Polyline>
    private lateinit var adapter: BikeTracksMapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_8

        adapter = BikeTracksMapAdapter(
            BikeTrackMapListener { bikeTrack, index ->
                when (index) {
                    0 -> scrollToPosition(bikeTrack)
                    1 -> showInfoFragment(bikeTrack)
                    else -> zoomInPolyline(bikeTrack,false) // 2
                }
            },
            BikeTrackMapLongListener { index ->
                contentDescrToast(index) // Remember: index - 1
                true
            }
        )

        setTitleSubtitle(R.string.bike_tracks)

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

        viewModel.bikeTracks.observe(this) { tracks ->
            adapter.submitList(tracks)
        }
    }

    override fun setTitleSubtitle(stringId: Int) {
        supportActionBar?.title = String.format(
            getString(R.string.app_title),
            getString(stringId)
        )

        supportActionBar?.subtitle = String.format(
            getString(R.string.common_map_subtitle),
            getString(R.string.bike_tracks_activity_subtitle)
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        var bikeTrack: BikeTrackKt? = null
        val itemId = viewModel.currentBikeTrackId
        if (itemId != 0L) {
            bikeTrack = getBikeTrack(itemId)
            if (bikeTrack != null) {
                scrollToPosition(bikeTrack.id,true)
            }
        }

        allBikeCoords = mutableListOf()
        map.setOnPolylineClickListener(this)
        optPolylines = HashMap() // Just initialise, for now
        polylines = HashMap()

        val bikeTracks = adapter.currentList
        if (bikeTracks.isNotEmpty()) {
            lifecycleScope.launch {
                for (i in bikeTracks.indices) {
                    val bikeCoords =
                        viewModel.getBikeCoordsByTrackId(bikeTracks[i].id)
                    allBikeCoords.addAll(bikeCoords)
                    addPolyline(bikeCoords, bikeTracks[i])
                    if (i == bikeTracks.size.minus(1)) {
                        when (itemId) {
                            0L ->  {
                                zoomInOutPolylines(
                                    true, polylines.values
                                )
                            }
                            else -> {
                                if (bikeTrack != null) {
                                    zoomInPolyline(bikeTrack,true)
                                    with(bikeTrack) {
                                        setFavouriteButton(binding.toggleFaveButton,
                                        id, ITEM_VIEW_TYPE_ITEM_8, fave,true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomInOutPolylines(false, polylines.values)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPolylineClick(polyline: Polyline?) {
        polyline?.let { poly ->
            val bikeTrackId = poly.tag as Long
            scrollToPosition(bikeTrackId,false)
            getBikeTrack(bikeTrackId)?.let { bikeTrack ->
                zoomInPolyline(bikeTrack,false)
            }
        }
    }

    private fun addPolyline(
        bikeCoords: List<BikeCoordsKt>,
        bikeTrack: BikeTrackKt,
        optional: Boolean = false
    ) {
        val index = bikeTrack.shared
        // Hybrid and Satellite maps are dark, so the green is lighter
        val colors = when (map.mapType) {
            3 -> R.array.bike_track_colors_alt // Hybrid
            2 -> R.array.bike_track_colors // Terrain
            1 -> R.array.bike_track_colors_alt // Satellite
            else -> R.array.bike_track_colors // Normal
        }
        val colorId = getBikeTrackColorId(this, index, colors)

        val strokeWidth = when (optional) {
            true -> POLYLINE_OBESE_STROKE_WIDTH
            else -> POLYLINE_LIGHT_STROKE_WIDTH
        }

        val polylineOptions = PolylineOptions()
            .color(colorId)
            .jointType(JointType.ROUND)
            .width(strokeWidth)
        bikeCoords.forEach {
            polylineOptions.add(it.latLng)
        }

        map.addPolyline(polylineOptions).apply {
            isClickable = true
            tag = bikeTrack.id

            if (index > 0) {
                pattern = listOf(
                    Gap(POLYLINE_PATTERN_GAP_LENGTH),
                    Dot(),
                    Gap(POLYLINE_PATTERN_GAP_LENGTH)
                )
            }

            when (optional) {
                false -> polylines[bikeTrack.id] = this
                else -> {
                    val capsPair = getCapsPair()
                        startCap = capsPair.first
                        endCap = capsPair.second
                    optPolylines[bikeTrack.id] = this
                }
            }
        }
    }

    private fun getBikeTrack(itemId: Long): BikeTrackKt? {
        return adapter.currentList.stream()
            .filter { item ->
                item.id == itemId
            }
            .findFirst()
            .orElse(null)
    }

    private fun getCapsPair(): Pair<CustomCap, CustomCap> {
        val startPair =
            getMarkerPair(0,0,1,2)
        val startCap = CustomCap(
            BitmapHelper.vectorToBitmap(
                this,
                startPair.second,
                ContextCompat.getColor(
                    this, startPair.first
                )
            )
        )

        val endPair =
            getMarkerPair(1,0,2,2)
        val endCap = CustomCap(
            BitmapHelper.vectorToBitmap(
                this,
                endPair.second,
                ContextCompat.getColor(
                    this, endPair.first
                )
            )
        )

        return Pair(startCap, endCap)
    }

    private fun hideOptionalPolylines() {
        if (optPolylines.isNotEmpty()) {
            for (poly in optPolylines.values) {
                poly.isVisible = false
            }
        }
    }

    private fun scrollToPosition(bikeTrack: BikeTrackKt) {
        bikeTrack.position?.let { pos ->
            bikeTrack.size?.let { qty ->
                recyclerView.smoothScrollToPosition(when (pos) {
                        qty.minus(1) -> pos.minus(1)
                        else -> pos.plus(1)
                    }
                )
            }
        }
    }

    private fun scrollToPosition(bikeTrackId: Long, init: Boolean) {
        val index = adapter.currentList.indexOfFirst { bikeTrack ->
            bikeTrack.id == bikeTrackId
        }
        if (index != -1) {
            when (init) {
                true -> recyclerView.scrollToPosition(index)
                else -> scrollToPosition(index, adapter.currentList.size)
            }
        }
    }

    private fun showInfoFragment(bikeTrack: BikeTrackKt) {
        viewModel.setBikeTrackItem(bikeTrack)
        InfoBikeTrackFragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }

    private fun showNormalPolylines() {
        for (poly in polylines.values) {
            poly.isVisible = true
        }
    }

    private fun showOptionalPolyline(bikeTrack: BikeTrackKt) {
        hideOptionalPolylines()
        showNormalPolylines()

        polylines[bikeTrack.id]?.isVisible = false

        when (optPolylines.containsKey(bikeTrack.id)) {
            true -> optPolylines[bikeTrack.id]?.isVisible = true
            else -> {
                lifecycleScope.launch {
                    val bikeCoords =
                        viewModel.getBikeCoordsByTrackId(bikeTrack.id)
                    addPolyline(bikeCoords, bikeTrack,true)
                }
            }
        }
    }

    private fun zoomInPolyline(
        bikeTrack: BikeTrackKt,
        init: Boolean
    ) {
        val polyline = polylines[bikeTrack.id]
        polyline?.let { poly ->
            showOptionalPolyline(bikeTrack)
            zoomInOutPolylines(init, mutableListOf(poly))
        }
    }

    private fun zoomInOutPolylines(
        init: Boolean,
        polys: MutableCollection<Polyline>
    ) {
        zoomInOutPolylines(
            init, map, getMapFragment().view, polys
        )
    }
}
