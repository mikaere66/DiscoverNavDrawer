package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt

class StreetArtLongListener(val longClickListener: (streetArt: StreetArtKt) -> Boolean) {
    fun onLongClick(streetArt: StreetArtKt) = longClickListener(streetArt)
}
