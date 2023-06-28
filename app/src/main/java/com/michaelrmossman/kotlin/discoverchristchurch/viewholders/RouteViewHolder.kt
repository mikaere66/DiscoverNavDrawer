package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.RouteItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteFaveListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteLongListener

class RouteViewHolder private constructor(
    private val binding: RouteItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        routeItem: RouteKt, routeFaveListener: RouteFaveListener,
        routeListener: RouteListener, routeLongListener: RouteLongListener
    ) {
        binding.apply {
            checkBox = routeFavourite
            clickListener = routeListener
            faveListener = routeFaveListener
            longClickListener = routeLongListener
//            nearestVisible = routeItem.nearest != defaultNearest // -0.0
            route = routeItem
            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): RouteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = RouteItemBinding.inflate(layoutInflater, parent,false)
            return RouteViewHolder(binding)
        }
    }
}
