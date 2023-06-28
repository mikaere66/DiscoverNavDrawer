package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import androidx.recyclerview.widget.DiffUtil
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt

class RouteDiffCallback : DiffUtil.ItemCallback<RouteKt>() {

    override fun areItemsTheSame(oldItem: RouteKt, newItem: RouteKt): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RouteKt, newItem: RouteKt): Boolean {
        return oldItem == newItem
    }
}
