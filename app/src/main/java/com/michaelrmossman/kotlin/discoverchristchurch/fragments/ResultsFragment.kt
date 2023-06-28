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
class ResultsFragment: BaseFragment<FragmentCommonBinding>(R.layout.fragment_common) {

    private lateinit var adapter: RoutesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay

        viewModel.setTitle(getString(R.string.search_by))
    }

    override fun onResume() {
        super.onResume()

        viewModel.setMultiRouteIds(listOf())
        viewModel.setMultiRouteIndex(Int.MAX_VALUE)
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

        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            val results = viewModel.getRoutesByFeature()
            adapter.submitList(results)

            val subtitle =
                String.format(getString(R.string.results_subtitle),
                    viewModel.currentSearch, results.size)
            viewModel.setSubtitle(subtitle)
        }
    }

    private fun goDetails(route: RouteKt) {
        val multiRouteIds = adapter.currentList.map { it.id }
        viewModel.setMultiRouteIds(multiRouteIds)
        viewModel.setMultiRouteIndex(0)
        navigateTo(1805604051, route) // DetailsActivity
    }

    private fun goWaypoints(route: RouteKt) {
        navigateTo(1805602011, route) // Basic|Extended
    }
}
