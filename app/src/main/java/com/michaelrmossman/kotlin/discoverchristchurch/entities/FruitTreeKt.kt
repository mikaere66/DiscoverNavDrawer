package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class FruitTreeKt(

    val id: Long,

    val category: String,

    val color: String,

    val latLng: LatLng,

    val type: String
)
