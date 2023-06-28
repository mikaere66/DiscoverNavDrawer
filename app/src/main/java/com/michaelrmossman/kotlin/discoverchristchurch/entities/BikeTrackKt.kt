package com.michaelrmossman.kotlin.discoverchristchurch.entities

data class BikeTrackKt(

    val id: Long,

    val track: String,

    val gain: Int,

    val grade: Int,

    val gradient: Double,

    val highest: Int,

    val landscape: Boolean,

    val length: Double,

    val lowest: Int,

    val max: Double,

    val notes: String? = null,

    val shared: Int,

    var colorId: Int? = null, // For recycler arrow color, NOT track

    val fave: Boolean, //

    var position: Int? = null, //

    var size: Int? = null, //

    var subtitle: String? = null, //

    val textColor: Int,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
