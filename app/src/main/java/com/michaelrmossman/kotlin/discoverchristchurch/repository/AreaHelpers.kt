package com.michaelrmossman.kotlin.discoverchristchurch.repository

import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Area
import com.michaelrmossman.kotlin.discoverchristchurch.entities.AreaKt
import com.michaelrmossman.kotlin.discoverchristchurch.enums.SortOrder

object AreaHelpers {

    suspend fun getAreasWithPlacesAndRoutes(
        areas: List<Area>, dao: DiscoverDao, sortBy: Int, sortOrder: SortOrder
    ) : List<AreaKt> {
        val areasWithPlacesAndRoutes = mutableListOf<AreaKt>()
        areas.forEach { area ->
            val places = dao.getPlacesCountByAreaId(area.id)
            val routes = dao.getRoutesCountByAreaId(area.id)
            val accessibility =
                when (dao.getAccessibleCountByAreaId(area.id) > 0) {
                    true -> 2
                    else -> {
                        when (dao.getBuggyCountByAreaId(area.id) > 0) {
                            true -> 1
                            else -> 0
                        }
                    }
                }
            val dogsOk = dao.getDogsOkCountByAreaId(area.id)
            val dogsOnLeash = dao.getDogsOnLeashCountByAreaId(area.id) > 0
            val dogs =
                when (dogsOk) {
                    routes -> 2 // All okay
                    else -> {
                        when (dogsOnLeash) {
                            true -> 1 // Some leash
                            else -> 0
                        }
                    }
                }
            val linked =
                when (dao.getLinkedRoutesCountByAreaId(area.id)) {
                    routes -> 2 // All linked
                    0 -> 0 // None linked
                    else -> 1
                }
            val shared =
                when (dao.getSharedUseCountByAreaId(area.id)) {
                    routes -> 2 // All shared
                    0 -> 0 // None shared
                    else -> 1
                }
            val transport =
                when (dao.getTransportReqdCountByAreaId(area.id)) {
                    routes -> 2 // All have pub transport
                    0 -> 0 // None have pub transport
                    else -> 1
                }
            val distance =
                when (routes) {
                    0 -> Int.MAX_VALUE
                    else -> dao.getRoutesSumDistanceByAreaId(area.id)
                }
            val time =
                when (routes) {
                    0 -> Int.MAX_VALUE
                    else -> dao.getRoutesSumTimeByAreaId(area.id)
                }
            areasWithPlacesAndRoutes.add (
                AreaKt(
                    area.id,
                    area.area,
                    places,
                    routes,
                    distance,
                    time,
                    accessibility,
                    dogs,
                    linked,
                    shared,
                    transport
                )
            )
        }
        when (sortBy) {
            0 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortBy { it.area }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.area }
                    }
                }
            }
            1 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.accessible }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortBy { it.accessible }
                    }
                }
            }
            2 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.dogs }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortBy { it.dogs }
                    }
                }
            }
            3 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.shared }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortBy { it.shared }
                    }
                }
            }
            4 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.transport }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortBy { it.transport }
                    }
                }
            }
            5 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortBy { it.distance }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.distance }
                    }
                }
            }
            else -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        areasWithPlacesAndRoutes.sortBy { it.time }
                    }
                    else -> {
                        areasWithPlacesAndRoutes.sortByDescending { it.time }
                    }
                }
            }
        }
        return areasWithPlacesAndRoutes
    }
}
