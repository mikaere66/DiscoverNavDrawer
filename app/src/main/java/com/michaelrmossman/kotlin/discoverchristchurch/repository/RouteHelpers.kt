package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.*
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.enums.SortOrder
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import kotlin.math.abs

object RouteHelpers {

    suspend fun getMultiLinkedRoutesKt(
        dao: DiscoverDao, linkedIds: List<Long>
    ) : List<RouteKt> {
        val linkedRoutes = mutableListOf<RouteKt>()
        linkedIds.forEach { linkedId ->
            val linkedRoute = dao.getRouteKtById(
                getRoutesQuery(
                    linkedId,
                    null
                )
            )
            linkedRoutes.add(linkedRoute)
        }
        return linkedRoutes
    }

    suspend fun getRoutesByFeature(
        currentFeature: String, currentSelection: Int, dao: DiscoverDao
    ) : List<RouteKt> {
        val sb = StringBuilder()

        sb.append("WHERE")
        sb.append(" ")
        sb.append(currentFeature)
        sb.append(" ")

        when (currentFeature) {
            TRANSPORT_DB -> sb.append("= 0")
            SHARED_DB -> {
                when (currentSelection) {
                    0 -> {
                        sb.append("= 1")
                    }
                    else -> {
                        sb.append("!= 1")
                    }
                }
            }
            ROUND_DB -> {
                sb.append("=")
                sb.append(" ")
                when (currentSelection) {
                    0 -> sb.append(baseRet)
                    else -> sb.append(base1way)
                }
                sb.append(" ")
                sb.append("OR")
                sb.append(" ")
                sb.append(currentFeature)
                sb.append(" ")
                sb.append("=")
                sb.append(" ")
                when (currentSelection) {
                    0 -> sb.append(extRet)
                    else -> sb.append(ext1way)
                }
            }
            else -> { // DOGS_DB | ACCESSIBLE_DB
                sb.append(" ")
                when (currentSelection) {
                    2 -> sb.append(">")
                    else -> sb.append("=")
                }
                sb.append(" ")
                sb.append(2.minus(currentSelection))
            }
        }

        sb.append(" ")
        sb.append("ORDER BY")
        sb.append(" ")
        sb.append("route")

        return dao.getRoutesKtList(
            getRoutesQuery(
                0L,
                null,
                -1,
                SortOrder.NONE,
                sb.toString()
            )
        )
    }

    fun getRoutesQuery(
        itemId: Long,
        searchTerm: String?,
        sortBy: Int = -1, // Was 0
        sortOrder: SortOrder = SortOrder.NONE,
        whereClause: String? = null
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT R.*,")
        sb.append(" ")
        sb.append(" ")
        sb.append("V.fave AS fave") //
        sb.append(" ")
        sb.append("FROM $ROUTES_TABLE_NAME AS R")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (R.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_01)")

        when (whereClause) {
            null -> {
                when (itemId) {
                    in 1L..Long.MAX_VALUE -> {
                        args = itemId
                        sb.append(" ")
                        sb.append("WHERE R.id = ?")
                    }
                    0L -> {
                        if (searchTerm?.isNotBlank() == true) {
                            args = "%$searchTerm%"
                            sb.append(" ")
                            sb.append("WHERE route")
                            sb.append(" ")
                            sb.append("LIKE ?")
                        }
                    }
                    else -> {
                        args = abs(itemId) // ID is negative!
                        sb.append(" ") // Areas|Places|Routes
                        sb.append("WHERE R.placeId = ?")
                    }
                }
                if (sortBy != -1) {
                    sb.append(" ")
                    sb.append("ORDER BY")
                    sb.append(" ")
                    when (sortBy) {
                        6 -> sb.append("distance")
                        5 -> sb.append("time")
                        4 -> sb.append("transport")
                        3 -> sb.append("shared")
                        2 -> sb.append("dogs")
                        1 -> sb.append("accessible")
                        else -> sb.append("route")
                    }
                    sb.append(" ")
                    // Allow for Māori special chars
                    sb.append("COLLATE UNICODE")
                    sb.append(" ")
                    when (sortOrder) {
                        SortOrder.ASC -> {
                            when (sortBy) {
                                in 1..3 -> sb.append("DESC")
                                else -> sb.append("ASC")
                            }
                        }
                        SortOrder.DESC -> {
                            when (sortBy) {
                                in 1..3 -> sb.append("ASC")
                                else -> sb.append("DESC")
                            }
                        }
                        SortOrder.NONE -> {}
                    }
                }
            } // Routes by Feature
            else -> sb.append(" ").append(whereClause)
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
