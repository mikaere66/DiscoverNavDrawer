package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogPark
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getLatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getWaypointColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointPositionText
import kotlinx.coroutines.flow.Flow

object DogParkHelpers {

    private suspend fun getDogParkKt(
        dao: DiscoverDao, dogPark: DogPark, dogParkType: DogType
    ) : DogParkKt {
        val fave = dao.getFavouriteCountByIdAndType(
            dogPark.id, ITEM_VIEW_TYPE_ITEM_3
        ) != 0

        return DogParkKt(
            dogPark.id,
            dogPark.dogPark,
            dogPark.extra,
            dogPark.typeId,
            dogPark.environmentIds,
            dogPark.holeIds,
            dogPark.details,
            dogPark.angle,
            dogParkType.border, //
            dogParkType.color, //
            0,
            dogPark.dogFacilities,
            dogPark.dogInfo,
            dogPark.dogNote,
            fave,
            dogPark.latLng?.let { getLatLng(it) },
            dogPark.linkedIds,
            0,
            0,
            String(),
            dogPark.startPoint
        )
    }

    fun getDogParksBy(
        dao: DiscoverDao, searchTerm: String?
    ) : Flow<List<DogParkKt>> {
        val simpleQuery = getDogParksQuery(0L, searchTerm)
        return dao.getDogParksFlow(simpleQuery)
    }

    suspend fun getDogParksKt(
        dao: DiscoverDao, dogParkTypes: List<DogType>
    ) : List<DogParkKt> {
        val dogParksKt = mutableListOf<DogParkKt>()

        dogParkTypes.forEach { dogParkType ->
            if (dogParkType.selected) {
                val dogParks = dao.getDogParksByType(dogParkType.id)

                dogParks.forEach { dogPark ->
                    dogParksKt.add(getDogParkKt(dao, dogPark, dogParkType))
                }
            }
        }

        dogParksKt.sortBy { it.dogPark }

        return dogParksKt
    }

    fun getDogParksFullKt(
        dogParks: List<DogParkKt>
    ) : List<DogParkKt> {
        val dogParksKt = mutableListOf<DogParkKt>()
        val size = dogParks.size

        for (i in dogParks.indices) {
            dogParksKt.add(
                DogParkKt(
                    dogParks[i].id,
                    dogParks[i].dogPark,
                    dogParks[i].extra,
                    dogParks[i].typeId,
                    dogParks[i].environmentIds,
                    dogParks[i].holeIds,
                    dogParks[i].details,
                    dogParks[i].angle,
                    dogParks[i].border,
                    dogParks[i].color,
                    getWaypointColorId(i, size), //
                    dogParks[i].dogFacilities,
                    dogParks[i].dogInfo,
                    dogParks[i].dogNote,
                    dogParks[i].fave,
                    dogParks[i].latLng,
                    dogParks[i].linkedIds,
                    i, //
                    size, //
                    getWaypointPositionText(
                        i.plus(1),
                        -5,
                        size
                    ), //
                    dogParks[i].startPoint
                )
            )
        }

        return dogParksKt

    }

    fun getDogParksQuery(
        itemId: Long, searchTerm: String?
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("P.id,")
        sb.append(" ")
        sb.append("P.dogPark,")
        sb.append(" ")
        sb.append("P.extra,")
        sb.append(" ")
        sb.append("P.typeId,")
        sb.append(" ")
        sb.append("P.environmentIds,")
        sb.append(" ")
        sb.append("P.holeIds,")
        sb.append(" ")
        sb.append("P.details,")
        sb.append(" ")
        sb.append("P.angle,")
        sb.append(" ")
        sb.append("T.border,") //
        sb.append(" ")
        sb.append("T.color,") //
        sb.append(" ")
        sb.append("P.dogFacilities,")
        sb.append(" ")
        sb.append("P.dogInfo,")
        sb.append(" ")
        sb.append("P.dogNote,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("P.latLng,")
        sb.append(" ")
        sb.append("P.linkedIds,")
        sb.append(" ")
        sb.append("P.startPoint")
        sb.append(" ")
        sb.append("FROM dog_parks_table AS P")

        sb.append(" ")
        sb.append("INNER JOIN dog_types_table AS T")
        sb.append(" ")
        sb.append("ON P.typeId = T.id")

        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (P.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_3)")

        when (itemId) {
            0L -> {
               sb.append(" ")
               sb.append("WHERE T.selected = 1")
                if (searchTerm?.isNotBlank() == true) {
                    args = "%$searchTerm%"
                    sb.append(" ")
                    sb.append("AND dogPark")
                    sb.append(" ")
                    sb.append("LIKE ?")
                }
               sb.append(" ")
               sb.append("ORDER BY P.dogPark")
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

    suspend fun getDogParkTypesKt(
        dao: DiscoverDao, dogParkTypes: List<DogType>
    ) : List<DogTypeKt> {
        val dogParkTypesKt = mutableListOf<DogTypeKt>()
        dogParkTypes.forEach { dogParkType ->
            val count =
                when (dogParkType.id) {
                    1L -> {
                        val dogExerciseAreas = dao.getDogParkCountByType(8)
                        val dogParks = dao.getDogParkCountByType(9)
                        dogExerciseAreas.plus(dogParks)
                    }
                    else -> dao.getDogParkCountByType(dogParkType.id)
                }
           
            dogParkTypesKt.add(
                DogTypeKt(
                    dogParkType.id,
                    dogParkType.bylaw,
                    dogParkType.border,
                    dogParkType.color,
                    count, //
                    dogParkType.selected
                )
            )
        }
        return dogParkTypesKt
    }
}
