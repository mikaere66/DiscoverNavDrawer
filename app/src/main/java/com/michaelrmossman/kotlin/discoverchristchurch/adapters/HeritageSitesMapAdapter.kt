package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.HeritageMapItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt

class HeritageSitesMapAdapter(
    private val heritageSiteListener: HeritageSiteMapListener,
    private val heritageSiteLongListener: HeritageSiteMapLongListener
) : ListAdapter<HeritageSiteKt, HeritageSitesMapAdapter.ViewHolder>(HeritageSiteKtDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), heritageSiteListener, heritageSiteLongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: HeritageMapItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: HeritageSiteKt,
            heritageSiteListener: HeritageSiteMapListener,
            heritageSiteLongListener: HeritageSiteMapLongListener
        ) {
            binding.apply {
                heritageSite = item
                clickListener = heritageSiteListener
                longClickListener = heritageSiteLongListener
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HeritageMapItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class HeritageSiteKtDiffCallback: DiffUtil.ItemCallback<HeritageSiteKt>() {
    override fun areItemsTheSame(oldItem: HeritageSiteKt, newItem: HeritageSiteKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: HeritageSiteKt, newItem: HeritageSiteKt): Boolean {
        return oldItem == newItem
    }
}

class HeritageSiteMapListener(val clickListener: (heritageSite: HeritageSiteKt, index: Int) -> Unit) {
    fun onClick(heritageSite: HeritageSiteKt, index: Int) = clickListener(heritageSite, index)
}

class HeritageSiteMapLongListener(val longClickListener: (index: Int) -> Boolean) {
    fun onLongClick(index: Int) = longClickListener(index)
}
