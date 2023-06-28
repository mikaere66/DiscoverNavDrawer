package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ChCh360PagerAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goWaypoints
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFavouritesMessagePair
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChCh360Activity: DetailedActivity() {

    private lateinit var pagerAdapter: ChCh360PagerAdapter
    private lateinit var chCh360Items: List<ChCh360Kt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailsImage.setOnClickListener { goImage() }

        val context = this@ChCh360Activity
        lifecycleScope.launch {
            chCh360Items = viewModel.getChCh360Items(true)

            getCurrentChCh360Item()?.let { currentChCh360Item ->
                setStreetViewFab(::goStreetView, currentChCh360Item.angle)
            }

            pagerAdapter = ChCh360PagerAdapter(
                context,
                chCh360Items.size
            )

            viewPager.apply {
                adapter = pagerAdapter
                registerOnPageChangeCallback(
                    object: ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            if (context::chCh360Items.isInitialized) {
                                val chCh360Item = chCh360Items[position]
                                setCurrentChCh360ItemUi(chCh360Item)
                                // Set chCh360 ID for use with Waypoints
                                viewModel.setChCh360ItemId(chCh360Item.id)
                            }
                        }
                    }
                )

                when (savedInstanceState) {
                    null -> setCurrentViewPagerItem()
                    else -> {
                        val position =
                            savedInstanceState.getInt(FRAGMENT_DETAILED_EXTRA)
                        setCurrentItem(position,false)
                    }
                }
            }

            val mapsFab: FloatingActionButton = binding.mapsFab
            mapsFab.setOnClickListener {  goWaypoints() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_ch_ch_360_activity, menu)
        actionMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_map -> {
                goWaypoints()
                true
            }
            R.id.menu_street_view -> {
                goStreetView()
                true
            }
            R.id.menu_fave -> {
                toggleFave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (::chCh360Items.isInitialized) {
            lifecycleScope.launch {
                val itemId = viewModel.chCh360Item.id
                val item = viewModel.getChCh360ItemById(itemId)
                menu.findItem(R.id.menu_fave)?.title = getString(
                    when (item.fave) {
                        true -> R.string.menu_fave_remove
                        else -> R.string.menu_fave_add
                    }
                )
            }
            return true
        }
        return false
    }

    private fun getCurrentChCh360Item(): ChCh360Kt? {
        return when (val itemId = viewModel.chCh360ItemId) {
            0L -> chCh360Items[0] // Was set to infer ALL items
            else -> {
                chCh360Items.stream()
                    .filter { item ->
                        item.id == itemId
                    }
                    .findFirst()
                    .orElse(null)
            }
        }
    }

    private fun goImage() {
        val item = viewModel.chCh360Item
        val imageId =
            getImageIdentifier(R.array.ch_ch_360_images, item.id)
        goFullscreen(this, imageId, item.leg)
    }

    private fun goStreetView() {
        getCurrentChCh360Item()?.let { item ->
            val coordinates = Coords(
                String.format(getString(R.string.app_title),
                    item.leg),
                String.format(getString(R.string.street_view_start_point_subtitle),
                    item.start),
                item.latLng,
                item.angle,
                getWaypointSubtitleText(1,2)
            )
            viewModel.setCurrentCoords(coordinates)
            startActivity(
                Intent(this, StreetViewActivity::class.java)
            )
        }
    }

    private fun goWaypoints() { // Refer SysUtils
        viewModel.setRoutesMenuVisible(false)
        // Reset currentRouteId is 0L to hide Route specific options
        viewModel.setCurrentRouteId(0L)
        goWaypoints(this, Int.MIN_VALUE) // ChCh361Activity
    }

    private fun setCurrentChCh360ItemUi(chCh360Item: ChCh360Kt) {
        // Set chCh360Item for use with ImagesActivity|InfoMegaFragment
        viewModel.setChCh360Item(chCh360Item)

        setStreetViewFab(::goStreetView, chCh360Item.angle,false)

        val imageId =
            getImageIdentifier(R.array.ch_ch_360_images, chCh360Item.id)
        setToolbarImage(imageId)

        setToolbarTitle(chCh360Item.leg)
        setToolbarSubtitle(
            chCh360Items.size,
            R.string.details_ch_ch_360_subtitle
        )
    }

    private fun setCurrentViewPagerItem() {
        val index = chCh360Items.indexOfFirst { item ->
            item.id == viewModel.chCh360ItemId
        }

        if (index != -1) {
            viewPager.setCurrentItem(index,false)
        }
    }

    private fun toggleFave() {
        lifecycleScope.launch {
            val itemId = viewModel.chCh360Item.id
            val item = viewModel.getChCh360ItemById(itemId)
            val fave = item.fave
            val result =
                viewModel.toggleFavourite(!fave, itemId, ITEM_VIEW_TYPE_ITEM_00,true)
            val messagePair = getFavouritesMessagePair(result)
            fancyToast(this@ChCh360Activity, messagePair.first, messagePair.second)
        }
    }
}
