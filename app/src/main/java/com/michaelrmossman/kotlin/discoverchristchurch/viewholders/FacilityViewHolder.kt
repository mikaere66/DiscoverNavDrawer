package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FacilityItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FacilityLongListener

class FacilityViewHolder private constructor(
    private val binding: FacilityItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        facilityItem: FacilityKt,
        facilityListener: BasicClickListener,
        facilityLongListener: FacilityLongListener
    ) {
        binding.apply {
            checkBox = facilityFavourite
            clickListener = facilityListener
            longClickListener = facilityLongListener
            facility = facilityItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): FacilityViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                FacilityItemBinding.inflate(layoutInflater, parent,false)
            return FacilityViewHolder(binding)
        }
    }
}
