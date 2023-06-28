package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FreeWiFiItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FreeWiFiLongListener

class FreeWiFiViewHolder private constructor(
    private val binding: FreeWiFiItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        freeWiFiItem: FreeWiFiKt,
        freeWiFiListener: BasicClickListener,
        freeWiFiLongListener: FreeWiFiLongListener
    ) {
        binding.apply {
            checkBox = freeWiFiFavourite
            clickListener = freeWiFiListener
            longClickListener = freeWiFiLongListener
            freeWiFi = freeWiFiItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): FreeWiFiViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                FreeWiFiItemBinding.inflate(layoutInflater, parent,false)
            return FreeWiFiViewHolder(binding)
        }
    }
}
