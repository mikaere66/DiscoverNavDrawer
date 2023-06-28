package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.Menu
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.viewpager2.widget.ViewPager2
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityDetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA

// Extended by BikeDetails|ChCh360|Details|DogDetails|StreetDetails activities, for ViewPager2
abstract class DetailedActivity: CommonDialogActivity<ActivityDetailsBinding>(
    R.layout.activity_details
) {

    lateinit var viewPager: ViewPager2
    var actionMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        val appBarLayout: AppBarLayout = binding.appBar
        appBarLayout.addOnOffsetChangedListener { layout, offset ->
            val scrollRange = layout.totalScrollRange
            val isShown = scrollRange + offset == 0
            showHideMenuItems(isShown)
        }

        viewPager = binding.viewPager
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val position = viewPager.currentItem
        outState.putInt(FRAGMENT_DETAILED_EXTRA, position)

        super.onSaveInstanceState(outState)
    }

    fun setStreetViewFab(
        action:() -> Unit, angle: Int, init: Boolean = true
    ) {
        /* Visibility set to GONE in xml, so unless
           this method is called, it won't appear */
        val streetViewFab: FloatingActionButton =
            binding.streetViewFab
        streetViewFab.apply {
            isEnabled = angle != -1
            if (init) {
                isVisible = true // Not visible in BikeDetails
                setOnClickListener { action() }
            }
        }
    }

    fun setToolbarImage(@DrawableRes imageId: Int) {
        // Defaults to R.drawable.tracks_and_safety, if image not found
        GlideApp.with(this).load(imageId).into(binding.detailsImage)
    }

    fun setToolbarSubtitle(size: Int, @StringRes subtitleId: Int) {
        val position = viewPager.currentItem.plus(1)
        val subtitle =
            String.format(getString(subtitleId), position, size)
        binding.toolbarLayout.subtitle = subtitle
    }

    fun setToolbarTitle(name: String) {
        binding.toolbarLayout.title =
            String.format(getString(R.string.app_title),
                name)
    }

    private fun showHideMenuItems(show: Boolean) {
        actionMenu?.apply {
            findItem(R.id.menu_map)?.isVisible = show
            findItem(R.id.menu_street_view)?.isVisible = show
            findItem(R.id.menu_fave)?.isVisible = show
            // Only relevant to DetailsActivity
            if (viewModel.routesMenuVisible) {
                findItem(R.id.menu_search)?.isVisible = show
            }
        }
    }
}
