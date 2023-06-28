package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class ParkKt(

    val id: Long,

    val name: String,

    val typeId: Long,

    val area: Int,

    val perimeter: Int,

    val environmentIds: String,

    val latLng: LatLng,

    val border: String,

    val color: String,

    val fave: Boolean, //

    val type: String, //

    var zStamp: Long = 0L // Added in FavouritesFragment
)
