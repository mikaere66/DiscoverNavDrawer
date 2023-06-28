package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class UrbanPlayKt(

    val id: Long,

    val name: String,

    val address: String,

    val cost: String,

    val description: String,

    val fave: Boolean, //

    val hours: String,

    val image: String,

    val landscape: Boolean,

    val latLng: LatLng,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
