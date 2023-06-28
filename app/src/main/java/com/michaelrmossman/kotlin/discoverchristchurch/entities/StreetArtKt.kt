package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class StreetArtKt(

    val id: Long,

    val latLng: LatLng,

    val extra: String?,

    val image: String,

    val landscape: Boolean,

    val title: String,

    val artistIds: String?,

    val description: String?,

    var colorId: Int? = null, // For recycler arrow color

    val credit: String?,

    val date: String?,

    val fave: Boolean, //

    var position: Int? = null, //

    var size: Int? = null, //

    var subtitle: String? = null, //

    val viewableId: Int,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
