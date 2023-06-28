package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.CONVENIENCE_TYPES_TABLE_NAME

@Entity(tableName = CONVENIENCE_TYPES_TABLE_NAME) // 2
data class ConvenienceType(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    var selected: Boolean
)
