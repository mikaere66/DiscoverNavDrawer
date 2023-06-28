package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ch_ch_360_table")
data class ChCh360(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val leg: String,

    @ColumnInfo
    val intro: String,

    @ColumnInfo
    val start: String,

    @ColumnInfo
    val finish: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val directions: String? = null,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val trackCount: Int,

    @ColumnInfo
    val angle: Int,

    @ColumnInfo
    val color: String,

    @ColumnInfo
    val filename: String,

    @ColumnInfo
    val image: String? = null,

    @ColumnInfo
    val length: Float
)
