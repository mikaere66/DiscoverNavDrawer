package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.StreetDetailsPagerAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StreetDetailsActivity: DetailedActivity() {

    private lateinit var pagerAdapter: StreetDetailsPagerAdapter
    private lateinit var streetArtItems: List<StreetArtKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailsImage.setOnClickListener { goImage() }

        val context = this@StreetDetailsActivity
        lifecycleScope.launch {
            streetArtItems = viewModel.getStreetArtItems()

            pagerAdapter = StreetDetailsPagerAdapter(
                context,
                streetArtItems.size
            )

            viewPager.apply {
                adapter = pagerAdapter
                registerOnPageChangeCallback(
                    object: ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            if (context::streetArtItems.isInitialized) {
                                val streetArt = streetArtItems[position]
                                setCurrentStreetArtUi(streetArt)
                                viewModel.setCurrentStreetArtId(streetArt.id)
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
        inflater.inflate(R.menu.menu_street_art_activity, menu)
        actionMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_map -> {
                goMap()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun goImage() {
        val item = viewModel.streetArtItem
        val imageId =
            getImageIdentifier(R.array.street_art_images, item.id)
        goFullscreen(this, imageId, item.title, item.landscape)
    }

    private fun goMap() {
        startActivity(
            Intent(this, StreetArtActivity::class.java)
        )
    }

    private fun setCurrentStreetArtUi(streetArt: StreetArtKt) {
        // Set streetArt for use with ImagesActivity
        viewModel.setStreetArtItem(streetArt)

        val imageId =
            getImageIdentifier(R.array.street_art_images, streetArt.id)
        setToolbarImage(imageId)

        setToolbarTitle(streetArt.title)
        setToolbarSubtitle(
            streetArtItems.size,
            R.string.details_street_art_subtitle
        )
    }

    private fun setCurrentViewPagerItem() {
        val index = streetArtItems.indexOfFirst { item ->
            item.id == viewModel.currentStreetArtId
        }
        if (index != -1) {
            viewPager.setCurrentItem(index,false)
        }
    }
}
