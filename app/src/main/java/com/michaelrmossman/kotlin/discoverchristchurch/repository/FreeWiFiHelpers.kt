package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.FREE_WI_FI_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5

object FreeWiFiHelpers {

    fun getFreeWiFiLocationsQuery(
        itemId: Long, sort: Boolean
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("W.id,")
        sb.append(" ")
        sb.append("W.name,")
        sb.append(" ")
        sb.append("W.extra,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("W.latLng")
        sb.append(" ")
        sb.append("FROM $FREE_WI_FI_TABLE_NAME AS W")

        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (W.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_5)")

        when (itemId) {
            0L -> {
                if (sort) {
                    sb.append(" ")
                    sb.append("ORDER BY name")
                }
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE W.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
