package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "access_points_table")
data class AccessPoint(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val legId: Long,

    @ColumnInfo
    var accessPoint: String,

    @ColumnInfo
    var directions: String,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val angle: Int,

    @ColumnInfo
    val conveniences: String? = null
)
