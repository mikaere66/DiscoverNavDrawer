package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox

class BasicClickListener(val clickListener: (checkBox: CheckBox, itemId: Long) -> Unit) {
    fun onClick(checkBox: CheckBox, itemId: Long) = clickListener(checkBox, itemId)
}
