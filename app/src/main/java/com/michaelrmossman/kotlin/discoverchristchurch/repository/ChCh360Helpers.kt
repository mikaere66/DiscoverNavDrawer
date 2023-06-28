package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.CH_CH_360_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00

object ChCh360Helpers {

    fun getChCh360Query(itemId: Long): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT C.*,")
        sb.append(" ")
        sb.append(" ")
        sb.append("V.fave AS fave") //
        sb.append(" ")
        sb.append("FROM $CH_CH_360_TABLE_NAME AS C")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (C.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_00)")
        if (itemId > 0L) {
            args = itemId
            sb.append(" ")
            sb.append("WHERE C.id = ?")
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
