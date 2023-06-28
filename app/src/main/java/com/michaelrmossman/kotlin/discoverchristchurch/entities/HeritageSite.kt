package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heritage_sites_table") // 60
data class HeritageSite(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val address: String,

    @ColumnInfo
    val angle: Int,

    @ColumnInfo
    val image: String? = null,

    @ColumnInfo
    val landscape: Boolean,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val typeId: Long,

    @ColumnInfo
    val url: String
)
