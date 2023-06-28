package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "waypoints_table")
data class Waypoint(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val routeId: Long,

    @ColumnInfo
    var waypoint: String,

    @ColumnInfo
    var directions: String,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val angle: Int
)
