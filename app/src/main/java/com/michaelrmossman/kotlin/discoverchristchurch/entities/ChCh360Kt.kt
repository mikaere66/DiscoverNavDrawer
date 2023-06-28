package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class ChCh360Kt(

    val id: Long,

    val leg: String,

    val intro: String,

    val start: String,

    val finish: String,

    val description: String,

    val directions: String?,

    val latLng: LatLng,

    val trackCount: Int,

    val angle: Int,

    val color: String,

    val fave: Boolean, //

    val filename: String,

    val image: String?,

    val length: Float,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
