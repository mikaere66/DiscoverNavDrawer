package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.defaultNearest

// Used to find true centre of either polygons or polylines
data class PolyPoint(

    val itemId: Long,

    val latLng: LatLng,

    var nearest: Double? = defaultNearest // -0.0
)
