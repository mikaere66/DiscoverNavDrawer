package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BikeDetailsPagerAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFavouritesMessagePair
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BikeDetailsActivity: DetailedActivity() {

    private lateinit var pagerAdapter: BikeDetailsPagerAdapter
    private lateinit var bikeTrackItems: List<BikeTrackKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailsImage.setOnClickListener { goImage() }

        val context = this@BikeDetailsActivity
        lifecycleScope.launch {
            bikeTrackItems = viewModel.getBikeTrackItems(true)

            pagerAdapter = BikeDetailsPagerAdapter(
                context,
                bikeTrackItems.size
            )

            viewPager.apply {
                adapter = pagerAdapter
                registerOnPageChangeCallback(
                    object: ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            if (context::bikeTrackItems.isInitialized) {
                                val bikeTrack = bikeTrackItems[position]
                                setCurrentBikeTrackUi(bikeTrack)
                                viewModel.setCurrentBikeTrackId(bikeTrack.id)
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
            mapsFab.setOnClickListener {  goMap() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_bike_details_activity, menu)
        actionMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_map -> {
                goMap()
                true
            }
            R.id.menu_fave -> {
                toggleFave()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (::bikeTrackItems.isInitialized) {
            lifecycleScope.launch {
                val itemId = viewModel.bikeTrackItem.id
                val item = viewModel.getBikeTrackById(itemId)
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

    private fun goImage() {
        val item = viewModel.bikeTrackItem
        val imageId =
            getImageIdentifier(R.array.bike_track_images, item.id)
        goFullscreen(this, imageId, item.track, item.landscape)
    }

    private fun goMap() {
        startActivity(
            Intent(this, BikeTracksActivity::class.java)
        )
    }

    private fun setCurrentBikeTrackUi(bikeTrack: BikeTrackKt) {
        // Set bikeTrack for use with ImagesActivity
        viewModel.setBikeTrackItem(bikeTrack)

        val imageId =
            getImageIdentifier(R.array.bike_track_images, bikeTrack.id)
        setToolbarImage(imageId)

        setToolbarTitle(bikeTrack.track)
        setToolbarSubtitle(
            bikeTrackItems.size,
            R.string.details_bike_track_subtitle
        )
    }

    private fun setCurrentViewPagerItem() {
        val index = bikeTrackItems.indexOfFirst { item ->
            item.id == viewModel.currentBikeTrackId
        }

        if (index != -1) {
            viewPager.setCurrentItem(index,false)
        }
    }

    private fun toggleFave() {
        lifecycleScope.launch {
            val itemId = viewModel.bikeTrackItem.id
            val item = viewModel.getBikeTrackById(itemId)
            val fave = item.fave
            val result =
                viewModel.toggleFavourite(!fave, itemId, ITEM_VIEW_TYPE_ITEM_8,true)
            val messagePair = getFavouritesMessagePair(result)
            fancyToast(this@BikeDetailsActivity, messagePair.first, messagePair.second)
        }
    }
}
