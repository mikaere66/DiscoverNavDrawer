package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facilities_table") // 675 (number 214 is Unknown)
data class Facility(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val facility: String,

    @ColumnInfo
    val extra: String? = null,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val typeId: Long,

    @ColumnInfo
    val url: String? = null
)
