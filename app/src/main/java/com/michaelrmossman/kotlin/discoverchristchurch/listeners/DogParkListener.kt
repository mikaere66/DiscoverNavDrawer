package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt

class DogParkListener(val clickListener: (checkBox: CheckBox, dogPark: DogParkKt, index: Int) -> Unit) {
    fun onClick(checkBox: CheckBox, dogPark: DogParkKt, index: Int) = clickListener(checkBox, dogPark, index)
}
