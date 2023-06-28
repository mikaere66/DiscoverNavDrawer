package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoChCh361Binding

// Called from ChCh361Activity. Shows minimal info re individual waypoints
class InfoChCh361Fragment: InfoBaseFragment<FragmentInfoChCh361Binding>(R.layout.fragment_info_ch_ch_361) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            discoverViewModel = viewModel
            infoFragment = this@InfoChCh361Fragment
        }

        // This forces the BottomSheet to appear at max height, since it's so small
        expandCollapseDialog(false)
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }
}
