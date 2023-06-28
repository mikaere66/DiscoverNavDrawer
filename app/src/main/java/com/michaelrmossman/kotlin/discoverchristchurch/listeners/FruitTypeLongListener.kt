package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt

class FruitTypeLongListener(val longClickListener: (fruitType: FruitTypeKt) -> Boolean) {
    fun onLongClick(fruitType: FruitTypeKt) = longClickListener(fruitType)
}
