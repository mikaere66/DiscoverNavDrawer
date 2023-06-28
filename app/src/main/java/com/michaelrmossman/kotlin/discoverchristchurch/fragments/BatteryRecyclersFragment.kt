package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BatteryRecyclersAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BatteryRecyclerLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentBatteryRecyclersBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BatteryRecyclersFragment: BaseFragment<FragmentBatteryRecyclersBinding>(R.layout.fragment_battery_recyclers) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.battery_recyclers))
        viewModel.setTitle(title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BatteryRecyclersAdapter(
            BasicClickListener { checkBox, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(checkBox.isChecked, itemId, ITEM_VIEW_TYPE_ITEM_1)
                }
            },
            BatteryRecyclerLongListener { batteryRecycler ->
                goBatteryRecycler(batteryRecycler)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            batteryRecyclersFragment = this@BatteryRecyclersFragment
            recyclerView.adapter = adapter
            setFabsBasic(mapsMenuFab, backToTopFab, recyclerView)
        }

        viewModel.batteryRecyclers.observe(viewLifecycleOwner) { batteryRecyclers ->
            adapter.addFooterAndSubmitList(batteryRecyclers)

            setSubtitle(batteryRecyclers.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goBatteryRecycler(batteryRecycler: BatteryRecyclerKt) {
        goBatteryRecyclerSingle(batteryRecycler,201602011) // BatteryRecyclersActivity
    }

    fun goBatteryRecyclers() {
        viewModel.setCurrentBatteryRecyclerId(0L)
        navigateTo(201602011) // BatteryRecyclersActivity
    }

    private fun setSubtitle(count: Int) {
        val subtitle =
            String.format(getString(R.string.battery_recyclers_fragment_subtitle),
                count)
        viewModel.setSubtitle(subtitle)
    }
}
