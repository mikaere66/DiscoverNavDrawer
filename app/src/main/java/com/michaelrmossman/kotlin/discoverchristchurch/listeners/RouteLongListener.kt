package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt

class RouteLongListener(val longClickListener: (route: RouteKt) -> Boolean) {
    fun onLongClick(route: RouteKt) = longClickListener(route)
}

