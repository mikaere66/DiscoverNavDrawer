package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class DrinkFountainKt(

    val id: Long,

    val name: String,

    val extra: String?,

    val latLng: LatLng,

    val alt: Boolean, // Toilet block/outside tap, etc.

    val bottles: Boolean,

    val convenience: Boolean,

    val dogs: Boolean,

    val fave: Boolean, //

    var zStamp: Long = 0L // Added in FavouritesFragment
)
