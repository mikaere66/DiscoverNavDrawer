package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.RoutesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentRoutesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteFaveListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getRoutesSubtitle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MultiRouteFragment: BaseFragment<FragmentRoutesBinding>(R.layout.fragment_routes) {

    private lateinit var adapter: RoutesAdapter

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

        viewModel.routesFilteredBy.observe(viewLifecycleOwner) { routes ->
            adapter.submitList(routes)

            if (routes.isNotEmpty()) {
                setTitleSubtitle(routes)
                enableMapMenu()
            }
        }

        setMapMenu()
    }

    private fun enableMapMenu() {
        /* It's a race to see which comes first, onPrepareMenu()
           or this. The solution seems to be to have BOTH */
        actionMenu?.findItem(R.id.menu_multi_map)?.isEnabled =
            adapter.currentList.isNotEmpty()
    }

    private fun goDetails(route: RouteKt) {
        setMultiRouteIds()
        navigateTo(1321604051, route) // DetailsActivity
    }

    private fun goMultiDay() {
        setMultiRouteIds()
        navigateTo(
            1321613211,null // MultiDayActivity
        )
    }

    private fun goWaypoints(route: RouteKt) {
        navigateTo(1321602011, route) // Basic|Extended
    }

    private fun setMapMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_multi_routes, menu)
                actionMenu = menu
            }

            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.menu_multi_map).isEnabled =
                    adapter.currentList.isNotEmpty()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_multi_map -> {
                        goMultiDay()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun setMultiRouteIds() {
        val multiRouteIds = adapter.currentList.map { it.id }
        viewModel.setMultiRouteIds(multiRouteIds)
        viewModel.setRoutesMenuVisible(false)
    }

    private fun setTitleSubtitle(routes: List<RouteKt>) {
        val stringId = when (viewModel.multiRouteIndex) {
            MULTI_DAY_COASTAL_ID -> R.string.coastal_pathway_subtitle
            MULTI_DAY_CRATER_ID -> R.string.crater_rim_subtitle
            else -> R.string.head_to_head_subtitle // MULTI_DAY_HEADS_ID
        }
        val title = String.format(
            getString(R.string.app_title),
            getString(stringId)
        )
        viewModel.setTitle(title)

        val subtitle = getRoutesSubtitle(routes.size)
        viewModel.setSubtitle(subtitle)
    }
}
