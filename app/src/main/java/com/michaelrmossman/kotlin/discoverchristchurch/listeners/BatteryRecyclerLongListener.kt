package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt

class BatteryRecyclerLongListener(val longClickListener: (batteryRecycler: BatteryRecyclerKt) -> Boolean) {
    fun onLongClick(batteryRecycler: BatteryRecyclerKt) = longClickListener(batteryRecycler)
}
