package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt

class UrbanPlayListener(val clickListener: (checkBox: CheckBox, index: Int, urbanPlay: UrbanPlayKt) -> Unit) {
    fun onClick(checkBox: CheckBox, index: Int, urbanPlay: UrbanPlayKt) = clickListener(checkBox, index, urbanPlay)
}
