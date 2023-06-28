package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.FACILITIES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FACILITY_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityTypeKt
import kotlinx.coroutines.flow.Flow

object FacilityHelpers {

    fun getFacilitiesBy(
        dao: DiscoverDao, searchTerm: String? // Also filterTerm
    ) : Flow<List<FacilityKt>> {
        val simpleQuery = getFacilitiesQuery(0L, searchTerm)
        return dao.getFacilitiesFlow(simpleQuery)
    }

    fun getFacilitiesQuery(itemId: Long, searchTerm: String?): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("F.id,")
        sb.append(" ")
        sb.append("F.facility,")
        sb.append(" ")
        sb.append("F.extra,")
        sb.append(" ")
        sb.append("F.latLng,")
        sb.append(" ")
        sb.append("F.typeId,")
        sb.append(" ")
        sb.append("F.url,")
        sb.append(" ")
        sb.append("T.color AS color,") //
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.type AS type") //
        sb.append(" ")
        sb.append("FROM $FACILITIES_TABLE_NAME AS F")
        sb.append(" ")
        sb.append("INNER JOIN $FACILITY_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON F.typeId = T.id")

        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (F.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_2)")

        if (itemId < 1L) {
            sb.append(" ")
            sb.append("WHERE T.selected = 1")

            if (searchTerm?.isNotBlank() == true) {
                args = "%$searchTerm%"
                sb.append(" ")
                sb.append("AND F.facility")
                sb.append(" ")
                sb.append("LIKE ?")
            }

            if (itemId == 0L) { // Map list does NOT require sorting
                sb.append(" ")
                sb.append("ORDER BY")
                sb.append(" ")
                sb.append("facility")
                sb.append(" ")
                // Allow for Māori special chars
                sb.append("COLLATE UNICODE")
            }

        } else {
            args = itemId
            sb.append(" ")
            sb.append("WHERE F.id = ?")
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }

    suspend fun getFacilityTypesKt(
        dao: DiscoverDao, facilityTypes: List<FacilityType>
    ) : List<FacilityTypeKt> {
        val facilityTypesKt = mutableListOf<FacilityTypeKt>()

        facilityTypes.forEach { facilityType ->
            val count = dao.getFacilityCountByType(facilityType.id)

            facilityTypesKt.add(
                FacilityTypeKt(
                    facilityType.id,
                    facilityType.type,
                    facilityType.color,
                    count, //
                    facilityType.selected
                )
            )
        }

        return facilityTypesKt
    }
}
