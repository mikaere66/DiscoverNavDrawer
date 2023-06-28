package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_recyclers_table") // 17
data class BatteryRecycler(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val address: String,

    @ColumnInfo
    val hours: String,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val local: Boolean,

    @ColumnInfo
    val typeId: Long
)
