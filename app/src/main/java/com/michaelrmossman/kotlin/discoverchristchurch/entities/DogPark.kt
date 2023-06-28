package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_parks_table") // 165
data class DogPark(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val dogPark: String,

    @ColumnInfo
    val extra: String? = null,

    @ColumnInfo
    val typeId: Long,

    @ColumnInfo
    val environmentIds: String,

    @ColumnInfo
    val holeIds: String? = null,

    @ColumnInfo
    val angle: Int,

    @ColumnInfo
    val details: String? = null,

    /* CC info regarding SOME dog parks, from
       their "Areas to exercise your dog" page */
    @ColumnInfo
    val dogFacilities: String? = null,

    @ColumnInfo
    val dogInfo: String? = null,

    @ColumnInfo
    val dogNote: String? = null,

    @ColumnInfo
    val latLng: String? = null,

    /* ids of ROUTES mentioned;
       separated by commas */
    @ColumnInfo
    val linkedIds: String? = null,

    @ColumnInfo
    val startPoint: String? = null
)
