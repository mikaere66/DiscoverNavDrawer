package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ParkItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ParkLongListener

class ParkViewHolder private constructor(
    private val binding: ParkItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        parkItem: ParkKt,
        parkListener: BasicClickListener,
        parkLongListener: ParkLongListener
    ) {
        binding.apply {
            checkBox = parkFavourite
            clickListener = parkListener
            longClickListener = parkLongListener
            park = parkItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ParkViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ParkItemBinding.inflate(layoutInflater, parent,false)
            return ParkViewHolder(binding)
        }
    }
}
