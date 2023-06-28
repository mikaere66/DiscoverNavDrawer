package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.defaultNearest

data class DogParkKt(

    val id: Long = 0L,

    val dogPark: String,

    val extra: String? = null,

    val typeId: Long,

    val environmentIds: String,

    val holeIds: String? = null,

    val details: String? = null,

    val angle: Int, // Note different order from DogPark entity

    val border: String,

    val color: String,

    var colorId: Int? = null, // For recycler arrow color, NOT track

    val dogFacilities: String? = null,

    val dogInfo: String? = null,

    val dogNote: String? = null,

    val fave: Boolean, //

    val latLng: LatLng? = null,

    val linkedIds: String? = null, //

    var position: Int? = null, //

    var size: Int? = null, //

    var subtitle: String? = null, //

    val startPoint: String? = null,

    var zStamp: Long = 0L// Added in FavouritesFragment
)
