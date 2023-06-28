package com.michaelrmossman.kotlin.discoverchristchurch.entities

data class FruitTypeKt(

    val id: Long,

    val catId: Long,

    val category: String, //

    val color: String, //

    val count: Int, //

    val fave: Boolean, //

    val type: String,

    var zStamp: Long = 0L // Added in FavouritesFragment
)
