package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt

class HeritageSiteLongListener(val longClickListener: (heritageSite: HeritageSiteKt) -> Boolean) {
    fun onLongClick(heritageSite: HeritageSiteKt) = longClickListener(heritageSite)
}
