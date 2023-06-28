package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FruitTypeItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FruitTypeLongListener

class FruitTypeViewHolder private constructor(
    private val binding: FruitTypeItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        fruitTypeItem: FruitTypeKt,
        fruitTypeListener: BasicClickListener,
        fruitTypeLongListener: FruitTypeLongListener
    ) {
        binding.apply {
            checkBox = fruitTypeFavourite
            clickListener = fruitTypeListener
            longClickListener = fruitTypeLongListener
            fruitType = fruitTypeItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): FruitTypeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                FruitTypeItemBinding.inflate(layoutInflater, parent,false)
            return FruitTypeViewHolder(binding)
        }
    }
}
