package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt

class FountainLongListener(val longClickListener: (fountain: DrinkFountainKt) -> Boolean) {
    fun onLongClick(fountain: DrinkFountainKt) = longClickListener(fountain)
}
