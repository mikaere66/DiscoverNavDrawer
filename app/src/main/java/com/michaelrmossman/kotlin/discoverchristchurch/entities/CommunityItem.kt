package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "community_items_table") // 12
data class CommunityItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val count: Int,

    @ColumnInfo
    val drawableIndex: Int,

    @ColumnInfo
    val helpText: String
)
