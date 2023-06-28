package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.AreaItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.AreaKt

class AreasAdapter(
    private val clickListener: AreaListener
) : ListAdapter<AreaKt, AreasAdapter.ViewHolder>(AreaDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: AreaItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(areaItem: AreaKt, areaListener: AreaListener) {
            binding.apply {
                clickListener = areaListener
                area = areaItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AreaItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class AreaDiffCallback: DiffUtil.ItemCallback<AreaKt>() {
    override fun areItemsTheSame(oldItem: AreaKt, newItem: AreaKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AreaKt, newItem: AreaKt): Boolean {
        return oldItem == newItem
    }
}

class AreaListener(val clickListener: (area: AreaKt) -> Unit) {
    fun onClick(area: AreaKt) = clickListener(area)
}