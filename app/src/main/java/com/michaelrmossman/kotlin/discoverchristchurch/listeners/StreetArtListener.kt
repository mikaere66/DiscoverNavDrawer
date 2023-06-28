package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt

class StreetArtListener(val clickListener: (checkBox: CheckBox, index: Int, streetArt: StreetArtKt) -> Unit) {
    fun onClick(checkBox: CheckBox, index: Int, streetArt: StreetArtKt) = clickListener(checkBox, index, streetArt)
}
