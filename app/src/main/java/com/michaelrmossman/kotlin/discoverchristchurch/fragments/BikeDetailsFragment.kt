package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentBikeDetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_POSITION_EXTRA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BikeDetailsFragment: BaseFragment<FragmentBikeDetailsBinding>(R.layout.fragment_bike_details) {

    private var bikeTrackItem: BikeTrackKt? = null

    companion object {
        fun create(position: Int) =
            BikeDetailsFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_POSITION_EXTRA, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(FRAGMENT_POSITION_EXTRA)?.let { position ->
            if (viewModel.bikeTrackItems.isNotEmpty()) {
                bikeTrackItem = viewModel.bikeTrackItems[position]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bikeTrackItem?.let { bikeTrack ->
            binding.detailsLayout.bikeTrack = bikeTrack

            val title =
                String.format(getString(R.string.app_title),
                    getString(R.string.bike_details_subtitle))
            viewModel.setTitle(title)
            viewModel.setSubtitle(bikeTrack.track)
        }
    }
}
