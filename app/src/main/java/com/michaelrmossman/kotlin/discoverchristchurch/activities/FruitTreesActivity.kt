package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTreeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.CAMERA_UPDATE_PADDING
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.utils.TextOnlyInfoWindowAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorMultiIconRenderer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FruitTreesActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_6

        if (viewModel.currentFruitTypeId == 0L) {
            setTitleSubtitle(getTitleString())
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)
        lifecycleScope.launch {
            when (val fruitTypeId = viewModel.currentFruitTypeId) {
                0L -> {
                    val fruitCats = viewModel.getFruitCatsList()
                    for (i in fruitCats.indices) {
                        val fruitTypes =
                            viewModel.getFruitTypesByCatId(fruitCats[i].id)
                        for (j in fruitTypes.indices) {
                            val fruitTrees =
                                viewModel.getFruitTreesByTypeId(fruitTypes[j].id)
                            addClusteredMarkers(
                                fruitTrees,i == 0,false
                            )
                        }
                        if (i == fruitCats.size.minus(1)) {
                            initInfoWindowAdapter()
                            zoomOutMarkers(true)
                        }
                    }
                }
                else -> {
                    val fruitTrees =
                        viewModel.getFruitTreesByTypeId(fruitTypeId)
                    addClusteredMarkers(fruitTrees, init = true, single = true)
                    val fruitType =
                        viewModel.getFruitTypeById(fruitTypeId)
                    with(fruitType) {
                        setFavouriteButton(binding.toggleFaveButton,
                        id, ITEM_VIEW_TYPE_ITEM_6, fave,true)
                    }
                }
            }
        }
    }

    private fun addClusteredMarkers(
        fruitTrees: List<FruitTreeKt>, init: Boolean, single: Boolean
    ) {
        if (init) {
            val context = this@FruitTreesActivity
            clusterManager.apply {
                setRenderer(
                    ColorMultiIconRenderer(
                        context,
                        R.drawable.ic_baseline_park_default_30,
                        map,this
                    )
                )
            }
        }
        for (i in fruitTrees.indices) {
            val latLng = fruitTrees[i].latLng
            val markerItem = getMarkerItem(
                latLng, fruitTrees[i].type,
                fruitTrees[i].category, fruitTrees[i].color
            )
            allMarkerItems[fruitTrees[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (single && i == fruitTrees.size.minus(1)) {
                initInfoWindowAdapter()
                setTitleSubtitle(getTitleString())
                when (allMarkerItems.size) {
                    1 -> zoomInMarker(latLng)
                    else -> {
                        setTitleSubtitle(getTitleString())
                        zoomInOutMarkers(true, CAMERA_UPDATE_PADDING)
                    }
                }
            }
        }
    }

    // Multi/single refers to categories, not quantity
    private fun getTitleString(): String {
        return when (viewModel.currentFruitTypeId) {
            0L -> getString(R.string.fruit_activity_title_multi)
            else -> resources.getQuantityString(
                R.plurals.fruit_activity_title_single,
                allMarkerItems.size
            )
        }
    }

    private fun initInfoWindowAdapter() {
        initClusterManager(true)

        clusterManager.markerCollection.apply {
            setInfoWindowAdapter(
                TextOnlyInfoWindowAdapter(
                    this@FruitTreesActivity,
                    R.string.fruit_tree_info_window
                )
            )
        }
    }

    override fun setTitleSubtitle(string: String) {
        lifecycleScope.launch {
            val fruitCount: Int
            /* Must be done like setTitleSubtitle(getTitleString()) to
               override default signature in CommunityBaseActivity */
            val titleSuffix =
                when (val fruitTypeId = viewModel.currentFruitTypeId) {
                    0L -> {
                        fruitCount = 0
                        getString(R.string.fruit_activity_title_multi)
                    }
                    else -> {
                        val fruitType = viewModel.getFruitTypeById(fruitTypeId)
                        val type = fruitType.type.replace("Tree", String())
                        fruitCount = fruitType.count
                        String.format(
                            string,
                            type
                        )
                    }
                }
            val title = String.format(
                getString(R.string.app_title),
                titleSuffix
            )
            supportActionBar?.title = title

            val subtitle = resources.getQuantityString(
                R.plurals.fruit_trees_activity_subtitle,
                fruitCount
            )
            supportActionBar?.subtitle = subtitle
        }
    }
}
