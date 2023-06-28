package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.AreaListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.AreasAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentAreasBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.AreaKt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class AreasFragment: BaseFragment<FragmentAreasBinding>(R.layout.fragment_areas) {

    private lateinit var animatedBottomBar: AnimatedBottomBar

    override fun onResume() {
        super.onResume()

        viewModel.setCurrentAreaId(0L)

        if (::animatedBottomBar.isInitialized) {
            setBottomTabs()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AreasAdapter(
            AreaListener { area ->
                goPlaces(area)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter

        viewModel.setTitle(getString(R.string.areas_title))

        val index = getOverflowIconIndex()
        viewModel.setOverflowIconIndex(index.plus(100))

        setSortMenu(viewLifecycleOwner)
        setBottomBar()

        viewModel.areas.observe(viewLifecycleOwner) { areas ->
            binding.emptyViewGroup.visibility = View.GONE
            adapter.submitList(areas)

            val areasTab = animatedBottomBar.tabs[1]
            when (areas.size) {
                0 -> animatedBottomBar.clearBadgeAtTab(areasTab)
                else -> {
                    lifecycleScope.launch {
                        val places = viewModel.getPlacesCount()
                        val routes = viewModel.getRoutesCount()
                        val subtitle =
                            String.format(getString(R.string.areas_subtitle),
                                areas.size, places, routes)
                        viewModel.setSubtitle(subtitle)

                        animatedBottomBar.setBadgeAtTab(areasTab,
                            AnimatedBottomBar.Badge(areas.size.toString())
                        )
                    }
                }
            }
        }
    }

    private fun goPlaces(area: AreaKt) {
        viewModel.setCurrentAreaId(area.id)
        navigateTo(118818156) // , findNavController()) // PlacesFragment
    }

    private fun setBottomBar() {
        animatedBottomBar = binding.areasButtonsLayout.bottomBar
        setBottomBar(animatedBottomBar,1)
        setBottomTabs()
    }

    private fun setBottomTabs() {
        animatedBottomBar.apply {
            setTabEnabledAt(1,false)
            setTabEnabledAt(2,false)
        }
    }
}
