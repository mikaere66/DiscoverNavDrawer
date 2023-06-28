package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt

class RouteFaveListener(val clickListener: (checkBox: CheckBox, route: RouteKt) -> Unit) {
    fun onClick(checkBox: CheckBox, route: RouteKt) = clickListener(checkBox, route)
}
