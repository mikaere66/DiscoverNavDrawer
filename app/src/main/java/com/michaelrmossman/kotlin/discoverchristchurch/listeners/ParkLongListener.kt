package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt

class ParkLongListener(val longClickListener: (park: ParkKt) -> Boolean) {
    fun onLongClick(park: ParkKt) = longClickListener(park)
}
