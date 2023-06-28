package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DetailsPagerAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_DETAILED_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_01
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goWaypoints
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.base1way
import com.michaelrmossman.kotlin.discoverchristchurch.utils.baseRet
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsActivity: DetailedActivity() {

    private lateinit var pagerAdapter: DetailsPagerAdapter
    private lateinit var routes: List<RouteKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.detailsImage.setOnClickListener { goImage() }

        val context = this@DetailsActivity
        lifecycleScope.launch {
            routes = when (viewModel.multiRouteIds.isNotEmpty()) {
                /* Based on DogPark linkedIds, or
                   adapter.currentList from either
                   MultiRoute|Routes fragments */
                true -> {
                    viewModel.getMultiLinkedRoutesKt()
                }
                // Based on routeSearchTerm and sortByPref
                else -> viewModel.getRoutesKtList(true)
            }

            getCurrentRoute()?.let { currentRoute ->
                setStreetViewFab(::goStreetView, currentRoute.angle)
            }

            pagerAdapter = DetailsPagerAdapter(
                context,
                routes.size
            )

            viewPager.apply {
                adapter = pagerAdapter
                registerOnPageChangeCallback(
                        object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        if (context::routes.isInitialized) {
                            val route = routes[position]
                            setCurrentRouteUi(route)
                            // Set route ID for use with Waypoints
                            viewModel.setCurrentRouteId(route.id)
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
            mapsFab.setOnClickListener { goWaypoints() }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_details_activity, menu)
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
            R.id.menu_search -> {
                getRouteName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (::routes.isInitialized) {
            lifecycleScope.launch {
                val routeId = viewModel.currentRoute.id
                val routeKt = viewModel.getRouteKtById(routeId)
                menu.findItem(R.id.menu_fave)?.title = getString(
                    when (routeKt.fave) {
                        true -> R.string.menu_fave_remove
                        else -> R.string.menu_fave_add
                    }
                )
            }
            return true
        }
        return false
    }

    private fun getCurrentRouteId(): Long {
        /* Intent extra (or argument) is only sent when navigating
           from MainActivity to DetailsActivity using the NavMenu.
           See menu/activity_main_drawer & navigation/nav_graph */
        return when (intent?.extras?.containsKey("routeId")) {
            false -> viewModel.currentRouteId
            else -> when (val routeId =
                intent?.extras?.getLong("routeId")
            ) {
                null -> viewModel.currentRouteId
                else -> routeId
            }
        }
    }

    private fun getCurrentRoute(): RouteKt? {
        return when (val routeId = getCurrentRouteId()) {
            0L -> routes[0] // Not set: infers ALL routes
            else -> {
                routes.stream()
                    .filter { route ->
                        route.id == routeId
                    }
                    .findFirst()
                    .orElse(null)
            }
        }
    }

    private fun goImage() {
        val route = viewModel.currentRoute
        val imageId =
            getImageIdentifier(R.array.route_images, route.id)
        goFullscreen(this, imageId, route.route)
    }

    private fun goStreetView() {
        val route = viewModel.currentRoute
        when (route.angle) {
            // Shouldn't actually be able to get this far, so just for safety
            -1 -> fancyToast(this,4, R.string.no_street_view_a)
            else -> {
                val coords =
                    Coords(
                        String.format(getString(R.string.app_title),
                            route.route),
                        String.format(getString(R.string.street_view_waypoint_subtitle),
                            route.start),
                        route.latLng,
                        route.angle,
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

    private fun goWaypoints() { // Refer SysUtils
        goWaypoints(this, viewModel.currentRoute.round)
    }

    private fun setCurrentRouteUi(route: RouteKt) {
        /* Set currentRoute for use in DetailsActivity, which
           points to : ImagesActivity | StreetViewActivity
           or one of the activities in goWaypoints() */
        viewModel.setCurrentRoute(route)

        setStreetViewFab(::goStreetView, route.angle,false)

        val imageId =
            getImageIdentifier(R.array.route_images, route.id)
        setToolbarImage(imageId)

        setToolbarTitle(route.route)
        setToolbarSubtitle(
            routes.size,
            when (route.shared) {
                1 -> R.string.details_shared_subtitle
                else -> {
                    when (route.round) {
                        in base1way..baseRet -> {
                            R.string.details_basic_subtitle
                        }
                        else -> R.string.details_extended_subtitle
                    }
                }
            }
        )
    }

    private fun setCurrentViewPagerItem() {
        val index = routes.indexOfFirst { route ->
            route.id == getCurrentRouteId()
        }

        if (index != -1) {
            viewPager.setCurrentItem(index,false)
        }
    }

    override fun setPosition(routeId: Long) {
        val route: RouteKt? = routes.stream()
            .filter { filtered ->
                filtered.id == routeId
            }
            .findFirst()
            .orElse(null)

        route?.let { filtered ->
            val index = routes.indexOfFirst {
                it.id == filtered.id
            }

            if (index != -1) {
                viewPager.setCurrentItem(index,true)
            }
        }
    }

    private fun toggleFave() {
        lifecycleScope.launch {
            val routeId = viewModel.currentRoute.id
            val route = viewModel.getRouteKtById(routeId)
            with (route) {
                toggleFavourite(
                    this.fave,
                    this.id,
                    ITEM_VIEW_TYPE_ITEM_01,
                    true
                )
            }
        }
    }
}
