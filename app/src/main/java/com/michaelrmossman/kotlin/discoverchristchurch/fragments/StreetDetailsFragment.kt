package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentStreetDetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_POSITION_EXTRA
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreetDetailsFragment: BaseFragment<FragmentStreetDetailsBinding>(R.layout.fragment_street_details) {

    private var streetArtItem: StreetArtKt? = null

    companion object {
        fun create(position: Int) =
            StreetDetailsFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_POSITION_EXTRA, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(FRAGMENT_POSITION_EXTRA)?.let { position ->
            if (viewModel.streetArtItems.isNotEmpty()) {
                streetArtItem = viewModel.streetArtItems[position]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        streetArtItem?.let { streetArt ->
            binding.detailsLayout.streetArt = streetArt

            val title =
                String.format(getString(R.string.app_title),
                    getString(R.string.street_art_fragment_title))
            viewModel.setTitle(title)
            viewModel.setSubtitle(streetArt.title)

//            streetArt.artistList?.let { artists ->
//                val sb = StringBuilder()
//                for (artist in artists.values) {
//                    when (artists.size) {
//                        1 -> sb.append(artist)
//                        else -> {
//                            sb.append(
//                                String.format(
//                                    getString(R.string.bullet_point),
//                                    artist
//                                )
//                            )
//                        }
//                    }
//                }
//
//                val quantityString = resources.getQuantityString(
//                    R.plurals.street_artists,
//                    artists.size
//                )
//
//                binding.apply {
//                    streetDetailsArtistsIcon.isVisible = true
//                    streetDetailsArtistsText.apply {
//                        isVisible = true
//                        text = String.format(
//                            quantityString,
//                            sb.toString()
//                        )
//                    }
//                }
//            }
        }
    }
}

