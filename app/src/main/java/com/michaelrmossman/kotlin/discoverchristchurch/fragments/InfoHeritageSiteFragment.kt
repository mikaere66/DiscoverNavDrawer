package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoHeritageSiteBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7

// Called from HeritageSitesActivity. Shows minimal info re individual heritage sites
class InfoHeritageSiteFragment: InfoBaseFragment<FragmentInfoHeritageSiteBinding>(R.layout.fragment_info_heritage_site) {

    private lateinit var heritageSiteItem: HeritageSiteKt

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

        heritageSiteItem = viewModel.currentHeritageSite
        val imageId =
            getImageIdentifier(R.array.heritage_site_images, heritageSiteItem.id)
        GlideApp.with(this).load(imageId).into(binding.infoImage)

        binding.apply {
            checkBox = infoFavourite
            discoverViewModel = viewModel
            infoFragment = this@InfoHeritageSiteFragment
            infoImage.setOnClickListener { goImage() }
        }
    }

    private fun goImage() {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.heritage_site_images, heritageSiteItem.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, heritageSiteItem.name, heritageSiteItem.landscape
            )
        }
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_7,true
        )
    }
}
