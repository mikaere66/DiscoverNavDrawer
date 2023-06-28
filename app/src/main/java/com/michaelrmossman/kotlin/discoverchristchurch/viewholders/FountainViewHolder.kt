package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FountainItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FountainLongListener

class FountainViewHolder private constructor(
    private val binding: FountainItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        fountainItem: DrinkFountainKt,
        fountainListener: BasicClickListener,
        fountainLongListener: FountainLongListener
    ) {
        binding.apply {
            checkBox = fountainFavourite
            clickListener = fountainListener
            longClickListener = fountainLongListener
            fountain = fountainItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): FountainViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                FountainItemBinding.inflate(layoutInflater, parent,false)
            return FountainViewHolder(binding)
        }
    }
}
