package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.BikeTracksAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentBikeTracksBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BikeTracksFragment: BaseFragment<FragmentBikeTracksBinding>(R.layout.fragment_bike_tracks) {

    override fun onDestroy() {
        super.onDestroy()

        viewModel.setOverflowIconIndex(0)
    }

    override fun onResume() {
        super.onResume() // Tested: Required
        val title = String.format(
            getString(R.string.app_title),
            getString(R.string.bike_tracks)
        )
        viewModel.setTitle(title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BikeTracksAdapter(
            BikeTrackListener { bikeTrack, checkBox, index ->
                when (index) {
                    0 ->  goDetails(bikeTrack)
                    else -> toggleFave(checkBox.isChecked, bikeTrack.id, ITEM_VIEW_TYPE_ITEM_8)
                }
            },
            BikeTrackLongListener { bikeTrack ->
                goBikeTrack(bikeTrack)
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.apply {
            bikeTracksFragment = this@BikeTracksFragment
            recyclerView.adapter = adapter
            setFabsExtra(mapsMenuFab, backToTopFab, recyclerView)
        }

        val index = getOverflowIconIndex(false)
        viewModel.setOverflowIconIndex(index.plus(200))
        setSortMenu(viewLifecycleOwner,false)

        viewModel.bikeTracks.observe(viewLifecycleOwner) { bikeTracks ->
            adapter.addFooterAndSubmitList(bikeTracks)

            setSubtitle(bikeTracks.size)
        }
    }

    fun backToTop() {
        scrollToPosition(binding.recyclerView)
    }

    private fun goBikeTrack(bikeTrack: BikeTrackKt) {
        goBikeTrackSingle(bikeTrack,209602091) // BikeTracksActivity
    }

    fun goBikeTracks() {
        viewModel.setCurrentBikeTrackId(0L)
        navigateTo(209602091)
    }

    private fun goDetails(bikeTrack: BikeTrackKt) {
        goBikeTrackSingle(bikeTrack,209602041) // BikeDetailsActivity
    }

    private fun setSubtitle(count: Int) {
        val subtitle =
            String.format(getString(R.string.bike_tracks_fragment_subtitle),
                count)
        viewModel.setSubtitle(subtitle)
    }
}
