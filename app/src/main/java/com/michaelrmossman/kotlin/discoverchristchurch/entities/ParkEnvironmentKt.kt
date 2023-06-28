package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class ParkEnvironmentKt(

    val id: Long,

    val parkId: Long,

    val latLng: LatLng,

    val name: String? = null // Debug
)
