package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "park_environments_table") // 108,310
data class ParkEnvironment(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val parkId: Long,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val name: String? = null // Debug
)
