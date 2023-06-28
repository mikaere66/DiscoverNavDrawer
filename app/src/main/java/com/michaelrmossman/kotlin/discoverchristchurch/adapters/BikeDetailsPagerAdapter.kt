package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.BikeDetailsFragment

class BikeDetailsPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var count: Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return BikeDetailsFragment.create(position)
    }

    override fun getItemCount(): Int = count
}
