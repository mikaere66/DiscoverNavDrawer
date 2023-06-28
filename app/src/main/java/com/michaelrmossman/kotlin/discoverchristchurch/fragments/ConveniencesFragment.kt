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
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ConvenienceLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ConveniencesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ConveniencesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentConveniencesBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConveniencesFragment: BaseFragment<FragmentConveniencesBinding>(
    R.layout.fragment_conveniences
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var conveniencesDialogBinding: ConveniencesDialogBinding
    private lateinit var convenienceTypes: List<ConvenienceType>
    private lateinit var convenienceCheckBoxes: List<CheckBox>
    private lateinit var adapter: ConveniencesAdapter

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                convenienceCheckBoxes, isChecked,
                conveniencesDialogBinding.conveniencesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.public_cons))
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
                        getConvenienceTypes()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = ConveniencesAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_9)
                }
            },
            ConvenienceLongListener { convenience ->
                goConvenience(convenience)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            conveniencesFragment = this@ConveniencesFragment
            recyclerView.adapter = adapter
        }

        viewModel.conveniences.observe(viewLifecycleOwner) { conveniences ->
            adapter.addFooterAndSubmitList(conveniences)

            setSubtitleAndFabs(conveniences.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getConvenienceTypes() {
        conveniencesDialogBinding =
            ConveniencesDialogBinding.inflate(layoutInflater)
        with (conveniencesDialogBinding) {
            convenienceCheckBoxes = listOf(
                this.convenienceSelect1,
                this.convenienceSelect2
            )
        }

        conveniencesDialogBinding.apply {
            conveniencesFragment = this@ConveniencesFragment

            for (i in convenienceCheckBoxes.indices) {
                convenienceCheckBoxes[i].apply {
                    isChecked = convenienceTypes[i].selected
                    setOnCheckedChangeListener(
                        this@ConveniencesFragment
                    )
                }
            }
        }

        val checkedCount = getCheckedCount(convenienceCheckBoxes)
        conveniencesDialogBinding.conveniencesSelectAll.apply {
            isChecked = checkedCount == convenienceCheckBoxes.size
            isEnabled = checkedCount < convenienceCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_house_siding_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(conveniencesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun goConvenience(convenience: ConvenienceKt) {
        goConvenienceSingle(convenience,315603151)
    }

    fun goConveniences() {
        viewModel.setCurrentConvenienceId(0L)
        navigateTo(315603151)
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            convenienceTypes = viewModel.getConvenienceTypesList()
            val listBooleans = convenienceTypes.map { it.selected }
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
        val updates = mutableListOf<ConvenienceType>()
        for (i in convenienceCheckBoxes.indices) {
            if (convenienceTypes[i].selected != convenienceCheckBoxes[i].isChecked) {
                updates.add(
                    ConvenienceType(
                        convenienceTypes[i].id,
                        convenienceCheckBoxes[i].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateConvenienceTypesSelected(updates)
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
            val total = viewModel.getConveniencesCount()
            val subtitle = String.format(
                getString(R.string.conveniences_fragment_subtitle),
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
            conveniencesDialogBinding.conveniencesSelectAll.isChecked
        if (checked) {
            convenienceCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        conveniencesDialogBinding.conveniencesSelectAll.isEnabled = !checked
    }
}
