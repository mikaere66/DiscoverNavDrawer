package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoRouteBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_01

// Called from RoutesActivity. Shows minimal info re individual routes
class InfoRouteFragment: InfoBaseFragment<FragmentInfoRouteBinding>(R.layout.fragment_info_route) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            // if (viewModel.currentRoute.intro != null) { infoBarrier.addView(infoIntroText) }
            checkBox = infoFavourite
            discoverViewModel = viewModel
            infoFragment = this@InfoRouteFragment
        }

        // This forces the BottomSheet to appear at max height, since it's so small
        expandCollapseDialog(false)
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }

    fun toggleFavourite(checkBox: CheckBox, itemId: Long) {
        toggleFave( // Called from xml
            checkBox.isChecked, itemId,
            ITEM_VIEW_TYPE_ITEM_01,true
        )
    }
}
