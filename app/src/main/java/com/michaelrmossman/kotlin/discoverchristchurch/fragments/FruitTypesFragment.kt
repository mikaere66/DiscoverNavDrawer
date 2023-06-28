package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Html
//import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FruitTypeLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FruitTypesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FruitSeasonsDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FruitTypesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentFruitTypesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCat
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCatKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRUIT_SEASON_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.NavUtils.navigateTo
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFruitCatWithQuantity
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.utils.selectedIndex
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Note: this is one of the few fragments that does NOT use BaseFragment for navigation
@AndroidEntryPoint
class FruitTypesFragment: BaseFragment<FragmentFruitTypesBinding>(
    R.layout.fragment_fruit_types
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var fruitSeasonsDialogBinding: FruitSeasonsDialogBinding
    private lateinit var fruitTypesDialogBinding: FruitTypesDialogBinding
    private lateinit var fruitTypeImageViews: List<ImageView>
    private lateinit var fruitTypeCheckBoxes: List<CheckBox>
    private lateinit var fruitTreeCats: List<FruitCatKt>

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                fruitTypeCheckBoxes, isChecked,
                fruitTypesDialogBinding.fruitTypesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.fruit_fragment_title))
        viewModel.setTitle(title)
    }

    override fun onDestroy() {
        viewModel.setOverflowIconIndex(0) // Reset to Default
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fruit_trees_fragment, menu)
                actionMenu = menu
            }

            override fun onPrepareMenu(menu: Menu) {
                setFilterIcon()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter_by_cat -> {
                        getFruitTypeCats()
                        true
                    }
                    R.id.menu_filter_by_sea -> {
                        getFruitSeason()
                        true
                    }
                    R.id.menu_filter_clear -> {
                        clearFilters()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = FruitTypesAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_6)
                }
            },
            FruitTypeLongListener { fruitType ->
                goFruitType(fruitType)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            fruitTypesFragment = this@FruitTypesFragment
            recyclerView.adapter = adapter
        }

        viewModel.fruitTypes.observe(viewLifecycleOwner) { fruitTypes ->
            adapter.addFooterAndSubmitList(fruitTypes)

            setSubtitleAndFabs(fruitTypes.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun clearFilters() {
        val updates = mutableListOf<FruitCat>()
        for (i in fruitTreeCats.indices) {
            with (fruitTreeCats[i]) {
                updates.add(getFruitCat(id, category, color, months,true))
            }
        }
        updateDBandUI(-1, updates)
    }

    private fun getFruitCat(
        id: Long, cat: String, color: String, mths: String, selected: Boolean
    ) : FruitCat = FruitCat(id, cat, color, mths, selected)

    private fun getFruitSeason() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        fruitSeasonsDialogBinding = FruitSeasonsDialogBinding.inflate(layoutInflater)
        fruitSeasonsDialogBinding.fruitSeasonsNote.text =
            Html.fromHtml(getString(R.string.fruit_trees_note),
                Html.FROM_HTML_MODE_COMPACT)

        lateinit var fruitCatRadioButtons: List<RadioButton>
        val selectedSeason = sharedPrefs.getInt(FRUIT_SEASON_PREF, -1)
        if (selectedSeason != -1) {
            with (fruitSeasonsDialogBinding) {
                fruitCatRadioButtons = listOf(
                    fruitSeasonSummer, fruitSeasonAutumn,
                    fruitSeasonWinter, fruitSeasonSpring
                )
            }
            val current = getString(R.string.fruit_trees_current)
            fruitSeasonsDialogBinding.apply {
                fruitCatRadioButtons[selectedSeason].append(current)
            }
        }

        alertDialogBuilder.apply {
            setView(fruitSeasonsDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
        }
        alertDialog = alertDialogBuilder.create()
        val radioGroup = fruitSeasonsDialogBinding.fruitSeasonsRadioGroup
        radioGroup.apply {
            setOnCheckedChangeListener { _, _ ->
                alertDialog?.dismiss()
                setSeason(this.selectedIndex())
            }
        }
        alertDialog?.show()
    }

    private fun getFruitTypeCats() {
        val listBooleans = fruitTreeCats.map { it.selected }

        fruitTypesDialogBinding =
            FruitTypesDialogBinding.inflate(layoutInflater)
        with (fruitTypesDialogBinding) {
            fruitTypeCheckBoxes = listOf(
                this.fruitTypeSelect01, this.fruitTypeSelect02, this.fruitTypeSelect03,
                this.fruitTypeSelect04, this.fruitTypeSelect05, this.fruitTypeSelect06,
                this.fruitTypeSelect07, this.fruitTypeSelect08, this.fruitTypeSelect09,
                this.fruitTypeSelect10, this.fruitTypeSelect11, this.fruitTypeSelect12,
                this.fruitTypeSelect13, this.fruitTypeSelect14, this.fruitTypeSelect15,
                this.fruitTypeSelect16, this.fruitTypeSelect17, this.fruitTypeSelect18,
                this.fruitTypeSelect19, this.fruitTypeSelect20, this.fruitTypeSelect21
            )
            fruitTypeImageViews = listOf(
                this.fruitTypeColor01, this.fruitTypeColor02, this.fruitTypeColor03,
                this.fruitTypeColor04, this.fruitTypeColor05, this.fruitTypeColor06,
                this.fruitTypeColor07, this.fruitTypeColor08, this.fruitTypeColor09,
                this.fruitTypeColor10, this.fruitTypeColor11, this.fruitTypeColor12,
                this.fruitTypeColor13, this.fruitTypeColor14, this.fruitTypeColor15,
                this.fruitTypeColor16, this.fruitTypeColor17, this.fruitTypeColor18,
                this.fruitTypeColor19, this.fruitTypeColor20, this.fruitTypeColor21
            )
        }
        fruitTypesDialogBinding.apply {
            fruitTypesFragment = this@FruitTypesFragment
            for (i in fruitTypeCheckBoxes.indices) {
                fruitTypeCheckBoxes[i].apply {
                    isChecked = listBooleans[i]
                    setOnCheckedChangeListener(
                        this@FruitTypesFragment
                    )
                    text = getFruitCatWithQuantity(
                        fruitTreeCats[i]
                    )
                }
            }
            for (i in fruitTypeImageViews.indices) {
                val color = Color.parseColor(fruitTreeCats[i].color)
                fruitTypeImageViews[i].setColorFilter(color)
            }
        }

        val checkedCount = getCheckedCount(fruitTypeCheckBoxes)
        fruitTypesDialogBinding.fruitTypesSelectAll.apply {
            isChecked = checkedCount == fruitTypeCheckBoxes.size
            isEnabled = checkedCount < fruitTypeCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_local_florist_black_24)
            setTitle(R.string.menu_filter_by_cat)
            setView(fruitTypesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setCategories()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goFruitType(fruitType: FruitTypeKt) {
        goFruitTypeSingle(fruitType,618606181) // FruitTreesActivity
    }

    fun goFruitTrees() {
        viewModel.setCurrentFruitTypeId(0L)
        navigateTo(618606181, findNavController()) // FruitTreesActivity
    }

    private fun setCategories() {
        val updates = mutableListOf<FruitCat>()
        for (i in fruitTypeCheckBoxes.indices) {
            if (fruitTreeCats[i].selected != fruitTypeCheckBoxes[i].isChecked) {
                val selected = fruitTypeCheckBoxes[i].isChecked
                with (fruitTreeCats[i]) {
                    updates.add(getFruitCat(id, category, color, months, selected))
                }
            }
        }
        updateDBandUI(-1, updates)
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            fruitTreeCats = viewModel.getFruitCatsListKt()
            val listBooleans = fruitTreeCats.map { it.selected }
            val filtered = !listBooleans.allTrue() // Note NOT

            actionMenu?.apply {
                findItem(R.id.menu_filter_clear)?.isEnabled = filtered

                val byCategoryMenuItem = findItem(R.id.menu_filter_by_cat)
                val bySeasonMenuItem = findItem(R.id.menu_filter_by_sea) // MenuItems

                val byCategoryStringId = R.string.menu_filter_by_cat    // String IDs
                val bySeasonStingId = R.string.menu_filter_by_sea

                when (filtered) {
                    true -> { // Place a checkmark next to the CURRENT filter type
                        val selectedSeason = sharedPrefs.getInt(FRUIT_SEASON_PREF, -1)

                        val selectedStringId = when (selectedSeason) {
                            -1 -> byCategoryStringId
                            else -> bySeasonStingId
                        }
                        val selectedMenuItem = when (selectedSeason) {
                            -1 -> byCategoryMenuItem
                            else -> bySeasonMenuItem
                        }
                        val title = getString(selectedStringId)
                        val check = "\u00A0\u2713" // Space+Checkmark
                        selectedMenuItem.title = "$title$check"

                        val unselectedMenuItem = when (selectedSeason) {
                            -1 -> bySeasonMenuItem
                            else -> byCategoryMenuItem
                        }
                        val unselectedStringId = when (selectedSeason) {
                            -1 -> bySeasonStingId
                            else -> byCategoryStringId
                        }
                        unselectedMenuItem.title = getString(unselectedStringId)
                    }
                    else -> { // Restore default menuItem title(s) without check
                        byCategoryMenuItem.title = getString(byCategoryStringId)
                        bySeasonMenuItem.title = getString(bySeasonStingId)
                    }
                }
            }

            viewModel.setOverflowIconIndex( // Trigger update for overflow icon
                when (filtered) {
                    true -> 2
                    else -> 1
                }
            )
        }
    }

    private fun setSeason(season: Int) {
        val updates = mutableListOf<FruitCat>()
        val monthsInSeason = when (season) {
            0 -> arrayOf("12","1","2")      // Summer
            1 -> arrayOf("3","4","5")       // Autumn
            2 -> arrayOf("6","7","8")       // Winter
            else -> arrayOf("9","10","11")  // Spring
        }
        for (i in fruitTreeCats.indices) {
            var selected = false
            val selection = fruitTreeCats[i].months.split(",")
            monthsInSeason.forEach { month ->
                if (selection.contains(month)) {
                    // Log.d("MONTHS","$selection | $month")
                    selected = true
                    return@forEach
                }
            }
            with (fruitTreeCats[i]) {
                updates.add(getFruitCat(id, category, color, months, selected))
            }
        }
        updateDBandUI(season, updates)
    }

    private fun setSubtitleAndFabs(count: Int) {
        lifecycleScope.launch {
            val total = viewModel.getFruitTypesCount()
            val subtitle = String.format(
                getString(R.string.fruit_trees_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsBasic(mapsMenuFab, backToTopFab, recyclerView, count.minus(total))
            }
        }
    }

    fun toggleSelectAll() {
        val checked =
            fruitTypesDialogBinding.fruitTypesSelectAll.isChecked
        if (checked) {
            fruitTypeCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        fruitTypesDialogBinding.fruitTypesSelectAll.isEnabled = !checked
    }

    private fun updateDBandUI(selectedSeason: Int, updates: MutableList<FruitCat>) {
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateFruitCatsSelected(updates)
                if (updated > 0 &&
                        sharedPrefs.edit().putInt(FRUIT_SEASON_PREF,
                            selectedSeason).commit()) {
                    setFilterIcon()
                    with (binding) {
                        setBackToTopFab(mapsMenuFab, backToTopFab, recyclerView)
                    }
                }
            }
        }
    }
}
