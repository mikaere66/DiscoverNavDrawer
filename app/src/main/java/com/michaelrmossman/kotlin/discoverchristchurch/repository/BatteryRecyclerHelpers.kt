package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.BATTERY_COLORS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.BATTERY_RECYCLERS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.BATTERY_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1

object BatteryRecyclerHelpers {

    fun getBatteryRecyclersQuery(itemId: Long): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("R.id,")
        sb.append(" ")
        sb.append("R.name,")
        sb.append(" ")
        sb.append("R.address,")
        sb.append(" ")
        sb.append("R.hours,")
        sb.append(" ")
        sb.append("R.latLng,")
        sb.append(" ")
        sb.append("R.local,")
        sb.append(" ")
        sb.append("R.typeId,")
        sb.append(" ")
        sb.append("T.id AS typeId,") // Must specify explicitly
        sb.append(" ")
        sb.append("C.color AS color,") //
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.type AS type") //
        sb.append(" ")
        sb.append("FROM $BATTERY_RECYCLERS_TABLE_NAME AS R")
        sb.append(" ")
        sb.append("INNER JOIN $BATTERY_COLORS_TABLE_NAME AS C")
        sb.append(" ")
        sb.append("ON R.local = C.local")
        sb.append(" ")
        sb.append("INNER JOIN $BATTERY_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON R.typeId = T.id")

        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (R.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_1)")

        if (itemId > 0L) {
            args = itemId
            sb.append(" ")
            sb.append("WHERE R.id = ?")
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
