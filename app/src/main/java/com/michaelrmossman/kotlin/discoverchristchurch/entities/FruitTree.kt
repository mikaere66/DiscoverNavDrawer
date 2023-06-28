package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fruit_trees_table") // 5837
data class FruitTree(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val categoryId: Long,

    @ColumnInfo
    val typeId: Long,

    @ColumnInfo
    val latLng: String
)
