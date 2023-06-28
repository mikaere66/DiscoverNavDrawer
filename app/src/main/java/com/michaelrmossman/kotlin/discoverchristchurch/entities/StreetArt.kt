package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.STREET_ART_TABLE_NAME

@Entity(tableName = STREET_ART_TABLE_NAME) // 543
data class StreetArt(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    val id: Long = 0L,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val extra: String? = null,

    @ColumnInfo
    val image: String,

    @ColumnInfo
    val landscape: Boolean,

    @ColumnInfo
    val title: String,

    @ColumnInfo
    val description: String? = null,

    @ColumnInfo
    val credit: String? = null,

    @ColumnInfo
    val date: String? = null,

    @ColumnInfo // (name = STREET_ARTIST_IDS_COLUMN)
    val artistIds: String? = null,

//    @Ignore
//    var artistList: HashMap<Long, String>? = null,

    @ColumnInfo
    val viewableId: Int
)
