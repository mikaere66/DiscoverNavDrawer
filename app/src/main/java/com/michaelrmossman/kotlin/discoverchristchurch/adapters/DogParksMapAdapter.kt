package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.DogMapItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt

class DogParksMapAdapter(
    private val dogParkListener: DogParkMapListener,
    private val dogParkLongListener: DogParkMapLongListener
) : ListAdapter<DogParkKt, DogParksMapAdapter.ViewHolder>(DogParkKtDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), dogParkListener, dogParkLongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: DogMapItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: DogParkKt,
            dogParkListener: DogParkMapListener,
            dogParkLongListener: DogParkMapLongListener
        ) {
            binding.apply {
                dogPark = item
                clickListener = dogParkListener
                longClickListener = dogParkLongListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DogMapItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class DogParkKtDiffCallback: DiffUtil.ItemCallback<DogParkKt>() {
    override fun areItemsTheSame(oldItem: DogParkKt, newItem: DogParkKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DogParkKt, newItem: DogParkKt): Boolean {
        return oldItem == newItem
    }
}

class DogParkMapListener(val clickListener: (dogPark: DogParkKt, index: Int) -> Unit) {
    fun onClick(dogPark: DogParkKt, index: Int) = clickListener(dogPark, index)
}

class DogParkMapLongListener(val longClickListener: (index: Int) -> Boolean) {
    fun onLongClick(index: Int) = longClickListener(index)
}
