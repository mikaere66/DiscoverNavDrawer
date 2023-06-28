package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.CONVENIENCES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.CONVENIENCE_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9

object ConvenienceHelpers {

    fun getConveniencesQuery(itemId: Long): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("C.id,")
        sb.append(" ")
        sb.append("C.name,")
        sb.append(" ")
        sb.append("C.extra,")
        sb.append(" ")
        sb.append("C.typeId,")
        sb.append(" ")
        sb.append("C.hours,")
        sb.append(" ")
        sb.append("C.latLng,")
        sb.append(" ")
        sb.append("T.id AS typeId,") // Must specify explicitly
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.selected")
        sb.append(" ")
        sb.append("FROM $CONVENIENCES_TABLE_NAME AS C")
        sb.append(" ")
        sb.append("INNER JOIN $CONVENIENCE_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON C.typeId = T.id")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (C.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_9)")
        when (itemId) {
            0L -> {
                sb.append(" ")
                sb.append("WHERE T.selected = 1")
                sb.append(" ")
                sb.append("ORDER BY C.name")
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE C.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
