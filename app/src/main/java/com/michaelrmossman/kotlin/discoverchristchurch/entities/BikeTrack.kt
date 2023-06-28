package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bike_tracks_table") // 55
data class BikeTrack(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val track: String,

    @ColumnInfo
    val gain: Int,

    @ColumnInfo
    val grade: Int,

    @ColumnInfo
    val gradient: Double,

    @ColumnInfo
    val highest: Int,

    @ColumnInfo
    val landscape: Boolean,

    @ColumnInfo
    val length: Double,

    @ColumnInfo
    val lowest: Int,

    @ColumnInfo
    val max: Double,

    @ColumnInfo
    val notes: String? = null,

    @ColumnInfo
    val shared: Int,

    @ColumnInfo
    val textColor: Int
)
