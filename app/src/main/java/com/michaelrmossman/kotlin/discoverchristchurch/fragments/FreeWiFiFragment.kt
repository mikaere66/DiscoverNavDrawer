package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FreeWiFiAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FreeWiFiLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentFreeWiFiBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FreeWiFiFragment: BaseFragment<FragmentFreeWiFiBinding>(R.layout.fragment_free_wi_fi) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Tested: okay
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.free_wi_fi))
        viewModel.setTitle(title)
        val subtitle =
            getString(R.string.free_wi_fi_fragment_subtitle)
        viewModel.setSubtitle(subtitle)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = FreeWiFiAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_5)
                }
            },
            FreeWiFiLongListener { freeWiFi ->
                goFreeWiFi(freeWiFi)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            freeWiFiFragment = this@FreeWiFiFragment
            recyclerView.adapter = adapter
            setFabsBasic(mapsMenuFab, backToTopFab, recyclerView)
        }

        viewModel.freeWiFiLocations.observe(viewLifecycleOwner) { freeWiFiLocations ->
            adapter.addFooterAndSubmitList(freeWiFiLocations)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goFreeWiFi(freeWiFi: FreeWiFiKt) {
        goFreeWiFiSingle(freeWiFi,623606231) // FreeWiFiActivity
    }

    fun goMaps() {
        viewModel.setCurrentFreeWiFiId(0L)
        navigateTo(623606231) // FreeWiFiActivity
    }
}
