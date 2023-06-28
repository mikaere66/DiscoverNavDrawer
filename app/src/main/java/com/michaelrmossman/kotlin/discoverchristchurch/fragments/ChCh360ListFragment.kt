package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.ChCh360Adapter
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360Listener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360LongListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentChCh360ListBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_CH_CH_360_TAG
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChCh360ListFragment: BaseFragment<FragmentChCh360ListBinding>(R.layout.fragment_ch_ch_360_list) {

    override fun onResume() {
        super.onResume() // Tested: Required

        lifecycleScope.launch {
            val chCh360ItemsCount = viewModel.getChCh360ItemsCount()
            setTitleSubtitle(chCh360ItemsCount)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ChCh360Adapter(
            ChCh360Listener { chCh360, checkBox, index ->
                when (index) {
                    0 ->  goChCh360Detail(chCh360)
                    else -> toggleFave(checkBox.isChecked, chCh360.id, ITEM_VIEW_TYPE_ITEM_00)
                }
            },
            ChCh360LongListener { chCh360 ->
                goChCh360Map(chCh360)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter

        viewModel.chCh360ItemsKt.observe(viewLifecycleOwner) { chCh360Items ->
            adapter.submitList(chCh360Items)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_ch_ch_360_fragment, menu)
                if (menu is MenuBuilder) { // Always show icons in overflow
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                        R.id.menu_overview -> {
                        showImage(1)
                        true
                    }
                        R.id.menu_360 -> {
                        goChCh360Maps()
                        true
                    }
                        R.id.menu_pie -> {
                        showImage(0)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun goChCh360Detail(chCh360: ChCh360Kt) {
        goChCh360Details(chCh360,308603080)
    }

    private fun goChCh360Map(chCh360: ChCh360Kt) {
        goChCh360Maps(chCh360,308603081)
    }

    private fun goChCh360Maps() {
        viewModel.setRoutesMenuVisible(false)
        // Reset chCh360ItemId to 0L to infer ALL items
        viewModel.setChCh360ItemId(0L)
        navigateTo(308603082) // ChCh362Activity
    }

    private fun setTitleSubtitle(count: Int) {
        val title = String.format(getString(R.string.app_title),
            getString(R.string.ch_ch_360_title)
        )
        viewModel.setTitle(title)

        val subtitle = String.format(
            getString(R.string.ch_ch_360_subtitle),
            count
        )
        viewModel.setSubtitle(subtitle)
    }

    private fun showImage(index: Int) {
        activity?.let {
            ChCh360ImageFragment.create(index).show(
                it.supportFragmentManager,
                FRAGMENT_CH_CH_360_TAG
            )
        }
    }
}
