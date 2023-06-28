package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.StreetArtMapAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.StreetArtMapListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.StreetArtMapLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityStreetArtBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoStreetArtFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorSingleIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_TAG
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getStreetArtExtraText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreetArtActivity: CommunityBaseActivity<ActivityStreetArtBinding>(R.layout.activity_street_art) {

    private lateinit var adapter: StreetArtMapAdapter

    override fun onClusterItemClick(item: MarkerItem): Boolean {
        clearSelectedMarker()

        val streetArtKt: StreetArtKt? = adapter.currentList.stream()
            .filter { streetArt ->
                streetArt.latLng == item.position
            }
            .findFirst()
            .orElse(null)
        streetArtKt?.let { streetArt ->
            scrollToPosition(false, streetArt.position)
        }

        /* Returns: true if the listener consumed the event (i.e. the default behavior should
           NOT occur), false otherwise (i.e. the default behavior SHOULD occur). The default
           behavior is for the camera to move to the marker and an info window to appear */
        return false // Must be false to show infoWindow, otherwise it's just recycler pos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_11

        setTitleSubtitle(R.string.street_art_activity_title)

        adapter = StreetArtMapAdapter(
            StreetArtMapListener { streetArt, index ->
                when (index) {
                    0 -> scrollToPosition(streetArt)
                    1 -> showInfoFragment(streetArt)
                    else -> zoomInOutMarker(streetArt.id) // 2
                }
            },
            StreetArtMapLongListener { index ->
                contentDescrToast(index) // Remember: index - 1
                true
            }
        )

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

        viewModel.streetArtKt.observe(this) { streetArt ->
            adapter.submitList(streetArt)
        }

        viewModel.showStreetView.observe(this) { show ->
            if (show) {
                goStreetView()
                viewModel.setShowStreetView(false)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        val streetArt = adapter.currentList
        if (streetArt.isNotEmpty()) {
            var streetArtKt: StreetArtKt? = null
            val itemId = viewModel.currentStreetArtId
            if (itemId != 0L) {
                streetArtKt = getStreetArt(itemId)
                if (streetArtKt != null) {
                    scrollToPosition(true, streetArtKt.position)
                }
            }

            val context = this@StreetArtActivity
            clusterRenderer = ColorSingleIconRenderer(
                getColorId(),
                context,
                R.drawable.ic_baseline_brush_default_30,
                map, clusterManager
            )
            clusterManager.renderer = clusterRenderer
            for (i in streetArt.indices) {
                val snippet =
                    getStreetArtExtraText(context,true, streetArt[i])
                val markerItem = getMarkerItem(
                    streetArt[i].latLng, streetArt[i].title, snippet
                )
                allMarkerItems[streetArt[i].id] = markerItem
                clusterManager.addItem(markerItem)
                if (i == streetArt.size.minus(1)) {
                    when (itemId) {
                        0L -> zoomOutMarkers(true)
                        else -> {
                            if (streetArtKt != null) {
                                zoomInOutMarker(streetArtKt.id,true)
                            }
                        }
                    }
                    initClusterManager()
                }
            }
        }
    }

    private fun getStreetArt(itemId: Long): StreetArtKt? {
        return adapter.currentList.stream()
            .filter { item ->
                item.id == itemId
            }
            .findFirst()
            .orElse(null)
    }

    // Called from viewModel.showStreetView observer (see above)
    private fun goStreetView() {
        val site = viewModel.currentHeritageSite
        when (site.angle) {
            // Shouldn't actually be able to get this far, so just for safety
            -1 -> fancyToast(this,4, R.string.no_street_view_a)
            else -> {
                val coords = Coords(
                    String.format(
                        getString(R.string.app_title),
                        site.name
                    ),
                    String.format(
                        getString(R.string.street_view_address_subtitle),
                        site.address
                    ),
                    site.latLng,
                    site.angle,
                    getWaypointSubtitleText(1,2)
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

    private fun scrollToPosition(init: Boolean, position: Int?) {
        position?.let { pos -> // init single | onClusterItemClick
            when (init) {
                true -> recyclerView.scrollToPosition(pos)
                else -> scrollToPosition(pos, adapter.currentList.size)
            }
        }
    }

    private fun scrollToPosition(
        streetArt: StreetArtKt // recyclerView next/previous arrow
    ) {
        val size = adapter.currentList.size
        if (size > 1) {
            streetArt.position?.let { pos ->
                streetArt.size?.let { qty ->
                    recyclerView.smoothScrollToPosition(
                        when (pos) {
                            qty.minus(1) -> pos.minus(1)
                            else -> pos.plus(1)
                        }
                    )
                }
            }
        }
    }

    private fun showInfoFragment(streetArt: StreetArtKt) {
        viewModel.setStreetArtItem(streetArt)
        InfoStreetArtFragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }
}
