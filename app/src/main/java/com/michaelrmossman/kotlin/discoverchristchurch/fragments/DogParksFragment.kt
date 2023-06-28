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
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParksAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.DogTypesDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentDogParksBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getDogParkBylawWithQuantity
import com.michaelrmossman.kotlin.discoverchristchurch.utils.allTrue
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

// Note that this one of the few fragments that does NOT pass navigation via BaseFragment
@AndroidEntryPoint
class DogParksFragment: BaseFragment<FragmentDogParksBinding>(
    R.layout.fragment_dog_parks
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var dogTypesDialogBinding: DogTypesDialogBinding
    private lateinit var dogTypeCheckBoxes: List<CheckBox>
    private lateinit var dogParkTypes: List<DogTypeKt>
    private lateinit var adapter: DogParksAdapter

    override fun onCheckedChanged(
        buttonView: CompoundButton?, isChecked: Boolean
    ) {
        with (binding) {
            processCheckChangeListener(
                mapsMenuFab,
                backToTopFab,
                dogTypeCheckBoxes, isChecked,
                dogTypesDialogBinding.dogTypesSelectAll,
                recyclerView
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay

        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.dog_parks_title))
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
                        getDogParkTypes()
                        true
                    }
                    else -> false
                }
            } // Must be RESUMED for setFilterIcon()
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        adapter = DogParksAdapter(
            DogParkListener { checkBox, dogPark, index ->
                when (index) { // DetailsActivity, DogLinksFragment, DogDetailsActivity
                    0 -> goDogPark(dogPark, listOf(415604051, 415604126, 415604151))
                    else -> toggleFave(checkBox.isChecked, dogPark.id, ITEM_VIEW_TYPE_ITEM_3)
                }
            },
            DogParkLongListener { dogPark ->
                goEnvironmentSingle(dogPark,415604161) // DogParksActivity
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            dogParksFragment = this@DogParksFragment
            recyclerView.adapter = adapter
        }

        viewModel.dogParks.observe(viewLifecycleOwner) { dogParks ->
            adapter.addFooterAndSubmitList(dogParks)

            setSubtitleAndFabs(dogParks.size)
        }
    }

    fun goEnvironmentMulti() {
        goEnvironmentMulti(415604161) // DogParksActivity
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun getDogParkTypes() {
        val listBooleans = mutableListOf<Boolean>()
        listBooleans.add( 
           // Remember ... index NOT type
            dogParkTypes[7].selected || dogParkTypes[8].selected
        )
        // Excludes the end index element
        for (i in 1 until dogParkTypes.size.minus(1)) {
            listBooleans.add(dogParkTypes[i].selected)
        }
        val listIds = dogParkTypes.map { it.id }
        val listQuantities = dogParkTypes.map { it.count }

        dogTypesDialogBinding =
            DogTypesDialogBinding.inflate(layoutInflater)
        with (dogTypesDialogBinding) {
            dogTypeCheckBoxes = listOf(this.dogTypeSelect1,
                this.dogTypeSelect2, this.dogTypeSelect3,
                this.dogTypeSelect4, this.dogTypeSelect5,
                this.dogTypeSelect6, this.dogTypeSelect7
            )
        }
        dogTypesDialogBinding.apply {
            dogParksFragment = this@DogParksFragment
            for (i in dogTypeCheckBoxes.indices) {
                dogTypeCheckBoxes[i].apply {
                    isChecked = listBooleans[i]
                    setOnCheckedChangeListener(
                        this@DogParksFragment
                    )
                    text = getDogParkBylawWithQuantity(
                        listIds[i].toInt(), listQuantities[i]
                    )
                }
            }
        }

        val checkedCount = getCheckedCount(dogTypeCheckBoxes)
        dogTypesDialogBinding.dogTypesSelectAll.apply {
            isChecked = checkedCount == dogTypeCheckBoxes.size
            isEnabled = checkedCount < dogTypeCheckBoxes.size
        }

        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_directions_walk_black_24)
            setTitle(R.string.menu_filter_by_type)
            setView(dogTypesDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                setSelection()
            }
        }
        alertDialog =  alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun setFilterIcon() {
        lifecycleScope.launch {
            dogParkTypes = viewModel.getDogParkTypes()
            val listBooleans = dogParkTypes.map { it.selected }
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
        val updates = mutableListOf<DogType>()
        // Excludes the end index element
        for (i in 1 until dogTypeCheckBoxes.size) {
            if (dogParkTypes[i].selected != dogTypeCheckBoxes[i].isChecked) {
                updates.add(
                    DogType(
                        dogParkTypes[i].id,
                        dogParkTypes[i].bylaw,
                        dogParkTypes[i].border,
                        dogParkTypes[i].color,
                        dogTypeCheckBoxes[i].isChecked
                    )
                )
            }
        }
        for (i in 7..8) { // Remember: index NOT type
            if (dogParkTypes[i].selected != dogTypeCheckBoxes[0].isChecked) {
                updates.add(
                    DogType(
                        dogParkTypes[i].id,
                        dogParkTypes[i].bylaw,
                        dogParkTypes[i].border,
                        dogParkTypes[i].color,
                        dogTypeCheckBoxes[0].isChecked
                    )
                )
            }
        }
        if (updates.isNotEmpty()) {
            lifecycleScope.launch {
                val updated = viewModel.updateDogTypesSelected(updates)
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
            val total = viewModel.getDogParksCount()
            val subtitle = String.format(
                getString(R.string.dogs_fragment_subtitle),
                count, total
            )
            viewModel.setSubtitle(subtitle)

            with (binding) {
                setFabsExtra(
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
            dogTypesDialogBinding.dogTypesSelectAll.isChecked
        if (checked) {
            dogTypeCheckBoxes.forEach { checkBox ->
                checkBox.isChecked = true
            }
        }
        dogTypesDialogBinding.dogTypesSelectAll.isEnabled = !checked
    }
}
