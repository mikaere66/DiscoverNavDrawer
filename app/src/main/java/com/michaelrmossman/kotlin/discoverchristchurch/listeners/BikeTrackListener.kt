package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt

class BikeTrackListener(val clickListener: (bikeTrack: BikeTrackKt, checkBox: CheckBox, index: Int) -> Unit) {
    fun onClick(bikeTrack: BikeTrackKt, checkBox: CheckBox, index: Int) = clickListener(bikeTrack, checkBox, index)
}
