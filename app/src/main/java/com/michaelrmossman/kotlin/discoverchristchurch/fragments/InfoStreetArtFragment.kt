package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoStreetArtBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11

// Called from StreetArtActivity. Shows most StreetArt info
class InfoStreetArtFragment: InfoBaseFragment<FragmentInfoStreetArtBinding>(R.layout.fragment_info_street_art) {

    private lateinit var streetArtItem: StreetArtKt

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

        streetArtItem = viewModel.streetArtItem
        val imageId =
            getImageIdentifier(R.array.street_art_images, streetArtItem.id)
        GlideApp.with(this).load(imageId).into(binding.infoImage)

        binding.apply {
            streetArt = streetArtItem
            checkBox = infoFavourite
            detailsLayout.streetArt = streetArtItem
            infoFragment = this@InfoStreetArtFragment
            infoImage.setOnClickListener { goImage() }
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) { state ->
            binding.isCollapsed = state != BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun goImage() {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.street_art_images, streetArtItem.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, streetArtItem.title, streetArtItem.landscape
            )
        }
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_11,true
        )
    }
}
