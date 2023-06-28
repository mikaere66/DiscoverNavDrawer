package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.DogParkSearchItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt

class DogParkSearchAdapter(
    private val dogParkSearchListener: DogParkSearchListener
) : ListAdapter<DogParkKt, DogParkSearchAdapter.ViewHolder>(DogParkSearchDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), dogParkSearchListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: DogParkSearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(dogParkItem: DogParkKt, dogParkSearchListener: DogParkSearchListener) {
            binding.apply {
                clickListener = dogParkSearchListener
                dogPark = dogParkItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DogParkSearchItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class DogParkSearchDiffCallback: DiffUtil.ItemCallback<DogParkKt>() {

    override fun areItemsTheSame(oldItem: DogParkKt, newItem: DogParkKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DogParkKt, newItem: DogParkKt): Boolean {
        return oldItem == newItem
    }
}

class DogParkSearchListener(val clickListener: (dogPark: DogParkKt) -> Unit) {
    fun onClick(dogPark: DogParkKt) = clickListener(dogPark)
}
