package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt

class ChCh360Listener(val clickListener: (chCh360: ChCh360Kt, checkBox: CheckBox, index: Int) -> Unit) {
    fun onClick(chCh360: ChCh360Kt, checkBox: CheckBox, index: Int) = clickListener(chCh360, checkBox, index)
}
