package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tree_categories_table") // 21
data class FruitCat(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val category: String,

    @ColumnInfo
    val color: String,

    @ColumnInfo
    val months: String,

    @ColumnInfo
    var selected: Boolean
)
