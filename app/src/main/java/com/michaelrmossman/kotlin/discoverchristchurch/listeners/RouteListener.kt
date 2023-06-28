package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt

class RouteListener(val clickListener: (route: RouteKt) -> Unit) {
    fun onClick(route: RouteKt) = clickListener(route)
}
