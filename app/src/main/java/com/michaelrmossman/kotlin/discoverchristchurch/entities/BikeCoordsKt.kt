package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class BikeCoordsKt(

    val id: Long = 0L,

    val trackId: Long,

    val latLng: LatLng
)
