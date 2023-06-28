package com.michaelrmossman.kotlin.discoverchristchurch.entities

data class AreaKt(

    val id: Long,

    val area: String,

    val places: Int,

    val routes: Int,

    val distance: Int, // Sorting

    val time: Int, // Sorting

    val accessible: Int,

    val dogs: Int,

    val linked: Int,

    val shared: Int,

    val transport: Int
)
