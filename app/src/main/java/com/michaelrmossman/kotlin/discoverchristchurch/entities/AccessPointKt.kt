package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class AccessPointKt(

    val id: Long,

    val legId: Long,

    var accessPoint: String,

    var directions: String,

    val latLng: LatLng,

    val angle: Int,

    val conveniences: String? = null
)
