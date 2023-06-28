package com.michaelrmossman.kotlin.discoverchristchurch.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.michaelrmossman.kotlin.discoverchristchurch.database.*

@Entity(tableName = ROUTES_TABLE_NAME)
data class Route(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ROUTE_ID)
    val id: Long = 0L,

    @ColumnInfo
    val areaId: Long,

    @ColumnInfo(name = PLACE_ID)
    val placeId: Long,

    @ColumnInfo(name = ROUTE_DB)
    val route: String,

    @ColumnInfo
    val intro: String? = null,

    @ColumnInfo
    val warning: Int,

    @ColumnInfo
    val start: String,

    @ColumnInfo
    val finish: String,

    @ColumnInfo
    val directions: String? = null,

    @ColumnInfo
    val distance: Int,

    @ColumnInfo
    val time: Int,

    @ColumnInfo(name = ROUND_DB)
    val round: Int,

    @ColumnInfo(name = ACCESSIBLE_DB)
    val accessible: Int,

    @ColumnInfo(name = DOGS_DB)
    val dogs: Int,

    @ColumnInfo(name = SHARED_DB)
    val shared: Int,

    @ColumnInfo(name = TRANSPORT_DB)
    val transport: Boolean,

    @ColumnInfo
    val description: String,

    @ColumnInfo
    val latLng: String,

    @ColumnInfo
    val conveniences: String? = null,

    @ColumnInfo
    val dogsExtra: Int,

    @ColumnInfo
    val angle: Int,

//    @ColumnInfo
//    var fave: Boolean,

    @ColumnInfo(name = IMAGE_DB)
    val image: String? = null,

    /* ids of OTHER routes mentioned in
       description; separated by commas */
    @ColumnInfo
    val linkedIds: String? = null,

    /* Colors for BottomSheet expand/collapse icons
       that appear over images relating to each of
       the above; separated by commas. 0 = black,
       1 = white. Can be null, then all are white */
    @ColumnInfo
    val linkedColor: Int? = null
)
