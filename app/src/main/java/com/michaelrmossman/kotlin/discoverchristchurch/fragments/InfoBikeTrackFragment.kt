package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoBikeTrackBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8

// Called from BikeTracksActivity. Shows most BikeTrack info
class InfoBikeTrackFragment: InfoBaseFragment<FragmentInfoBikeTrackBinding>(R.layout.fragment_info_bike_track) {

    private lateinit var bikeTrackItem: BikeTrackKt

    override fun onStart() {
        super.onStart()

        if (isLandscape()) {
            // This forces the BottomSheet to appear at max height in landscape
            expandCollapseDialog(false)
            updateExpandButtonState(BottomSheetBehavior.STATE_EXPANDED)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bikeTrackItem = viewModel.bikeTrackItem
        val imageId =
            getImageIdentifier(R.array.bike_track_images, bikeTrackItem.id)
        GlideApp.with(this).load(imageId).into(binding.infoImage)
        val headerTextColor = ContextCompat.getColor(requireContext(),
            when (bikeTrackItem.textColor) {
                0 -> R.color.black
                else -> R.color.white
            }
        )

        binding.apply {
            bikeTrack = bikeTrackItem
            checkBox = infoFavourite
            infoFragment = this@InfoBikeTrackFragment
            infoImage.setOnClickListener { goImage() }

            infoTitleText.setTextColor(headerTextColor)
            infoSubtitleText.setTextColor(headerTextColor)

            detailsLayout.bikeTrack = bikeTrackItem
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) { state ->
            binding.isCollapsed = state != BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun goImage() {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.bike_track_images, bikeTrackItem.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, bikeTrackItem.track, bikeTrackItem.landscape
            )
        }
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_8,true
        )
    }
}
