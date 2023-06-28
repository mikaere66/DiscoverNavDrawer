package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heritage_types_table") // 8
data class HeritageType(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    var color: String,

    @ColumnInfo
    var type: String,

    @ColumnInfo
    var selected: Boolean
)
