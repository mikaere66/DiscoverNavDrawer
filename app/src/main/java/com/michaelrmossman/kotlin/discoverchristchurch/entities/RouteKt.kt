package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng

data class RouteKt(

    val id: Long,

    val areaId: Long,

    val placeId: Long,

    val route: String,

    val intro: String?,

    val warning: Int,

    val start: String,

    val finish: String,

    val distance: Int,

    val time: Int,

    val round: Int,

    val accessible: Int,

    val dogs: Int,

    val shared: Int,

    val transport: Boolean,

    val description: String,

    val latLng: LatLng,

    val conveniences: String?,

    val dogsExtra: Int,

    val angle: Int,

    val fave: Boolean,

    val linkedColor: Int? = null,

    val linkedIds: String? = null,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
