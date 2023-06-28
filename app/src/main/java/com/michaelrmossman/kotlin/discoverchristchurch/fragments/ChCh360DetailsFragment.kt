package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentChCh360DetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_POSITION_EXTRA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChCh360DetailsFragment: BaseFragment<FragmentChCh360DetailsBinding>(R.layout.fragment_ch_ch_360_details) {

    private var chCh360Item: ChCh360Kt? = null

    companion object {
        fun create(position: Int) =
            ChCh360DetailsFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_POSITION_EXTRA, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(FRAGMENT_POSITION_EXTRA)?.let { position ->
            if (viewModel.chCh360Items.isNotEmpty()) {
                chCh360Item = viewModel.chCh360Items[position]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chCh360Item?.let {
            // binding.chCh360 = it
            binding.chCh360Layout.chCh360 = it

            val title =
                String.format(getString(R.string.app_title),
                    getString(R.string.ch_ch_360_title))
            viewModel.setTitle(title)
            viewModel.setSubtitle(it.leg)
        }
    }
}
