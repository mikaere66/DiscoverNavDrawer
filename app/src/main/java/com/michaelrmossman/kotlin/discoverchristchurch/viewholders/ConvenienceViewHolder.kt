package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ConvenienceItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ConvenienceLongListener

class ConvenienceViewHolder private constructor(
    private val binding: ConvenienceItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        convenienceItem: ConvenienceKt,
        convenienceListener: BasicClickListener,
        convenienceLongListener: ConvenienceLongListener
    ) {
        binding.apply {
            checkBox = convenienceFavourite
            clickListener = convenienceListener
            longClickListener = convenienceLongListener
            convenience = convenienceItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ConvenienceViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                ConvenienceItemBinding.inflate(layoutInflater, parent,false)
            return ConvenienceViewHolder(binding)
        }
    }
}
