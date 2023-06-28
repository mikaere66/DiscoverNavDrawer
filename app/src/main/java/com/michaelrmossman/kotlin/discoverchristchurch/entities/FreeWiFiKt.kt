package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class FreeWiFiKt(

    val id: Long,

    val name: String,

    val extra: String?,

    val fave: Boolean, //

    val latLng: LatLng,

    var zStamp: Long = 0L // Added in FavouritesFragment
)