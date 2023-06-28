package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentChCh360ImageBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_CH_CH_360_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier

class ChCh360ImageFragment: BottomSheetDialogFragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var bottomSheetDialog: BottomSheetDialog? = null
    private var _binding: FragmentChCh360ImageBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun create(index: Int) =
            ChCh360ImageFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_CH_CH_360_EXTRA, index)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding =
            FragmentChCh360ImageBinding.inflate(
                inflater, container,false
            )

        bottomSheetDialog = dialog as? BottomSheetDialog
        bottomSheetBehavior =
            bottomSheetDialog?.behavior as BottomSheetBehavior<FrameLayout>
        bottomSheetBehavior.isDraggable = false // New
        bottomSheetBehavior.addBottomSheetCallback(object:
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when (state) {
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    /* Only update icon state if bottomSheet is in either the
                       expanded (fully-open) or collapsed (half-open) state */
                    else -> return
                }

                updateExpandButtonState(state)
            }
        })

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            imageFragment = this@ChCh360ImageFragment
        }

        val index =
            arguments?.getInt(FRAGMENT_CH_CH_360_EXTRA)
                ?: throw IllegalStateException()

        val stringId =
            when (index) {
                0 -> R.string.ch_ch_360_wheel_descr
                else -> R.string.ch_ch_360_overview_descr
            }
        binding.infoHeaderText.text = getString(stringId)

        val imageId =
            getImageIdentifier(R.array.ch_ch_360_popups, index.toLong())
        GlideApp.with(this).load(imageId).into(binding.fullscreenImage)

        // This forces the BottomSheet to appear at max height
        expandCollapseDialog(false)

        updateExpandButtonState(bottomSheetBehavior.state)
    }


    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val state: Int =
            savedInstanceState?.getInt(FRAGMENT_INFO_EXTRA)
                ?: BottomSheetBehavior.SAVE_NONE // Note elvis op
        updateExpandButtonState(state)
    }

    fun expandCollapseDialog(collapse: Boolean) { // Called from xml
        if (collapse) dismiss()
        else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun updateExpandButtonState(state: Int) {
        binding.isCollapsed =
            state != BottomSheetBehavior.STATE_EXPANDED
    }
}
