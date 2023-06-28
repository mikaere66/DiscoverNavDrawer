package com.michaelrmossman.kotlin.discoverchristchurch.entities

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

data class WaypointKt(

    val id: Long,

    val route: String,

    val waypoint: String,

    var directions: String? = null,

    /* 0 = AccessPointsAsWaypoint
       1 = ChCh360ItemAsWaypoint
       2 = RouteAsWaypoint
       3 = actual Waypoint */
    val itemType: Int,

    val latLng: LatLng,

    val markerOptions: MarkerOptions,

    val angle: Int,

    val colorId: Int,

    var position: Int,

    val size: Int,

    var subtitle: String,

    /* Street View subtitle
       in RouteAsWaypoint */
    var extra: Any? = null
)
