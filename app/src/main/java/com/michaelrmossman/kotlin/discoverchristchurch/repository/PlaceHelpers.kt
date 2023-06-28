package com.michaelrmossman.kotlin.discoverchristchurch.repository

import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Place
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PlaceKt
import com.michaelrmossman.kotlin.discoverchristchurch.enums.SortOrder

object PlaceHelpers {

    suspend fun getPlacesWithRoutes(
        dao: DiscoverDao, places: List<Place>, sortBy: Int, sortOrder: SortOrder
    ) : List<PlaceKt> {
        val placesWithRoutes = mutableListOf<PlaceKt>()
        places.forEach { place ->
            val area = dao.getAreaById(place.areaId)
            val routes = dao.getPlacesCountByPlaceId(place.id)
            val accessible = dao.getAccessibleCountByPlaceId(place.id)
            val accessibilityColor: Int
            val accessibilityIcon: Int
            val accessibility = // Sorting
                when (accessible > 0) {
                    true -> {
                        accessibilityColor =
                            when (accessible) {
                                routes -> 3 // All accessible
                                else -> 2 // Some accessible
                            }
                        accessibilityIcon =
                            when (accessible) {
                                routes -> 3 // As
                                else -> 2 // above.
                            }
                        2
                    }
                    else -> {
                        when (dao.getBuggyCountByPlaceId(place.id) > 0) {
                            true -> {
                                accessibilityColor = 1 // Buggies
                                accessibilityIcon = 1 // only.
                                1
                            }
                            else -> {
                                accessibilityColor = 0
                                accessibilityIcon = 0
                                0
                            }
                        }
                    }
                }
            val dogsOk = dao.getDogsOkCountByPlaceId(place.id)
            val dogsOnLeash = dao.getDogsOnLeashCountByPlaceId(place.id) > 0
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
                when (dao.getLinkedRoutesCountByPlaceId(place.id)) {
                    routes -> 2 // All linked
                    0 -> 0 // None linked
                    else -> 1
                }
            val shared =
                when (dao.getSharedUseCountByPlaceId(place.id)) {
                    routes -> 2 // All shared
                    0 -> 0 // None shared
                    else -> 1
                }
            val transport =
                when (dao.getTransportReqdCountByPlaceId(place.id)) {
                    routes -> 2 // All have pub transport
                    0 -> 0 // None have pub transport
                    else -> 1
                }
            val distance =
                when (routes) {
                    0 -> Int.MAX_VALUE
                    else -> dao.getRoutesSumDistanceByPlaceId(place.id)
                }
            val time =
                when (routes) {
                    0 -> Int.MAX_VALUE
                    else -> dao.getRoutesSumTimeByPlaceId(place.id)
                }
            placesWithRoutes.add (
                PlaceKt(
                    place.id,
                    area.area,
                    place.place,
                    routes,
                    distance,
                    time,
                    accessibilityColor,
                    accessibilityIcon,
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
                        placesWithRoutes.sortBy { it.place }
                    }
                    else -> {
                        placesWithRoutes.sortByDescending { it.place }
                    }
                }
            }
            1 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortByDescending { it.accessible }
                    }
                    else -> {
                        placesWithRoutes.sortBy { it.accessible }
                    }
                }
            }
            2 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortByDescending { it.dogs }
                    }
                    else -> {
                        placesWithRoutes.sortBy { it.dogs }
                    }
                }
            }
            3 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortByDescending { it.shared }
                    }
                    else -> {
                        placesWithRoutes.sortBy { it.shared }
                    }
                }
            }
            4 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortByDescending { it.transport }
                    }
                    else -> {
                        placesWithRoutes.sortBy { it.transport }
                    }
                }
            }
            5 -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortBy { it.distance }
                    }
                    else -> {
                        placesWithRoutes.sortByDescending { it.distance }
                    }
                }
            }
            else -> {
                when (sortOrder) {
                    SortOrder.ASC -> {
                        placesWithRoutes.sortBy { it.time }
                    }
                    else -> {
                        placesWithRoutes.sortByDescending { it.time }
                    }
                }
            }
        }
        return placesWithRoutes
    }
}
