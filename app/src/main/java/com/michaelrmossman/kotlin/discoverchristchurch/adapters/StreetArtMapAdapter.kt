package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.StreetMapItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt

class StreetArtMapAdapter(
    private val streetArtListener: StreetArtMapListener,
    private val streetArtLongListener: StreetArtMapLongListener
) : ListAdapter<StreetArtKt, StreetArtMapAdapter.ViewHolder>(StreetArtKtDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), streetArtListener, streetArtLongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: StreetMapItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: StreetArtKt,
            streetArtListener: StreetArtMapListener,
            streetArtLongListener: StreetArtMapLongListener
        ) {
            binding.apply {
                streetArt = item
                clickListener = streetArtListener
                longClickListener = streetArtLongListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = StreetMapItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class StreetArtKtDiffCallback: DiffUtil.ItemCallback<StreetArtKt>() {
    override fun areItemsTheSame(oldItem: StreetArtKt, newItem: StreetArtKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StreetArtKt, newItem: StreetArtKt): Boolean {
        return oldItem == newItem
    }
}

class StreetArtMapListener(val clickListener: (streetArt: StreetArtKt, index: Int) -> Unit) {
    fun onClick(streetArt: StreetArtKt, index: Int) = clickListener(streetArt, index)
}

class StreetArtMapLongListener(val longClickListener: (index: Int) -> Boolean) {
    fun onLongClick(index: Int) = longClickListener(index)
}
