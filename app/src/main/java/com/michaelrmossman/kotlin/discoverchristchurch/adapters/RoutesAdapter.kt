package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteFaveListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.RouteViewHolder

class RoutesAdapter(
    private val routeFaveListener: RouteFaveListener,
    private val routeListener: RouteListener,
    private val routeLongListener: RouteLongListener
) : ListAdapter<RouteKt, RouteViewHolder>(RouteDiffCallback()) {

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position), routeFaveListener, routeListener, routeLongListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder.from(parent)
    }

//    class ViewHolder private constructor(
//        private val binding: RouteItemBinding
//    ) : RecyclerView.ViewHolder(binding.root) {

//        fun bind(routeItem: RouteKt, routeListener: RouteListener, routeLongListener: RouteLongListener) {
//            binding.apply {
//                clickListener = routeListener
//                longClickListener = routeLongListener
//                nearestVisible = routeItem.nearest != defaultNearest // -0.0
//                route = routeItem
//                executePendingBindings()
//            }
//        }

//        companion object {
//            fun from(parent: ViewGroup): ViewHolder {
//                val layoutInflater = LayoutInflater.from(parent.context)
//                val binding = RouteItemBinding.inflate(layoutInflater, parent,false)
//                return ViewHolder(binding)
//            }
//        }
//    }
}

