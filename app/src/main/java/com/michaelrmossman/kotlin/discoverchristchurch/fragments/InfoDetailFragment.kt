package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoDetailBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_02
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape

// Called from DetailedFragment, just for linked routes
class InfoDetailFragment: InfoBaseFragment<FragmentInfoDetailBinding>(R.layout.fragment_info_detail) {

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

        viewModel.linkedRoute.observe(viewLifecycleOwner) { route ->
            binding.apply {
                checkBox = infoFavourite
                discoverViewModel = viewModel
                infoFragment = this@InfoDetailFragment
                infoImage.setOnClickListener { goImage() }

                val imageId =
                    getImageIdentifier(R.array.route_images, route.id)
                GlideApp.with(this@InfoDetailFragment).load(imageId).into(infoImage)

                val expandDrawableId =
                    when (route.linkedColor) {
                        0 -> R.drawable.ic_baseline_expand_more_black_32
                        else -> R.drawable.ic_baseline_expand_more_white_32 // 1 or null
                    }
                infoExpandButton.setImageResource(expandDrawableId)

                val collapseDrawableIdId =
                    when (route.linkedColor) {
                        0 -> R.drawable.ic_baseline_expand_less_black_32
                        else -> R.drawable.ic_baseline_expand_less_white_32 // 1 or null
                    }
                infoCollapseButton.setImageResource(collapseDrawableIdId)
            }
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) { state ->
            binding.isCollapsed = state != BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun goImage() {
        activity?.let { fragmentActivity ->
            val route = viewModel.linkedRoute.value
            route?.let {
                val imageId =
                    getImageIdentifier(R.array.route_images, it.id)
                goFullscreen(
                    fragmentActivity as AppCompatActivity,
                    imageId, it.route
                )
            }
        }
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        /* Note different itemType for linkedRoute,
           although both still treated as Route */
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_02,false
        )
    }
}
