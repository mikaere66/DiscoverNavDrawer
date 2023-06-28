package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.PARKS_TABLE_NAME

@Entity(tableName = PARKS_TABLE_NAME) // 1323
data class Park(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val typeId: Long,

    @ColumnInfo
    val area: Int,

    @ColumnInfo
    val perimeter: Int,

    @ColumnInfo
    val environmentIds: String,

    @ColumnInfo
    val latLng: String
)
