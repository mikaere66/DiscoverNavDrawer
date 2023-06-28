package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.PARKS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.PARK_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import kotlinx.coroutines.flow.Flow

object ParkHelpers {

    fun getParksBy(
        dao: DiscoverDao, searchTerm: String? // Also filterTerm
    ) : Flow<List<ParkKt>> {
        val simpleQuery = getParksQuery(0L, searchTerm,true)
        return dao.getParksFlow(simpleQuery)
    }

    fun getParksQuery(
        itemId: Long, searchTerm: String?, sort: Boolean
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("P.id,")
        sb.append(" ")
        sb.append("P.name,")
        sb.append(" ")
        sb.append("P.typeId,")
        sb.append(" ")
        sb.append("P.area,")
        sb.append(" ")
        sb.append("P.perimeter,")
        sb.append(" ")
        sb.append("P.environmentIds,")
        sb.append(" ")
        sb.append("P.latLng,")
        sb.append(" ")
        sb.append("T.border AS border,")
        sb.append(" ")
        sb.append("T.color AS color,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.type AS type")
        sb.append(" ")
        sb.append("FROM $PARKS_TABLE_NAME AS P")
        sb.append(" ")
        sb.append("INNER JOIN $PARK_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON P.typeId = T.id")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (P.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_10)")
        when (itemId) {
            0L -> {
                sb.append(" ")
                sb.append("WHERE T.selected = 1")
                if (searchTerm?.isNotBlank() == true) {
                    args = "%$searchTerm%"
                    sb.append(" ")
                    sb.append("AND name")
                    sb.append(" ")
                    sb.append("LIKE ?")
                }
                if (sort) {
                    sb.append(" ")
                    sb.append("ORDER BY name")
                }
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE P.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }

    suspend fun getParkTypesKt(
        dao: DiscoverDao, parkTypes: List<ParkType>
    ) : List<ParkTypeKt> {
        val parkTypesKt = mutableListOf<ParkTypeKt>()

        parkTypes.forEach { parkType ->
            val count = dao.getParkCountByType(parkType.id)

            parkTypesKt.add(
                ParkTypeKt(
                    parkType.id,
                    parkType.type,
                    parkType.border,
                    parkType.color,
                    count, //
                    parkType.selected
                )
            )
        }

        return parkTypesKt
    }
}
