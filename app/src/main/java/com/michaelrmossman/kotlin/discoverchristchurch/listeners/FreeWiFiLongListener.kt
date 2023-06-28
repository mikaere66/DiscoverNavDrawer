package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt

class FreeWiFiLongListener(val longClickListener: (freeWiFi: FreeWiFiKt) -> Boolean) {
    fun onLongClick(freeWiFi: FreeWiFiKt) = longClickListener(freeWiFi)
}
