package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt

class ChCh360LongListener(val longClickListener: (chCh360: ChCh360Kt) -> Boolean) {
    fun onLongClick(chCh360: ChCh360Kt) = longClickListener(chCh360)
}
