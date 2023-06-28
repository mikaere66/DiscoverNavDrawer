package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.defaultNearest

data class DogEnvironmentKt(

    val id: Long,

    val dogParkId: Long,

    val latLng: LatLng,

    var nearest: Double? = defaultNearest // -0.0
)
