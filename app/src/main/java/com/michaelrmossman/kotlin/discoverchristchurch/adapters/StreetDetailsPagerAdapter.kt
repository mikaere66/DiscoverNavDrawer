package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.StreetDetailsFragment

class StreetDetailsPagerAdapter(
    fragmentActivity: FragmentActivity,
    private var count: Int
) : FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return StreetDetailsFragment.create(position)
    }

    override fun getItemCount(): Int = count
}
