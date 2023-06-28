package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.PlaceItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PlaceKt

class PlacesAdapter(
    private val clickListener: PlaceListener
) : ListAdapter<PlaceKt, PlacesAdapter.ViewHolder>(PlaceDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: PlaceItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(placeItem: PlaceKt, placeListener: PlaceListener) {
            binding.apply {
                clickListener = placeListener
                place = placeItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlaceItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class PlaceDiffCallback: DiffUtil.ItemCallback<PlaceKt>() {
    override fun areItemsTheSame(oldItem: PlaceKt, newItem: PlaceKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceKt, newItem: PlaceKt): Boolean {
        return oldItem == newItem
    }
}

class PlaceListener(val clickListener: (place: PlaceKt) -> Unit) {
    fun onClick(place: PlaceKt) = clickListener(place)
}
