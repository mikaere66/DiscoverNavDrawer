package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.BatteryRecyclerItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BatteryRecyclerLongListener

class BatteryRecyclerViewHolder private constructor(
    private val binding: BatteryRecyclerItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        batteryRecyclerItem: BatteryRecyclerKt,
        batteryRecyclerListener: BasicClickListener,
        batteryRecyclerLongListener: BatteryRecyclerLongListener
    ) {
        binding.apply {
            batteryRecycler = batteryRecyclerItem
            checkBox = batteryRecyclerFavourite
            clickListener = batteryRecyclerListener
            longClickListener = batteryRecyclerLongListener
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): BatteryRecyclerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                BatteryRecyclerItemBinding.inflate(layoutInflater, parent,false)
            return BatteryRecyclerViewHolder(binding)
        }
    }
}
