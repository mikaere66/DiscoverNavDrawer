package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.CommunityAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.CommunityListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentCommunityBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.entities.CommunityItem
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FIRST_LAUNCH_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SHOW_FOOTER_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleBoldMulti
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment: BaseFragment<FragmentCommunityBinding>(R.layout.fragment_community) {

    private lateinit var adapter: CommunityAdapter

    override fun onResume() {
        super.onResume() // Required
        viewModel.setTitle(getString(R.string.app_name))
        viewModel.setSubtitle(getString(R.string.community_fragment_subtitle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (sharedPrefs.getBoolean(FIRST_LAUNCH_PREF,true) &&
                sharedPrefs.edit().putBoolean(FIRST_LAUNCH_PREF, false).commit()) {
            // In xml, viewGroup is GONE by default
            binding.emptyViewGroup.isVisible = true
        }

        adapter = CommunityAdapter(
            CommunityListener { communityItem ->
                goCommunityItem(communityItem)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            recyclerView.adapter = adapter

            if (sharedPrefs.getBoolean(SHOW_FOOTER_PREF,true)) {
                footerLayout.apply {
                    communityFragment = this@CommunityFragment
                    itemConstraintLayout.isVisible = true
                    val string1b = getString(R.string.walks_city)
                    val stringId1a = R.string.walks_intro_1
                    val string2b = getString(R.string.walks_other)
                    val stringId2a = R.string.walks_intro_2
                    walksText.text = getSpannableStyleBoldMulti(
                        string1b, stringId1a, string2b, stringId2a
                    )
                }
            }
        }

        viewModel.communityItems.observe(viewLifecycleOwner) { items ->
            binding.emptyViewGroup.isVisible = false
            adapter.submitList(items)
        }

        viewModel.resetFlippedViews.observe(viewLifecycleOwner) { reset ->
            if (reset) {
                adapter.resetViews(true)
                viewModel.setResetFlippedViews(false)
            }
        }
    }

    fun confirmHideFooter(): Boolean { // Called from xml
        activity?.let { fragmentActivity ->
            val alertDialogBuilder = AlertDialog.Builder(fragmentActivity)
            alertDialogBuilder.apply {
                setIcon(R.drawable.ic_outline_hide_image_black_24)
                setTitle(R.string.footer_dialog_title)
                setMessage(R.string.footer_dialog_message)
                setNegativeButton(android.R.string.cancel,null)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    hideFooter()
                }
            }
            alertDialogBuilder.create()
            alertDialog = alertDialogBuilder.show()
            return true
        }
        return false
    }

    private fun goCommunityItem(communityItem: CommunityItem) {
        navigateTo(
            when (communityItem.id) {
                1L -> 315602016 // BatteryRecyclersFragment
                2L -> 315606016 // (Community) FacilitiesFragment
                3L -> 315604146 // DogParksFragment
                4L -> 315606156 // (Drink) FountainsFragment
                5L -> 315606236 // FreeWiFiFragment
                6L -> 315606186 // FruitTreesFragment
                7L -> 315608056 // HeritageSitesFragment
                8L -> 315602046 // (MTB) BikeTracksFragment
                9L -> 315616036 // (Public) ConveniencesFragment
                10L -> 315616016 // (Public) ParksFragment
                11L -> 315619016 // StreetArtFragment
                else -> 315621186 // UrbanPlayFragment
            }
        )
    }

    private fun hideFooter() {
        if (sharedPrefs.edit().putBoolean(SHOW_FOOTER_PREF, false).commit()) {
            binding.footerLayout.itemConstraintLayout.isVisible = false
        }
    }
}
