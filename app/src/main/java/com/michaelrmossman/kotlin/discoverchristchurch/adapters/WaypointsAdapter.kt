package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.WaypointItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isXLargeScreen

class WaypointsAdapter(
    private val clickListener: WaypointListener,
    private val longClickListener: WaypointLongListener
) : ListAdapter<WaypointKt, WaypointsAdapter.ViewHolder>(WaypointDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener, longClickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        private val binding: WaypointItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            waypointItem: WaypointKt,
            waypointListener: WaypointListener,
            waypointLongListener: WaypointLongListener
        ) {
            binding.apply {
                clickListener = waypointListener
                descriptionVisible = isXLargeScreen()
                longClickListener = waypointLongListener
                waypoint = waypointItem
                executePendingBindings()
            }
        }

        companion object {
            fun from(
                parent: ViewGroup
            ) : ViewHolder {
                val layoutInflater =
                    LayoutInflater.from(parent.context)
                val binding =
                    WaypointItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }
    }
}

class WaypointDiffCallback: DiffUtil.ItemCallback<WaypointKt>() {
    override fun areItemsTheSame(oldItem: WaypointKt, newItem: WaypointKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WaypointKt, newItem: WaypointKt): Boolean {
        return oldItem == newItem
    }
}

class WaypointListener(val clickListener: (index: Int, waypoint: WaypointKt) -> Unit) {
    fun onClick(index: Int, waypoint: WaypointKt) = clickListener(index, waypoint)
}

class WaypointLongListener(val longClickListener: (index: Int, waypoint: WaypointKt) -> Boolean) {
    fun onLongClick(index: Int, waypoint: WaypointKt) = longClickListener(index, waypoint)
}
