package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt

class BikeTrackLongListener(val longClickListener: (bikeTrack: BikeTrackKt) -> Boolean) {
    fun onLongClick(bikeTrack: BikeTrackKt) = longClickListener(bikeTrack)
}
