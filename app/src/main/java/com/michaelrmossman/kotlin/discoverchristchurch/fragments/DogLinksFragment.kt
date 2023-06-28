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
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogLinksFragment: BaseFragment<FragmentCommonBinding>(R.layout.fragment_common) {

    private lateinit var routes: List<RouteKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.dog_links_title))
        viewModel.setTitle(title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RoutesAdapter(
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

        lifecycleScope.launch {
            routes = viewModel.getMultiLinkedRoutesKt()
            adapter.submitList(routes)

            val quantityString =
                resources.getQuantityString(
                    R.plurals.routes_linked_subtitle,
                    routes.size
                )
            val subtitle =
                String.format(quantityString, routes.size)
            viewModel.setSubtitle(subtitle)
        }
    }

    private fun goDetails(route: RouteKt) {
        val linkedIds = routes.map { it.id }
        viewModel.setMultiRouteIds(linkedIds)
        viewModel.setMultiRouteIndex(Int.MIN_VALUE)
        navigateTo(412604051, route) // DetailsActivity
    }

    private fun goWaypoints(route: RouteKt) {
        navigateTo(412602011, route) // Basic|Extended
    }
}
