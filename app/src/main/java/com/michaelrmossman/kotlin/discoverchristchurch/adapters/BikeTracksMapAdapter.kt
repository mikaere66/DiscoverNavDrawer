package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.BikeMapItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt

class BikeTracksMapAdapter(
    private val bikeTrackListener: BikeTrackMapListener,
    private val bikeTrackLongListener: BikeTrackMapLongListener
) : ListAdapter<BikeTrackKt, BikeTracksMapAdapter.ViewHolder>(BikeTrackKtDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), bikeTrackListener, bikeTrackLongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: BikeMapItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: BikeTrackKt,
            bikeTrackListener: BikeTrackMapListener,
            bikeTrackLongListener: BikeTrackMapLongListener
        ) {
            binding.apply {
                bikeTrack = item
                clickListener = bikeTrackListener
                longClickListener = bikeTrackLongListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = BikeMapItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class BikeTrackKtDiffCallback: DiffUtil.ItemCallback<BikeTrackKt>() {
    override fun areItemsTheSame(oldItem: BikeTrackKt, newItem: BikeTrackKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BikeTrackKt, newItem: BikeTrackKt): Boolean {
        return oldItem == newItem
    }
}

class BikeTrackMapListener(val clickListener: (bikeTrack: BikeTrackKt, index: Int) -> Unit) {
    fun onClick(bikeTrack: BikeTrackKt, index: Int) = clickListener(bikeTrack, index)
}

class BikeTrackMapLongListener(val longClickListener: (index: Int) -> Boolean) {
    fun onLongClick(index: Int) = longClickListener(index)
}
