package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_types_table") // 9 (only 7 used, as "Dog Exercise Area" and "Dog Park" are combined into "Dog parks and exercise areas")
data class DogType(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val bylaw: String, // TODO?

    @ColumnInfo
    val border: String,

    @ColumnInfo
    val color: String,

    @ColumnInfo
    var selected: Boolean
)
