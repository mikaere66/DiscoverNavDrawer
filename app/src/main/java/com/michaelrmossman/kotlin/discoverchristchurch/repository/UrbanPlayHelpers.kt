package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.URBAN_PLAY_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12

object UrbanPlayHelpers {

    fun getUrbanPlayQuery(itemId: Long): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("U.id,")
        sb.append(" ")
        sb.append("U.name,")
        sb.append(" ")
        sb.append("U.address,")
        sb.append(" ")
        sb.append("U.cost,")
        sb.append(" ")
        sb.append("U.description,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("U.hours,")
        sb.append(" ")
        sb.append("U.image,")
        sb.append(" ")
        sb.append("U.landscape,")
        sb.append(" ")
        sb.append("U.latLng")
        sb.append(" ")
        sb.append("FROM $URBAN_PLAY_TABLE_NAME AS U")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (U.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_12)")
        when (itemId) {
            0L -> {
                sb.append(" ")
                sb.append("ORDER BY U.name")
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE U.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
