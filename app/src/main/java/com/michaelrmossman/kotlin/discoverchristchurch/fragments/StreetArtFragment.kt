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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.StreetArtAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentStreetArtBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StreetArtFragment: BaseFragment<FragmentStreetArtBinding>(R.layout.fragment_street_art) {

    private lateinit var adapter: StreetArtAdapter
    private lateinit var searchView: SearchView

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setStreetArtSearchTerm(null)
    }

    override fun onResume() {
        super.onResume() // Tested: Required
        val title = String.format(
            getString(R.string.app_title),
            getString(R.string.street_art_fragment_title)
        )
        viewModel.setTitle(title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = StreetArtAdapter(
            StreetArtListener { checkBox, index, streetArt ->
                when (index) {
                    0 ->  goStreetDetails(streetArt)
                    1 ->  showStreetArtImage(streetArt)
                    else -> toggleFave(checkBox.isChecked, streetArt.id, ITEM_VIEW_TYPE_ITEM_11)
                }
            },
            StreetArtLongListener { streetArt ->
                goStreetArt(streetArt)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        val spanSize = resources.getInteger(R.integer.street_art_grid_layout_span_count)
        val layoutManager = GridLayoutManager(requireContext(), spanSize)

        binding.apply {
            streetArtFragment = this@StreetArtFragment
            recyclerView.adapter = adapter
            recyclerView.layoutManager = layoutManager
        }

        setSearchMenu()

        viewModel.streetArtSearchedBy.observe(viewLifecycleOwner) { streetArt ->
            adapter.addFooterAndSubmitList(streetArt)

            layoutManager.spanSizeLookup = object: SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = when (position) {
                    0 -> spanSize
                    streetArt.size.plus(1) -> spanSize // Allow for header/footer
                    else -> 1
                }
            }

            setSubtitleAndFabs(streetArt.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goStreetDetails(streetArt: StreetArtKt) {
        goStreetArtSingle(1901619041, streetArt) // StreetDetailsActivity
    }

    private fun goRandom() {
        viewModel.setCurrentStreetArtId(-1L)
        navigateTo(1901619041) // StreetDetailsActivity
    }

    private fun goStreetArt(streetArt: StreetArtKt) {
        goStreetArtSingle(1901619011, streetArt) // StreetArtActivity
    }

    fun goStreetArtMulti() {
        viewModel.setCurrentStreetArtId(0L)
        navigateTo(1901619011) // StreetArtActivity
    }

    private fun setSearchMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_street_art_fragment, menu)
                actionMenu = menu

                val searchItem = menu.findItem(R.id.menu_search)
                searchItem.isVisible = true
                searchItem.title = getString(R.string.menu_search)

                searchView = searchItem.actionView as SearchView
                searchView.apply {
                    this.setOnSearchClickListener {
                        showHideRandomMenu(false)
                    }
                    this.setOnCloseListener {
                        this.apply {
                            clearFocus()
                            onActionViewCollapsed()
                        }
                        with (binding) {
                            setBackToTopFab(mapsMenuFab, backToTopFab, recyclerView)
                        }
                        showHideRandomMenu(true)
                        true
                    }

                    setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            setArtistSearchTerm(query)
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
                return when (menuItem.itemId) {
                    R.id.menu_random -> {
                        goRandom()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)
    }

    private fun setArtistSearchTerm(query: String?) {
        val searchTerm = when (query?.length) {
            in 0..1 -> null
            else -> query?.trim()
        }
        if (viewModel.streetArtSearchTerm.value != searchTerm) {
            viewModel.setStreetArtSearchTerm(searchTerm)
        }
    }

    private fun setSubtitleAndFabs(count: Int) {
        lifecycleScope.launch {
            val total = viewModel.getStreetArtCount()
            val subtitle = String.format(
                getString(R.string.street_art_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsExtra(
                    mapsMenuFab,
                    backToTopFab,
                    recyclerView,
                    count.minus(total),
                    count == 0
                )
            }
        }
    }

    private fun showHideRandomMenu(show: Boolean) {
        actionMenu?.findItem(R.id.menu_random)?.isVisible = show
    }
}
