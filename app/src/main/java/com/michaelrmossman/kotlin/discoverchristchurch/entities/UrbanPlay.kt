package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "urban_play_table") // 10
data class UrbanPlay(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val address: String,

    @ColumnInfo
    val cost: String,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val hours: String,

    @ColumnInfo
    val image: String,

    @ColumnInfo
    val landscape: Boolean,

    @ColumnInfo
    val latLng: String
)
