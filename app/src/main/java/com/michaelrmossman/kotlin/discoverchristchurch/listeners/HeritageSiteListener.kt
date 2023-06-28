package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import android.widget.CheckBox
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt

class HeritageSiteListener(val clickListener: (checkBox: CheckBox, heritageSite: HeritageSiteKt, index: Int) -> Unit) {
    fun onClick(checkBox: CheckBox, heritageSite: HeritageSiteKt, index: Int) = clickListener(checkBox, heritageSite, index)
}
