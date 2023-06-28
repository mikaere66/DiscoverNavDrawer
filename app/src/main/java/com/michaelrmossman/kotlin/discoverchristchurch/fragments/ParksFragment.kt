package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ParkLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ParksAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ParkTypesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentParksBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getParkTypeWithQuantity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ParksFragment: BaseFragment<FragmentParksBinding>(
    R.layout.fragment_parks
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var parkTypesDialogBinding: ParkTypesDialogBinding
    private lateinit var parkTypeCheckBoxes: List<CheckBox>
    private lateinit var parkTypes: List<ParkTypeKt>
    private lateinit var searchView: SearchView
    private lateinit var adapter: ParksAdapter

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                parkTypeCheckBoxes, isChecked,
                parkTypesDialogBinding.parkTypesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.public_parks))
        viewModel.setTitle(title)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setParkSearchTerm(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ParksAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_10)
                }
            },
            ParkLongListener { park ->
                goPark(park)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            parksFragment = this@ParksFragment
            recyclerView.adapter = adapter
        }

        setSearchMenu()

        viewModel.parksSearchedBy.observe(viewLifecycleOwner) { parks ->
            adapter.addFooterAndSubmitList(parks)

            setSubtitleAndFabs(parks.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getParkTypes() {
        val listBooleans = mutableListOf<Boolean>()
        for (i in parkTypes.indices) {
            listBooleans.add(parkTypes[i].selected)
        }
        val listIds = parkTypes.map { it.id }
        val listQuantities = parkTypes.map { it.count }

        parkTypesDialogBinding =
            ParkTypesDialogBinding.inflate(layoutInflater)
        with (parkTypesDialogBinding) {
            parkTypeCheckBoxes = listOf(
                this.parkTypeSelect1, this.parkTypeSelect2,
                this.parkTypeSelect3, this.parkTypeSelect4,
                this.parkTypeSelect5, this.parkTypeSelect6,
                this.parkTypeSelect7, this.parkTypeSelect8
            )
        }
        parkTypesDialogBinding.apply {
            parksFragment = this@ParksFragment
            for (i in parkTypeCheckBoxes.indices) {
                parkTypeCheckBoxes[i].apply {
                    isChecked = listBooleans[i]
                    setOnCheckedChangeListener(
                        this@ParksFragment
                    )
                    text = getParkTypeWithQuantity(
                        listIds[i].toInt(), listQuantities[i]
                    )
                }
            }
        }

        val checkedCount = getCheckedCount(parkTypeCheckBoxes)
        parkTypesDialogBinding.parkTypesSelectAll.apply {
            isChecked = checkedCount == parkTypeCheckBoxes.size
            isEnabled = checkedCount < parkTypeCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_emoji_nature_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(parkTypesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goPark(park: ParkKt) {
        goParkSingle(1601616011,park) // ParksActivity
    }

    fun goParks() {
        viewModel.setCurrentParkId(0L)
        navigateTo(1601616011) // ParksActivity
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            parkTypes = viewModel.getParkTypes()
            val listBooleans = parkTypes.map { it.selected }
            val drawableId =
                when (listBooleans.allTrue()) {
                    true -> R.drawable.ic_outline_filter_alt_white_24
                    else -> R.drawable.ic_outline_filter_alt_off_white_24
                }
            actionMenu?.findItem(R.id.menu_filter_by_type)?.icon =
                ContextCompat.getDrawable(requireContext(), drawableId)
        }
    }

    private fun setParkSearchTerm(query: String?) {
        val searchTerm =
            when (query?.length) {
                in 0..1 -> null
                else -> query?.trim()
            }

        if (viewModel.parkSearchTerm.value != searchTerm) {
            viewModel.setParkSearchTerm(searchTerm)
        }
    }

    private fun setSearchMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_parks_fragment, menu)
                actionMenu = menu

                val searchItem = menu.findItem(R.id.menu_search)
                searchItem.isVisible = true
                searchItem.title = getString(R.string.menu_search)

                searchView = searchItem.actionView as SearchView
                searchView.apply {
                    this.setOnSearchClickListener {
                        showHideFilterMenu(false)
                    }
                    this.setOnCloseListener {
                        this.apply {
                            clearFocus()
                            onActionViewCollapsed()
                        }
                        with (binding) {
                            setBackToTopFab(mapsMenuFab, backToTopFab, recyclerView)
                        }
                        showHideFilterMenu(true)
                        true
                    }

                    setOnQueryTextListener(object:
                            SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            setParkSearchTerm(query)
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

            override fun onPrepareMenu(menu: Menu) { setFilterIcon() }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter_by_type -> {
                        getParkTypes()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showHideFilterMenu(show: Boolean) {
        actionMenu?.findItem(R.id.menu_filter_by_type)?.isVisible = show
    }

    private fun setSelection() {
        val updates = mutableListOf<ParkType>()
        for (i in parkTypeCheckBoxes.indices) {
            if (parkTypes[i].selected != parkTypeCheckBoxes[i].isChecked) {
                updates.add(
                    ParkType(
                        parkTypes[i].id,
                        parkTypes[i].type,
                        parkTypes[i].border,
                        parkTypes[i].color,
                        parkTypeCheckBoxes[i].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateParkTypesSelected(updates)
                if (updated > 0) {
                    setFilterIcon()
                    with (binding) {
                        setBackToTopFab(mapsMenuFab, backToTopFab, recyclerView)
                    }
                }
            }
        }
    }

    private fun setSubtitleAndFabs(count: Int) {
        lifecycleScope.launch {
            val total = viewModel.getParksCount()
            val subtitle = String.format(
                getString(R.string.generic_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsBasic(
                    mapsMenuFab,
                    backToTopFab,
                    recyclerView,
                    count.minus(total),
                    count == 0
                )
            }
        }
    }

    fun toggleSelectAll() {
        val checked =
            parkTypesDialogBinding.parkTypesSelectAll.isChecked
        if (checked) {
            parkTypeCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        parkTypesDialogBinding.parkTypesSelectAll.isEnabled = !checked
    }
}
