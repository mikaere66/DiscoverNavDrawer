package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
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
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FacilityLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FacilitiesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FacilityTypesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentFacilitiesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFacilityTypeWithQuantity
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.utils.LIBRARY_FILTER_QUERY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FacilitiesFragment: BaseFragment<FragmentFacilitiesBinding>(
    R.layout.fragment_facilities
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var facilityTypesDialogBinding: FacilityTypesDialogBinding
    private lateinit var facilityTypeImageViews: List<ImageView>
    private lateinit var facilityTypeCheckBoxes: List<CheckBox>
    private lateinit var facilityTypes: List<FacilityTypeKt>
    private lateinit var adapter: FacilitiesAdapter
    private lateinit var searchView: SearchView

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                facilityTypeCheckBoxes, isChecked,
                facilityTypesDialogBinding.facilityTypesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.facilities_fragment_title))
        viewModel.setTitle(title)
    }

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setFacilitySearchTerm(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FacilitiesAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_2)
                }
            },
            FacilityLongListener { facility ->
                goFacility(facility)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            facilitiesFragment = this@FacilitiesFragment
            recyclerView.adapter = adapter
        }

        // Reset facilities filter to ensure NOT library
        setFacilitySearchTerm(null)

        setSearchMenu()

        viewModel.facilitiesSearchedBy.observe(viewLifecycleOwner) { facilities ->
            adapter.addFooterAndSubmitList(facilities)

            setSubtitleAndFabs(facilities.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getFacilityTypes() {
        val listBooleans = mutableListOf<Boolean>()
        for (i in facilityTypes.indices) {
            listBooleans.add(facilityTypes[i].selected)
        }
        val listIds = facilityTypes.map { it.id }
        val listQuantities = facilityTypes.map { it.count }

        facilityTypesDialogBinding =
            FacilityTypesDialogBinding.inflate(layoutInflater)
        with (facilityTypesDialogBinding) {
            facilityTypeCheckBoxes = listOf(this.facilityTypeSelect01,
                 this.facilityTypeSelect02, this.facilityTypeSelect03, this.facilityTypeSelect04, this.facilityTypeSelect05,
                this.facilityTypeSelect06, this.facilityTypeSelect07, this.facilityTypeSelect08, this.facilityTypeSelect09,
                this.facilityTypeSelect10, this.facilityTypeSelect11, this.facilityTypeSelect12, this.facilityTypeSelect13
            )
            facilityTypeImageViews = listOf(this.facilityTypeColor01,
                 this.facilityTypeColor02, this.facilityTypeColor03, this.facilityTypeColor04, this.facilityTypeColor05,
                this.facilityTypeColor06, this.facilityTypeColor07, this.facilityTypeColor08, this.facilityTypeColor09,
                this.facilityTypeColor10, this.facilityTypeColor11, this.facilityTypeColor12, this.facilityTypeColor13
            )
        }
        facilityTypesDialogBinding.apply {
            facilitiesFragment = this@FacilitiesFragment
            for (i in facilityTypeCheckBoxes.indices) {
                facilityTypeCheckBoxes[i].apply {
                    isChecked = listBooleans[i]
                    setOnCheckedChangeListener(
                        this@FacilitiesFragment
                    )
                    text = getFacilityTypeWithQuantity(
                        listIds[i].toInt(), listQuantities[i]
                    )
                }
            }
            for (i in facilityTypeImageViews.indices) {
                val color = Color.parseColor(facilityTypes[i].color)
                facilityTypeImageViews[i].setColorFilter(color)
            }
        }

        val checkedCount = getCheckedCount(facilityTypeCheckBoxes)
        facilityTypesDialogBinding.facilityTypesSelectAll.apply {
            isChecked = checkedCount == facilityTypeCheckBoxes.size
            isEnabled = checkedCount < facilityTypeCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_public_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(facilityTypesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goFacility(facility: FacilityKt) {
        goFacilitySingle(facility,601606011) // FacilitiesActivity
    }

    fun goFacilities() {
        viewModel.setCurrentFacilityId(0L)
        navigateTo(601606011) // FacilitiesActivity
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            facilityTypes = viewModel.getFacilityTypes()
            val listBooleans = facilityTypes.map { it.selected }
            val drawableId =
                when (listBooleans.allTrue()) {
                    true -> R.drawable.ic_outline_filter_alt_white_24
                    else -> R.drawable.ic_outline_filter_alt_off_white_24
                }
            actionMenu?.findItem(R.id.menu_filter_by_type)?.icon =
                ContextCompat.getDrawable(requireContext(), drawableId)
        }
    }

    private fun setFacilitySearchTerm(query: String?) {
        val searchTerm = when (query?.length) {
            in 0..1 -> null
            else -> query?.trim()
        }
        if (viewModel.facilitySearchTerm.value != searchTerm) {
            viewModel.setFacilitySearchTerm(searchTerm)
        }
    }

    private fun setLibraryTypes(checked: Boolean) {
        val query = when (checked) {
            true -> LIBRARY_FILTER_QUERY
            else -> null
        }
        setFacilitySearchTerm(query)
    }

    private fun setSearchMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_facilities_fragment, menu)
                actionMenu = menu

                val searchItem = menu.findItem(R.id.menu_search)
                searchItem.isVisible = true
                searchItem.title = getString(R.string.menu_search)

                searchView = searchItem.actionView as SearchView
                searchView.apply {
                    this.setOnSearchClickListener {
                        showHideMenuItems(false)
                    }
                    this.setOnCloseListener {
                        this.apply {
                            clearFocus()
                            onActionViewCollapsed()
                        }
                        with (binding) {
                            setBackToTopFab(mapsMenuFab, backToTopFab, recyclerView)
                        }
                        showHideMenuItems(true)
                        true
                    }

                    setOnQueryTextListener(object:
                            SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            return false
                        }

                        override fun onQueryTextChange(query: String?): Boolean {
                            setFacilitySearchTerm(query)
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

            override fun onPrepareMenu(menu: Menu) {
                menu.findItem(R.id.menu_filter_by_library).isChecked =
                    viewModel.facilitySearchTerm.value == LIBRARY_FILTER_QUERY
                setFilterIcon()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter_by_type -> {
                        getFacilityTypes()
                        true
                    }
                    R.id.menu_filter_by_library -> {
                        val isChecked = !menuItem.isChecked
                        menuItem.isChecked = isChecked
                        setLibraryTypes(isChecked)
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showHideMenuItems(show: Boolean) {
        actionMenu?.findItem(R.id.menu_filter_by_type)?.isVisible = show
        actionMenu?.findItem(R.id.menu_filter_by_library)?.isVisible = show
    }

    private fun setSelection() {
        val updates = mutableListOf<FacilityType>()
        for (i in facilityTypeCheckBoxes.indices) {
            if (facilityTypes[i].selected != facilityTypeCheckBoxes[i].isChecked) {
                updates.add(
                    FacilityType(
                        facilityTypes[i].id,
                        facilityTypes[i].type,
                        facilityTypes[i].color,
                        facilityTypeCheckBoxes[i].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateFacilityTypesSelected(updates)
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
            val total = viewModel.getFacilitiesCount()
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
            facilityTypesDialogBinding.facilityTypesSelectAll.isChecked
        if (checked) {
            facilityTypeCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        facilityTypesDialogBinding.facilityTypesSelectAll.isEnabled = !checked
    }
}
