package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class Coords( // Only used for Street View

    val title: String = String(),

    val subtitle: String = String(),

    val latLng: LatLng = LatLng(0.0, 0.0),

    var angle: Int = 0,

    val count: String = String()
)