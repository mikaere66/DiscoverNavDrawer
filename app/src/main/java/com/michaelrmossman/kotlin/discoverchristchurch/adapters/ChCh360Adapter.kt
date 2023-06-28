package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360Listener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360LongListener
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.ChCh360ViewHolder

class ChCh360Adapter(
    private val chCh360Listener: ChCh360Listener,
    private val chCh360LongListener: ChCh360LongListener
) : ListAdapter<ChCh360Kt, ChCh360ViewHolder>(ChCh360DiffCallback()) {

    override fun onBindViewHolder(holder: ChCh360ViewHolder, position: Int) {
        holder.bind(getItem(position), chCh360Listener, chCh360LongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChCh360ViewHolder {
        return ChCh360ViewHolder.from(parent)
    }
}

class ChCh360DiffCallback: DiffUtil.ItemCallback<ChCh360Kt>() {
    override fun areItemsTheSame(oldItem: ChCh360Kt, newItem: ChCh360Kt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ChCh360Kt, newItem: ChCh360Kt): Boolean {
        return oldItem == newItem
    }
}
