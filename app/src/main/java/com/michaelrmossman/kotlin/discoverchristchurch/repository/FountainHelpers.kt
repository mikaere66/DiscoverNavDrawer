package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.FOUNTAINS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4

object FountainHelpers {

    fun getFountainsQuery(
        itemId: Long, types: List<DrinkType>
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("F.id,")
        sb.append(" ")
        sb.append("F.name,")
        sb.append(" ")
        sb.append("F.extra,")
        sb.append(" ")
        sb.append("F.latLng,")
        sb.append(" ")
        sb.append("F.alt,")
        sb.append(" ")
        sb.append("F.bottles,")
        sb.append(" ")
        sb.append("F.convenience,")
        sb.append(" ")
        sb.append("F.dogs,")
        sb.append(" ")
        sb.append("V.fave AS fave") //
        sb.append(" ")
        sb.append("FROM $FOUNTAINS_TABLE_NAME AS F")

        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (F.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_4)")

        when (itemId) {
            0L -> {
                val typesSelected = types.map { it.selected }
                if (
                    typesSelected.isNotEmpty() && // Just for safety
                    typesSelected.size < 3 // Bottles, dogs, convenience
                ) {
                    val fountainTypes = types.map { it.type }
                    sb.append(" ")
                    sb.append("WHERE")
                    for (i in typesSelected.indices) {
                        sb.append(" ")
                        sb.append(fountainTypes[i])
                        sb.append(" ")
                        sb.append("=")
                        sb.append(" ")
                        sb.append("1")
                        if (i < typesSelected.size.minus(1)) {
                            sb.append(" ")
                            sb.append("OR")
                        }
                    }
                }
                sb.append(" ")
                sb.append("ORDER BY name")
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE F.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
