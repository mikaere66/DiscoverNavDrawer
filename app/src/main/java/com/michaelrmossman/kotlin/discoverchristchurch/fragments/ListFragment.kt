package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteFaveListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.RoutesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentListBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getRoutesSubtitle
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment: BaseFragment<FragmentListBinding>(R.layout.fragment_list) {

    private lateinit var adapter: RoutesAdapter
    private lateinit var searchView: SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.list_fragment_title))
        viewModel.setTitle(title)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setRouteSearchTerm(null)
    }

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

        binding.apply {
            listFragment = this@ListFragment

            recyclerView.adapter = adapter
            recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recycler: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recycler, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        with (binding) {
                            setBackToTopFab(null, backToTopFab, recyclerView)
                        }
                    }
                }
            })
        }

        setSearchMenu()

        viewModel.isNavDrawerOpen.observe(viewLifecycleOwner) { isOpen ->
            if (::searchView.isInitialized) {
                if (isOpen) {
                    searchView.clearFocus()
                    viewModel.setIsNavDrawerOpen(false)
                }
            }
        }

        viewModel.routesSearchedBy.observe(viewLifecycleOwner) { routes ->
            adapter.submitList(routes)

            if (routes.isNotEmpty()) {
                setSubtitle(routes)
            }
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goDetails(route: RouteKt) {
        navigateTo(1209604051, route) // DetailsActivity
    }

    private fun goWaypoints(route: RouteKt) {
        navigateTo(1209602011, route) // Basic|Extended
    }

    private fun setSearchMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_list_fragment, menu)
                actionMenu = menu

                val searchItem = menu.findItem(R.id.menu_search)
                searchItem.isVisible = true
                searchItem.title = getString(R.string.menu_search)

                searchView = searchItem.actionView as SearchView
                searchView.apply {
                    this.setOnCloseListener {
                        this.apply {
                            clearFocus()
                            onActionViewCollapsed()
                        }
                        with (binding) {
                            setBackToTopFab(null, backToTopFab, recyclerView)
                        }
                        true
                    }

                    setOnQueryTextListener(object:
                            SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            setRouteSearchTerm(query)
                            /* Returns: true if the query has been handled by the listener,
                               false to let the SearchView perform the default action */
                            return false
                        }
                    })

                    val searchManager =
                        activity?.getSystemService(SEARCH_SERVICE) as SearchManager
                    setSearchableInfo(
                        searchManager.getSearchableInfo(activity?.componentName)
                    )
                }

                val searchPlate =
                    searchView.findViewById(androidx.appcompat.R.id.search_src_text)
                        as EditText
                searchPlate.hint = getString(R.string.menu_search_hint)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false // Same as above
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun setRouteSearchTerm(query: String?) {
        val searchTerm =
            when (query?.length) {
                in 0..1 -> null
                else -> query?.trim()
            }

        if (viewModel.routeSearchTerm.value != searchTerm) {
            viewModel.setRouteSearchTerm(searchTerm)
        }
    }

    private fun setSubtitle(routes: List<RouteKt>) {
        val subtitle = getRoutesSubtitle(routes.size)
        viewModel.setSubtitle(subtitle)
    }
}
