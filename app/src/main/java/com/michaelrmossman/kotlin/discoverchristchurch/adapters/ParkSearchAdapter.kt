package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ParkSearchItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt

class ParkSearchAdapter(
    private val parkSearchListener: ParkSearchListener
) : ListAdapter<ParkKt, ParkSearchAdapter.ViewHolder>(ParkSearchDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), parkSearchListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: ParkSearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(parkItem: ParkKt, parkSearchListener: ParkSearchListener) {
            binding.apply {
                clickListener = parkSearchListener
                park = parkItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ParkSearchItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class ParkSearchDiffCallback: DiffUtil.ItemCallback<ParkKt>() {

    override fun areItemsTheSame(oldItem: ParkKt, newItem: ParkKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ParkKt, newItem: ParkKt): Boolean {
        return oldItem == newItem
    }
}

class ParkSearchListener(val clickListener: (park: ParkKt) -> Unit) {
    fun onClick(park: ParkKt) = clickListener(park)
}
