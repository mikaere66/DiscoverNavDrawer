package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt

class DogParkLongListener(val longClickListener: (dogPark: DogParkKt) -> Boolean) {
    fun onLongClick(dogPark: DogParkKt) = longClickListener(dogPark)
}
