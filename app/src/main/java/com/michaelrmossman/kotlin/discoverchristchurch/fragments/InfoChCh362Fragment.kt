package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoChCh362Binding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape

// Only called from ChCh362Activity. Shows most ChCh360 leg info
class InfoChCh362Fragment: InfoBaseFragment<FragmentInfoChCh362Binding>(R.layout.fragment_info_ch_ch_362) {

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

        binding.apply {
            discoverViewModel = viewModel
            infoFragment = this@InfoChCh362Fragment
        }

        viewModel.bottomSheetState.observe(viewLifecycleOwner) { state ->
            binding.isCollapsed = state != BottomSheetBehavior.STATE_EXPANDED
        }
    }

    fun processOnClick() {
        activity?.let {
            toggleDescription(
                it,
                binding.infoToggleGroup,
                binding.infoDescriptionToggleButton
            )
        }
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }

    private fun toggleDescription(
        context: Context, group: Group, toggleButton: View
    ) {
        toggleButton.startAnimation(
            when (group.isVisible) {
                false -> { // Was at END of function
                    binding.infoDescriptionHeader.text =
                        getString(R.string.ch_ch_360_description_full)
                    binding.infoDescriptionMoreLess.text =
                        getString(R.string.ch_ch_360_less)
                    expandCollapseDialog(false)
                    updateExpandButtonState(BottomSheetBehavior.STATE_EXPANDED)
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_clockwise
                    )
                }
                else -> {
                    binding.infoDescriptionHeader.text =
                        getString(R.string.ch_ch_360_description_leg)
                    binding.infoDescriptionMoreLess.text =
                        getString(R.string.ch_ch_360_more)
                    updateExpandButtonState(BottomSheetBehavior.STATE_COLLAPSED)
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_anticlockwise
                    )
                }
            }
        )

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.BOTTOM
        slideIn.duration =
            when (group.isVisible) {
                true -> resources.getInteger(
                    R.integer.toggle_close_anim_duration
                ).toLong()
                else -> resources.getInteger(
                    R.integer.toggle_open_anim_duration
                ).toLong()
            }

        slideIn.mode =
            when (group.isVisible) {
                true -> Slide.MODE_OUT
                else -> Slide.MODE_IN
            }

        slideIn.addTarget(binding.infoDescriptionToggleText)

        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup, slideIn
        )

        // Must use "with"
        with (group) {
            visibility =
                if (isVisible) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }
    }
}
