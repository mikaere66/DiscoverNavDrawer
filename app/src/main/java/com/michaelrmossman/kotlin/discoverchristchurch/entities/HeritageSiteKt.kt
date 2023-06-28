package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class HeritageSiteKt(

    val id: Long,

    val name: String,

    val address: String,

    val angle: Int,

    val image: String?,

    val landscape: Boolean,

    val latLng: LatLng,

    val typeId: Long,

    val color: String, //

    var colorId: Int? = null, // For recycler arrow color, NOT site

    val fave: Boolean, //

    var position: Int? = null, //

    var size: Int? = null, //

    var subtitle: String? = null, //

    val type: String, //

    var zStamp: Long = 0L // Added in FavouritesFragment
)
