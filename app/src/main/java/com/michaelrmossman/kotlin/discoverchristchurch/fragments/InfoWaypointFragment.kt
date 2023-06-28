package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentInfoWaypointBinding

// Called from BaseActivity. Shows minimal info re individual waypoints
class InfoWaypointFragment: InfoBaseFragment<FragmentInfoWaypointBinding>(R.layout.fragment_info_waypoint) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            discoverViewModel = viewModel
            infoFragment = this@InfoWaypointFragment
        }

        // This forces the BottomSheet to appear at max height, since it's so small
        expandCollapseDialog(false)
    }

    fun showStreetView() {
        viewModel.setShowStreetView(true)
    }
}
