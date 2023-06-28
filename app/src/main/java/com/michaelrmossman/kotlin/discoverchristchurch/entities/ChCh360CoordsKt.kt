package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class ChCh360CoordsKt(

    val id: Long,

    val legId: Long,

    val trackId: Int,

    val latLng: LatLng
)
