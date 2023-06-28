package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.PARK_TYPES_TABLE_NAME

@Entity(tableName = PARK_TYPES_TABLE_NAME)
data class ParkType(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val type: String,

    @ColumnInfo
    val border: String,

    @ColumnInfo
    val color: String,

    @ColumnInfo
    var selected: Boolean
)
