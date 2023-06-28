package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt

class UrbanPlayLongListener(val longClickListener: (urbanPlay: UrbanPlayKt) -> Boolean) {
    fun onLongClick(urbanPlay: UrbanPlayKt) = longClickListener(urbanPlay)
}
