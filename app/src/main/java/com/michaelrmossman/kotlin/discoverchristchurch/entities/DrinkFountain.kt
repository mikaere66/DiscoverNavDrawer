package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.FOUNTAINS_TABLE_NAME

@Entity(tableName = FOUNTAINS_TABLE_NAME) // 203 (178+16+3+6) ... 115 have NO features
data class DrinkFountain(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val extra: String? = null,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val alt: Boolean, // Toilet block/outside tap, etc.

    @ColumnInfo
    val bottles: Boolean,

    @ColumnInfo
    val convenience: Boolean,

    @ColumnInfo
    val dogs: Boolean
)
