package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "facility_types_table") // 13
data class FacilityType(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val type: String,

    @ColumnInfo
    val color: String,

    @ColumnInfo
    var selected: Boolean
)
