package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFavouritesMessagePair
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import kotlinx.coroutines.launch

open class InfoBaseFragment<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : BottomSheetDialogFragment() {

    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private var bottomSheetDialog: BottomSheetDialog? = null
    val viewModel: DiscoverViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding =
            DataBindingUtil.inflate(inflater, layoutResId, container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Reset state in viewModel, ready for next instance
        viewModel.setBottomSheetState(BottomSheetBehavior.SAVE_NONE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val state = bottomSheetBehavior.state
        outState.putInt(FRAGMENT_INFO_EXTRA, state)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        bottomSheetDialog = dialog as? BottomSheetDialog
        bottomSheetBehavior =
            bottomSheetDialog?.behavior as BottomSheetBehavior<FrameLayout>
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
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val state: Int =
            savedInstanceState?.getInt(FRAGMENT_INFO_EXTRA)
                ?: BottomSheetBehavior.SAVE_NONE // Note elvis op
        if (state == BottomSheetBehavior.STATE_EXPANDED) {
            expandCollapseDialog(false)
        }

        updateExpandButtonState(state)
    }

    fun expandCollapseDialog(collapse: Boolean) { // Called from xml
        if (collapse) dismiss()
        else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    // Called from InfoBikeTrack | InfoDetail fragments
    fun toggleFave(checked: Boolean, itemId: Long, itemType: Int, reInit: Boolean) {
        lifecycleScope.launch {
            val result = viewModel.toggleFavourite(checked, itemId, itemType, reInit)
            val messagePair = getFavouritesMessagePair(result)
            fancyToast(requireContext(), messagePair.first, messagePair.second)
        }
    }

    fun updateExpandButtonState(state: Int) {
        viewModel.setBottomSheetState(state)
    }
}
