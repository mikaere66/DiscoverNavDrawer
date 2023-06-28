package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class FacilityKt(

    val id: Long,

    val facility: String,

    val extra: String?,

    val latLng: LatLng,

    val typeId: Long,

    val url: String?,

    val color: String, //

    val fave: Boolean, //

    val type: String, //

    var zStamp: Long = 0L // Added in FavouritesFragment
)
