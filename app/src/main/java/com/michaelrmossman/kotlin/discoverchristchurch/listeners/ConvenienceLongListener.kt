package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt

class ConvenienceLongListener(val longClickListener: (convenience: ConvenienceKt) -> Boolean) {
    fun onLongClick(convenience: ConvenienceKt) = longClickListener(convenience)
}
