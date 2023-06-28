package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.PlaceListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.PlacesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentPlacesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PlaceKt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class PlacesFragment: BaseFragment<FragmentPlacesBinding>(R.layout.fragment_places) {

    private lateinit var animatedBottomBar: AnimatedBottomBar

    override fun onResume() {
        super.onResume()

        viewModel.setCurrentPlaceId(0L)

        if (::animatedBottomBar.isInitialized) {
            setBottomTabs()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PlacesAdapter(
            PlaceListener { place ->
                goRoutes(place)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter

        val index = getOverflowIconIndex()
        viewModel.setOverflowIconIndex(index.plus(100))

        setSortMenu(viewLifecycleOwner)
        setBottomBar()

        viewModel.places.observe(viewLifecycleOwner) { places ->
            binding.emptyViewGroup.visibility = View.GONE
            adapter.submitList(places)

            val placesTab = animatedBottomBar.tabs[2]
            when (places.size) {
                0 -> animatedBottomBar.clearBadgeAtTab(placesTab)
                else -> {
                    lifecycleScope.launch {
                        val currentAreaId = viewModel.currentAreaId
                        val currentArea = viewModel.getAreaById(currentAreaId)
                        val title =
                            String.format(getString(R.string.app_title),
                                currentArea.area)
                        viewModel.setTitle(title)

                        val routes = viewModel.getRoutesCountByAreaId(currentAreaId)
                        val subtitle =
                            String.format(getString(R.string.places_subtitle),
                                places.size, routes)
                        viewModel.setSubtitle(subtitle)

                        animatedBottomBar.setBadgeAtTab(placesTab,
                            AnimatedBottomBar.Badge(places.size.toString())
                        )
                    }
                }
            }
        }
    }

    private fun goRoutes(place: PlaceKt) {
        viewModel.setCurrentPlaceId(place.id)
        navigateTo(1612618156) // RoutesFragment
    }

    private fun setBottomBar() {
        animatedBottomBar = binding.placesButtonsLayout.bottomBar
        setBottomBar(animatedBottomBar,2)
        setBottomTabs()
    }

    private fun setBottomTabs() {
        animatedBottomBar.apply {
            setTabEnabledAt(1,true)
            setTabEnabledAt(2,false)
        }
    }
}
