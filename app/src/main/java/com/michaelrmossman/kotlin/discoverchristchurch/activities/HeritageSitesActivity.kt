package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.HeritageSiteMapListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.HeritageSiteMapLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.HeritageSitesMapAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityHeritageSitesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.MarkerItem
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoHeritageSiteFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_TAG
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorMultiIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeritageSitesActivity: CommunityBaseActivity<ActivityHeritageSitesBinding>(
    R.layout.activity_heritage_sites
) {

    private lateinit var adapter: HeritageSitesMapAdapter

    override fun onClusterItemClick(item: MarkerItem): Boolean {
        clearSelectedMarker()

        val heritageSite: HeritageSiteKt? = adapter.currentList.stream()
            .filter { site ->
                site.latLng == item.position
            }
            .findFirst()
            .orElse(null)
        heritageSite?.let { site ->
            scrollToPosition(false, site.position)
        }

        /* Returns: true if the listener consumed the event (i.e. the default behavior should
           NOT occur), false otherwise (i.e. the default behavior SHOULD occur). The default
           behavior is for the camera to move to the marker and an info window to appear */
        return false // Must be false to show infoWindow, otherwise it's just recycler pos
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_7

        adapter = HeritageSitesMapAdapter(
            HeritageSiteMapListener { site, index ->
                when (index) {
                    0 -> scrollToPosition(site)
                    1 -> showInfoFragment(site)
                    else -> zoomInOutMarker(site.id) // 2
                }
            },
            HeritageSiteMapLongListener { index ->
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

        viewModel.heritageSitesKt.observe(this) { sites ->
            adapter.submitList(sites)

            lifecycleScope.launch {
                val title = when (viewModel.getHeritageTypesSelectedCount()) {
                    1 -> {
                        val site = viewModel.getHeritageTypeSelected()
                        String.format(
                            getString(R.string.heritage_sites_activity_title_single),
                            site.type
                        )
                    }
                    else -> {
                        val total = viewModel.getHeritageSitesCount()
                        getTitleSubtitle(sites.size, total)
                    }
                }
                setTitleSubtitle(title)
            }
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

        var heritageSite: HeritageSiteKt? = null
        val itemId = viewModel.currentHeritageSiteId
        if (itemId != 0L) {
            heritageSite = getHeritageSite(itemId)
            if (heritageSite != null) {
                scrollToPosition(true, heritageSite.position)
            }
        }

        val context = this@HeritageSitesActivity
        clusterRenderer = ColorMultiIconRenderer(
            context,
            R.drawable.ic_baseline_place_default_30,
            map, clusterManager
        )
        clusterManager.renderer = clusterRenderer
        val heritageSites = adapter.currentList
        if (heritageSites.isNotEmpty()) {
            for (i in heritageSites.indices) {
                val markerItem = getMarkerItem(
                    heritageSites[i].latLng, heritageSites[i].name,
                    heritageSites[i].address, heritageSites[i].color
                )
                allMarkerItems[heritageSites[i].id] = markerItem
                clusterManager.addItem(markerItem)
                if (i == heritageSites.size.minus(1)) {
                    when (itemId) {
                        0L -> zoomOutMarkers(true)
                        else -> {
                            if (heritageSite != null) {
                                zoomInOutMarker(heritageSite.id,true)
                            }
                        }
                    }
                    initClusterManager()
                }
            }
        }
    }

    private fun getHeritageSite(itemId: Long): HeritageSiteKt? {
        return adapter.currentList.stream()
            .filter { item ->
                item.id == itemId
            }
            .findFirst()
            .orElse(null)
    }

    private fun getTitleSubtitle(count: Int, total: Int): String {
        return getString(
            when (count) {
                total -> R.string.heritage_sites_activity_title_multi
                else -> R.string.heritage_sites_activity_title_filter
            }
        )
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
        position?.let { pos -> // Init | onClusterItemClick()
            when (init) {
                true -> recyclerView.scrollToPosition(pos)
                else -> scrollToPosition(
                    position, adapter.currentList.size
                )
            }
        }
    }

    private fun scrollToPosition( // Recycler next/prev arrow
        heritageSite: HeritageSiteKt
    ) {
        if (adapter.currentList.size > 1) {
            heritageSite.position?.let { pos ->
                heritageSite.size?.let { qty ->
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

    private fun showInfoFragment(heritageSite: HeritageSiteKt) {
        viewModel.setCurrentHeritageSite(heritageSite)
        InfoHeritageSiteFragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }
}
