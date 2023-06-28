package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.CONVENIENCES_TABLE_NAME

@Entity(tableName = CONVENIENCES_TABLE_NAME) // 243 (12 changing rooms + 231 regular)
data class Convenience(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val name: String,

    @ColumnInfo
    val extra: String? = null,

    @ColumnInfo
    val hours: String? = null,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val typeId: Long
)
