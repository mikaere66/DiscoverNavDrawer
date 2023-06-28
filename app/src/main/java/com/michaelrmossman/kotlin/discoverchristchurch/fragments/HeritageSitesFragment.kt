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
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.HeritageSitesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentHeritageSitesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.HeritageTypesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getHeritageTypeWithQuantity
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HeritageSitesFragment: BaseFragment<FragmentHeritageSitesBinding>(
    R.layout.fragment_heritage_sites
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var heritageTypesDialogBinding: HeritageTypesDialogBinding
    private lateinit var heritageTypeCheckBoxes: List<CheckBox>
    private lateinit var heritageTypes: List<HeritageTypeKt>

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                heritageTypeCheckBoxes, isChecked,
                heritageTypesDialogBinding.heritageTypesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.heritage_sites))
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
                        getSiteTypes()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val adapter = HeritageSitesAdapter(
            HeritageSiteListener { checkBox, heritageSite, index ->
                when (index) {
                    0 -> showHeritageSiteImage(heritageSite)
                    else -> toggleFave(checkBox.isChecked, heritageSite.id, ITEM_VIEW_TYPE_ITEM_7)
                }
            },
            HeritageSiteLongListener { heritageSite ->
                goHeritageSite(heritageSite)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            heritageSitesFragment = this@HeritageSitesFragment
            recyclerView.adapter = adapter
        }

        viewModel.heritageSites.observe(viewLifecycleOwner) { heritageSites ->
            adapter.addFooterAndSubmitList(heritageSites)

            setSubtitleAndFabs(heritageSites.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getSiteTypes() {
        val listBooleans = mutableListOf<Boolean>()
        for (i in heritageTypes.indices) {
            listBooleans.add(heritageTypes[i].selected)
        }
        val listIds = heritageTypes.map { it.id }
        val listQuantities = heritageTypes.map { it.count }

        heritageTypesDialogBinding =
            HeritageTypesDialogBinding.inflate(layoutInflater)
        with (heritageTypesDialogBinding) {
            heritageTypeCheckBoxes = listOf(
                this.heritageTypeSelect1, this.heritageTypeSelect2,
                this.heritageTypeSelect3, this.heritageTypeSelect4,
                this.heritageTypeSelect5, this.heritageTypeSelect6,
                this.heritageTypeSelect7, this.heritageTypeSelect8
            )
        }
        heritageTypesDialogBinding.apply {
            heritageSitesFragment = this@HeritageSitesFragment
            for (i in heritageTypeCheckBoxes.indices) {
                heritageTypeCheckBoxes[i].apply {
                    isChecked = listBooleans[i]
                    setOnCheckedChangeListener(
                        this@HeritageSitesFragment
                    )
                    text = getHeritageTypeWithQuantity(
                        listIds[i].toInt(), listQuantities[i]
                    )
                }
            }
        }

        val checkedCount = getCheckedCount(heritageTypeCheckBoxes)
        heritageTypesDialogBinding.heritageTypesSelectAll.apply {
            isChecked = checkedCount == heritageTypeCheckBoxes.size
            isEnabled = checkedCount < heritageTypeCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_location_city_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(heritageTypesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goHeritageSite(heritageSite: HeritageSiteKt) {
        goHeritageSiteSingle(heritageSite,805608051) // HeritageSitesActivity
    }

    fun goHeritageSiteMulti() {
        viewModel.setCurrentHeritageSiteId(0L)
        navigateTo(805608051) // HeritageSitesActivity
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            heritageTypes = viewModel.getHeritageTypes()
            val listBooleans = heritageTypes.map { it.selected }
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
        val updates = mutableListOf<HeritageType>()
        for (i in heritageTypeCheckBoxes.indices) {
            if (heritageTypes[i].selected != heritageTypeCheckBoxes[i].isChecked) {
                updates.add(
                    HeritageType(
                        heritageTypes[i].id,
                        heritageTypes[i].color,
                        heritageTypes[i].type,
                        heritageTypeCheckBoxes[i].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateHeritageTypesSelected(updates)
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
            val total = viewModel.getHeritageSitesCount()
            val subtitle = String.format(
                getString(R.string.heritage_sites_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsExtra( // TODO
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
            heritageTypesDialogBinding.heritageTypesSelectAll.isChecked
        if (checked) {
            heritageTypeCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        heritageTypesDialogBinding.heritageTypesSelectAll.isEnabled = !checked
    }
}
