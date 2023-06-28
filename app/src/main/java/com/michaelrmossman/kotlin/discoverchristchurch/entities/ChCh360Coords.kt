package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ch_ch_360_coords_table")
data class ChCh360Coords(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val legId: Long,

    @ColumnInfo
    val trackId: Int,

    @ColumnInfo
    val latLng: String
)
