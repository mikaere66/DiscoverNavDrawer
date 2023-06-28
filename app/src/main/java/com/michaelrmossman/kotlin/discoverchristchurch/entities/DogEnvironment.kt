package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dog_environments_table") // 37,949
data class DogEnvironment(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val dogParkId: Long,

    @ColumnInfo
    val latLng: String
)
