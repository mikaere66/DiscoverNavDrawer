package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogDetailsPagerAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogDetailsActivity: DetailedActivity() {

    private lateinit var pagerAdapter: DogDetailsPagerAdapter
    private lateinit var dogParks: List<DogParkKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailsImage.setOnClickListener { goImage() }

        val context = this@DogDetailsActivity
        lifecycleScope.launch {
            /* As well as persist, will limit results to
               only "Dog Exercise Area|Dog Park" types,
               & sort by exercise area/dog park name */
            dogParks = viewModel.getDogParksList() // true

            getCurrentDogPark()?.let { currentDogPark ->
                setStreetViewFab(::goStreetView, currentDogPark.angle)
            }

            pagerAdapter = DogDetailsPagerAdapter(
                context,
                dogParks.size
            )

            viewPager.apply {
                adapter = pagerAdapter
                registerOnPageChangeCallback(
                        object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (context::dogParks.isInitialized) {
                            val dogPark = dogParks[position]
                            // Log.d("ROUTE_${ route.id }", route.route)
                            setCurrentDogParkUi(dogPark)
                            // Set route ID for use with Waypoints
                            viewModel.setCurrentDogParkId(dogPark.id)
                        }
                    }
                })

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
            mapsFab.setOnClickListener { goMap() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_dog_details_activity, menu)
        actionMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_map -> {
                goMap()
                true
            }
            R.id.menu_street_view -> {
                goStreetView()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun getCurrentDogPark(): DogParkKt? {
        return when (val dogParkId = viewModel.currentDogParkId) {
            0L -> dogParks[0] // Was set, to infer ALL dogParks
            else -> {
                dogParks.stream()
                    .filter { dogPark ->
                        dogPark.id == dogParkId
                    }
                    .findFirst()
                    .orElse(null)
            }
        }
    }

    private fun getImageItemId(dogParkId: Long): Long {
        return when (dogParkId) {
            129L -> 1L
            130L -> 2L
            131L -> 3L
            132L -> 4L
            133L -> 5L
            134L -> 6L
            135L -> 7L
            136L -> 8L
            163L -> 9L
            else -> 0L
        }
    }

    private fun goImage() {
        val dogPark = viewModel.currentDogPark
        val itemId = getImageItemId(dogPark.id)
        val imageId =
            getImageIdentifier(R.array.dog_park_images, itemId)
        goFullscreen(this, imageId, dogPark.dogPark)
    }

    private fun goMap() {
        startActivity(
            Intent(this, DogParksActivity::class.java)
        )
    }

    private fun goStreetView() {
        val dogPark = viewModel.currentDogPark
        goStreetView(dogPark.angle != -1)
    }

    private fun setCurrentDogParkUi(dogPark: DogParkKt) {
        /* Set currentDogPark for use in
           Images|StreetView activities */
        viewModel.setCurrentDogPark(dogPark)

        setStreetViewFab(::goStreetView, dogPark.angle,false)

        val itemId = getImageItemId(dogPark.id)
        val imageId =
            getImageIdentifier(R.array.dog_park_images, itemId)
        setToolbarImage(imageId)

        setToolbarTitle(dogPark.dogPark)
        setToolbarSubtitle(
            dogParks.size,
            when (dogPark.typeId) {
                8L -> R.string.dog_exercise_subtitle
                else -> R.string.dog_details_subtitle
            }
        )
    }

    private fun setCurrentViewPagerItem() {
        val index = dogParks.indexOfFirst { dogPark ->
            dogPark.id == viewModel.currentDogParkId
        }

        if (index != -1) {
            viewPager.setCurrentItem(index,false)
        }
    }
}
