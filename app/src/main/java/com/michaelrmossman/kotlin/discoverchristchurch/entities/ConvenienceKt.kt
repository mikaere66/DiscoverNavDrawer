package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class ConvenienceKt(

    val id: Long,

    val name: String,

    val extra: String?,

    val fave: Boolean, //

    val hours: String?,

    val latLng: LatLng,

    val typeId: Long,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
