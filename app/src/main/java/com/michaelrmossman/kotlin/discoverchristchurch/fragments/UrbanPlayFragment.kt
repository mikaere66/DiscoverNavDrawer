package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.UrbanPlayAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentUrbanPlayBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UrbanPlayFragment: BaseFragment<FragmentUrbanPlayBinding>(R.layout.fragment_urban_play) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitleSubtitle()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UrbanPlayAdapter(
            UrbanPlayListener { checkBox, index, urbanPlay ->
                when (index) {
                    0 -> showUrbanPlayImage(urbanPlay)
                    else -> toggleFave(checkBox.isChecked, urbanPlay.id, ITEM_VIEW_TYPE_ITEM_12)
                }
            },
            UrbanPlayLongListener { urbanPlay ->
                goUrbanPlay(urbanPlay)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            urbanPlayFragment = this@UrbanPlayFragment
            recyclerView.adapter = adapter
            setFabsBasic(mapsMenuFab, backToTopFab, recyclerView)
        }

        viewModel.urbanPlayItems.observe(viewLifecycleOwner) { urbanPlayItems ->
            adapter.addFooterAndSubmitList(urbanPlayItems)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goUrbanPlay(urbanPlay: UrbanPlayKt) {
        goUrbanPlaySingle(2118621181, urbanPlay) // UrbanPlayActivity
    }

    fun goUrbanPlayMulti() {
        viewModel.setCurrentUrbanPlayId(0L)
        navigateTo(2118621181) // UrbanPlayActivity
    }

    private fun setTitleSubtitle() {
        val title =
            String.format(getString(R.string.app_title),
                getString(R.string.urban_play))
        viewModel.setTitle(title)

        val subtitle = getString(R.string.urban_play_subtitle)
        viewModel.setSubtitle(subtitle)
    }
}
