package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME

@Entity(tableName = FAVOURITES_TABLE_NAME)
data class Favourite(

    @PrimaryKey
    @ColumnInfo
    val id: Long,

    @ColumnInfo
    val itemId: Long,

    @ColumnInfo
    val itemType: Int,

    @ColumnInfo
    val fave: Boolean
)
