package com.michaelrmossman.kotlin.discoverchristchurch.entities

data class PlaceKt(

    val id: Long = 0L,

    val area: String,

    val place: String,

    val routes: Int,

    val distance: Int, // Sorting

    val time: Int, // Sorting

    val accessibilityColor: Int,
    val accessibilityIcon: Int,
    val accessible: Int, // Sorting

    val dogs: Int,

    val linked: Int,

    val shared: Int,

    val transport: Int
)
