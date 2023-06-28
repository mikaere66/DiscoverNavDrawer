package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteFaveListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.RoutesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentRoutesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getRoutesSubtitle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

@AndroidEntryPoint
class RoutesFragment: BaseFragment<FragmentRoutesBinding>(R.layout.fragment_routes) {

    private lateinit var animatedBottomBar: AnimatedBottomBar
    private lateinit var adapter: RoutesAdapter

//    override fun onResume() {
//        super.onResume()

//        viewModel.setMultiRouteIds(listOf())
//        viewModel.setMultiRouteIndex(Int.MAX_VALUE)
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RoutesAdapter(
            RouteFaveListener { checkBox, route ->
                toggleFaveRoute(checkBox.isChecked, route.id)
            },
            RouteListener { route ->
                goDetails(route)
            },
            RouteLongListener { route ->
                goWaypoints(route)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter

        setSortMenu(viewLifecycleOwner)
        setBottomBar()

        viewModel.routes.observe(viewLifecycleOwner) { routes ->
            adapter.submitList(routes)

            val routesTab = animatedBottomBar.tabs[3]
            when (routes.size) {
                0 -> animatedBottomBar.clearBadgeAtTab(routesTab)
                else -> {
                    setTitleSubtitle(routes)

                    animatedBottomBar.setBadgeAtTab(routesTab,
                        AnimatedBottomBar.Badge(routes.size.toString())
                    )
                }
            }
        }
    }

    private fun goDetails(route: RouteKt) {
        val multiRouteIds = adapter.currentList.map { it.id }
        viewModel.setMultiRouteIds(multiRouteIds)
        viewModel.setMultiRouteIndex(0)
        navigateTo(1815604051, route) // DetailsActivity
    }

    private fun goWaypoints(route: RouteKt) {
        navigateTo(1815602011, route) // Basic|Extended
    }

    private fun setTitleSubtitle(routes: List<RouteKt>) {
        lifecycleScope.launch {
            val currentPlaceId = viewModel.currentPlaceId
            val currentPlace =
                viewModel.getPlaceById(currentPlaceId)
            val title =
                String.format(getString(R.string.app_title),
                    currentPlace.place)
            viewModel.setTitle(title)

            val subtitle = getRoutesSubtitle(routes.size)
            viewModel.setSubtitle(subtitle)
        }
    }

    private fun setBottomBar() {
        animatedBottomBar = binding.routesButtonsLayout.bottomBar
        setBottomBar(animatedBottomBar,3)
        setBottomTabs()
    }

    private fun setBottomTabs() {
        animatedBottomBar.apply {
            setTabEnabledAt(2,true)
        }
    }
}
