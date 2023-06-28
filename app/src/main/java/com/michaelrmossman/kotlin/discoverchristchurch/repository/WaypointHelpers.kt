package com.michaelrmossman.kotlin.discoverchristchurch.repository

import android.graphics.Color
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MAP_TYPE_DEFAULT
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MAP_TYPE_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getAccessPointMarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getChCh360MarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getLatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getRouteMarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getWaypointMarkerOptions
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getWaypointColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getAccessPointPositionText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getChCh360PositionText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointPositionText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.base1way
import com.michaelrmossman.kotlin.discoverchristchurch.utils.baseRet

object WaypointHelpers {

    suspend fun getAPsAsWaypointsWithStartWaypoint(
        accessPoints: List<AccessPoint>, dao: DiscoverDao, itemId: Long
    ) : List<WaypointKt> {
        val accessPointsAsWaypoints = mutableListOf<WaypointKt>()
        val chCh360 = dao.getChCh360ItemById(itemId)
        val chCh360LatLng = getLatLng(chCh360.latLng)
        val directions =
            when (chCh360.directions) {
                null -> {
                    val end =
                        when (val index = chCh360.description.indexOf("\n")) {
                            -1 -> chCh360.description.length
                            else -> index
                        }
                    chCh360.description.substring(0, end)
                }
                else -> chCh360.directions
            }
        val mapType = sharedPrefs.getInt(MAP_TYPE_PREF, MAP_TYPE_DEFAULT)
        val size = accessPoints.size.plus(1)
        val startWaypoint =
            WaypointKt(
                // Negative indicates that it's a ChCh360Item, not an AccessPoint
                chCh360.id.unaryMinus(),
                chCh360.leg,
                chCh360.start,
                directions,
                0,
                chCh360LatLng,
                getAccessPointMarkerOptions(chCh360LatLng, mapType,0, size),
                chCh360.angle,
                getWaypointColorId(0, size),
                0,
                size,
                getAccessPointPositionText(1, size)
            )
        accessPointsAsWaypoints.add(startWaypoint)
        for (i in accessPoints.indices) {
            val latLng = getLatLng(accessPoints[i].latLng)
            val position = i.plus(1) // RecyclerView ScrollTo
            accessPointsAsWaypoints.add(
                WaypointKt(
                    accessPoints[i].id,
                    chCh360.leg,
                    accessPoints[i].accessPoint,
                    accessPoints[i].directions,
                    0,
                    latLng,
                    getAccessPointMarkerOptions(latLng, mapType, position, size),
                    accessPoints[i].angle,
                    getWaypointColorId(position, size),
                    position,
                    size,
                    getAccessPointPositionText(
                        position.plus(1), // Allow for start
                        size
                    )
                )
            )
        }
        return accessPointsAsWaypoints
    }

    fun getChCh360ItemsAsWaypointsKt(
        items: List<ChCh360Kt>
    ) : List<WaypointKt> {
        val chCh360ItemsAsWaypoints = mutableListOf<WaypointKt>()
        for (i in items.indices) {
            chCh360ItemsAsWaypoints.add(
                WaypointKt(
                    items[i].id,
                    items[i].leg,
                    items[i].start,
                    items[i].intro,
                    1,
                    items[i].latLng,
                    getChCh360MarkerOptions(i, items[i]),
                    items[i].angle,
                    Color.parseColor(items[i].color),
                    i,
                    items.size,
                    getChCh360PositionText(
                        i.plus(1), items.size
                    )
                )
            )
        }
        return chCh360ItemsAsWaypoints
    }

    fun getRoutesListAsWaypoints(
        routes: List<Route>
    ) : List<WaypointKt> {
        val routesListAsWaypoints = mutableListOf<WaypointKt>()
        for (i in routes.indices) {
            val intro = when (routes[i].intro) {
                null -> routes[i].start
                else -> routes[i].intro
            }
            val routeLatLng = getLatLng(routes[i].latLng)
            routesListAsWaypoints.add(
                WaypointKt(
                    routes[i].id,
                    routes[i].route,
                    routes[i].route, // Street View Title
                    intro,
                    2,
                    routeLatLng,
                    getRouteMarkerOptions(i, routes[i]),
                    routes[i].angle,
                    getWaypointColorId(i, routes.size),
                    i,
                    routes.size,
                    getWaypointPositionText(
                        i.plus(1),
                        routes[i].round,
                        routes.size
                    ),
                    routes[i].start // Street View Subtitle
                )
            )
        }
        return routesListAsWaypoints
    }

    suspend fun getWaypointsWithStartWaypoint(
        all: Boolean, dao: DiscoverDao,
        routeId: Long, waypoints: List<Waypoint>
    ) : List<WaypointKt> {
        val waypointsWithStartWaypoint = mutableListOf<WaypointKt>()
        val route = dao.getRouteById(routeId)
        val routeLatLng = getLatLng(route.latLng)
        val directions =
            when (route.directions) {
                null -> {
                    val end =
                        when (val index = route.description.indexOf("\n")) {
                            -1 -> route.description.length
                            else -> index
                        }
                    route.description.substring(0, end)
                }
                else -> route.directions
            }
        val size =
            when (route.round) {
                in base1way..baseRet -> 2
                else -> waypoints.size.plus(1) // Allow for startWaypoint
            }
        if (all) { // Skip start waypoint when NOT FIRST crater rim
            val startWaypoint =
                WaypointKt(
                    // Negative indicates that it's a Route, not Waypoint
                    route.id.unaryMinus(),
                    route.route,
                    route.start,
                    directions,
                    3,
                    routeLatLng,
                    getWaypointMarkerOptions(
                        0, routeLatLng, route.round, size, route.shared
                    ),
                    route.angle,
                    getWaypointColorId(0, size),
                    0,
                    size,
                    getWaypointSubtitleText(1, size)
                )
            waypointsWithStartWaypoint.add(startWaypoint)
        }
        for (i in waypoints.indices) {
            val latLng = getLatLng(waypoints[i].latLng)
            val position =  // RecyclerView ScrollTo
                when (route.round) {
                    in base1way..baseRet -> 1
                    else -> i.plus(1)
                }
            val index = // Optional Markers in Basic
                when (route.round) {
                    in base1way..baseRet -> position.plus(1)
                    else -> position
                }
            waypointsWithStartWaypoint.add(
                WaypointKt(
                    waypoints[i].id,
                    route.route,
                    waypoints[i].waypoint,
                    waypoints[i].directions,
                    3,
                    latLng,
                    getWaypointMarkerOptions(
                        index, latLng, route.round, size, route.shared
                    ),
                    waypoints[i].angle,
                    getWaypointColorId(position, size),
                    position,
                    size,
                    getWaypointSubtitleText(
                        position.plus(1), size // Allow for start
                    )
                )
            )
        }
        return waypointsWithStartWaypoint
    }
}
