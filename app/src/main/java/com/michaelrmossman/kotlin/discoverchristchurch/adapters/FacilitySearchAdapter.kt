package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FacilitySearchItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt

class FacilitySearchAdapter(
    private val facilitySearchListener: FacilitySearchListener
) : ListAdapter<FacilityKt, FacilitySearchAdapter.ViewHolder>(FacilitySearchDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), facilitySearchListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: FacilitySearchItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(facilityItem: FacilityKt, facilitySearchListener: FacilitySearchListener) {
            binding.apply {
                clickListener = facilitySearchListener
                facility = facilityItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FacilitySearchItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class FacilitySearchDiffCallback: DiffUtil.ItemCallback<FacilityKt>() {

    override fun areItemsTheSame(oldItem: FacilityKt, newItem: FacilityKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FacilityKt, newItem: FacilityKt): Boolean {
        return oldItem == newItem
    }
}

class FacilitySearchListener(val clickListener: (facility: FacilityKt) -> Unit) {
    fun onClick(facility: FacilityKt) = clickListener(facility)
}
