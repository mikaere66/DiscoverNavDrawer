package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt

class FacilityLongListener(val longClickListener: (facility: FacilityKt) -> Boolean) {
    fun onLongClick(facility: FacilityKt) = longClickListener(facility)
}
