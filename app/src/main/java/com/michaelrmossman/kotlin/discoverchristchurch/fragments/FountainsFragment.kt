package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FountainLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FountainsAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FountainsDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentFountainsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FountainsFragment: BaseFragment<FragmentFountainsBinding>(
    R.layout.fragment_fountains
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var fountainsDialogBinding: FountainsDialogBinding
    private lateinit var fountainCheckBoxes: List<CheckBox>
    private lateinit var fountainTypes: List<DrinkType>
    private lateinit var adapter: FountainsAdapter

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                fountainCheckBoxes, isChecked,
                fountainsDialogBinding.fountainsSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.drink_fountains))
        viewModel.setTitle(title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_filter_by_type, menu)
                actionMenu = menu
            }

            override fun onPrepareMenu(menu: Menu) { setFilterIcon() }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_filter_by_type -> {
                        getFountainTypes()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = FountainsAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_4)
                }
            },
            FountainLongListener { fountain ->
                goFountain(fountain)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            fountainsFragment = this@FountainsFragment
            recyclerView.adapter = adapter
        }

        viewModel.drinkFountains.observe(viewLifecycleOwner) { fountains ->
            adapter.addFooterAndSubmitList(fountains)

            setSubtitleAndFabs(fountains.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getFountainTypes() {
        fountainsDialogBinding =
            FountainsDialogBinding.inflate(layoutInflater)
        with (fountainsDialogBinding) {
            fountainCheckBoxes = listOf(
                this.fountainSelect1,
                this.fountainSelect2,
                this.fountainSelect3
            )
        }

        fountainsDialogBinding.apply {
            fountainsFragment = this@FountainsFragment

            for (i in fountainCheckBoxes.indices) {
                fountainCheckBoxes[i].apply {
                    isChecked = fountainTypes[i].selected
                    setOnCheckedChangeListener(
                        this@FountainsFragment
                    )
                }
            }
        }

        val checkedCount = getCheckedCount(fountainCheckBoxes)
        fountainsDialogBinding.fountainsSelectAll.apply {
            isChecked = checkedCount == fountainCheckBoxes.size
            isEnabled = checkedCount < fountainCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_water_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(fountainsDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goFountain(fountain: DrinkFountainKt) {
        goFountainSingle(fountain,615606151) // FountainsActivity
    }

    fun goFountains() {
        viewModel.setCurrentFountainId(0L)
        navigateTo(615606151) // FountainsActivity
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            fountainTypes = viewModel.getFountainTypesList()
            val listBooleans = fountainTypes.map { it.selected }
            val drawableId =
                when (listBooleans.allTrue()) {
                    true -> R.drawable.ic_outline_filter_alt_white_24
                    else -> R.drawable.ic_outline_filter_alt_off_white_24
                }
            actionMenu?.findItem(R.id.menu_filter_by_type)?.icon =
                ContextCompat.getDrawable(requireContext(), drawableId)
        }
    }

    private fun setSelection() {
        val updates = mutableListOf<DrinkType>()
        for (i in fountainCheckBoxes.indices) {
            if (fountainTypes[i].selected != fountainCheckBoxes[i].isChecked) {
                updates.add(
                    DrinkType(
                        fountainTypes[i].id,
                        fountainTypes[i].type,
                        fountainCheckBoxes[i].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateDrinkTypesSelected(updates)
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
            val total = viewModel.getFountainsCount()
            val subtitle = String.format(
                getString(R.string.fountains_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsBasic(
                    mapsMenuFab,
                    backToTopFab,
                    recyclerView,
                    count.minus(total)
                )
            }
        }
    }

    fun toggleSelectAll() {
        val checked =
            fountainsDialogBinding.fountainsSelectAll.isChecked
        if (checked) {
            fountainCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        fountainsDialogBinding.fountainsSelectAll.isEnabled = !checked
    }
}
