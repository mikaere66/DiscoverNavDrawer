package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class BatteryRecyclerKt(

    val id: Long,

    val name: String,

    val address: String,

    val hours: String,

    val latLng: LatLng,

    val local: Boolean,

    val typeId: Long,

    val color: String, //

    val fave: Boolean, //

    val type: String, //

    var zStamp: Long = 0L // Added in FavouritesFragment
)
