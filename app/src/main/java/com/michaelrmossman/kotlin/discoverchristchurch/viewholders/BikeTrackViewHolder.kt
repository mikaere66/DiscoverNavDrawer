package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.BikeTrackItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackLongListener

class BikeTrackViewHolder private constructor(
    private val binding: BikeTrackItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        bikeTrackItem: BikeTrackKt,
        bikeTrackListener: BikeTrackListener,
        bikeTrackLongListener: BikeTrackLongListener
    ) {
        binding.apply {
            checkBox = bikeTrackFavourite
            bikeTrack = bikeTrackItem
            clickListener = bikeTrackListener
            longClickListener = bikeTrackLongListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): BikeTrackViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                BikeTrackItemBinding.inflate(layoutInflater, parent,false)
            return BikeTrackViewHolder(binding)
        }
    }
}
