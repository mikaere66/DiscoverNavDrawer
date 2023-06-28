package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.DogParkItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkLongListener

class DogParkViewHolder private constructor(
    private val binding: DogParkItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        dogParkItem: DogParkKt,
        dogParkListener: DogParkListener,
        dogParkLongListener: DogParkLongListener
    ) {
        binding.apply {
            checkBox = dogParkFavourite
            clickListener = dogParkListener
            longClickListener = dogParkLongListener
            /* Only the ONE of EITHER the dogInfo OR
            linkedRoutes icons will show at a time */
            dogInfo = dogParkItem.dogInfo != null
            dogPark = dogParkItem
            linkedRoute = dogParkItem.linkedIds != null
            linkedRoutes =
                when (dogParkItem.dogInfo) {
                    null -> {
                        when (dogParkItem.linkedIds) {
                            null -> false
                            else -> {
                                val linkedIds: List<String> =
                                    dogParkItem.linkedIds.split(",")
                                linkedIds.size > 1
                            }
                        }
                    }
                    else -> false
                }
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): DogParkViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                DogParkItemBinding.inflate(layoutInflater, parent,false)
            return DogParkViewHolder(binding)
        }
    }
}
