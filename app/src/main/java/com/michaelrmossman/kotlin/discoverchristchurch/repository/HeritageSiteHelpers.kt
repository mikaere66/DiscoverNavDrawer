package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.HERITAGE_SITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.HERITAGE_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageType
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getWaypointColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointPositionText

object HeritageSiteHelpers {

    fun getHeritageSitesFullKt(heritageSites: List<HeritageSiteKt>): List<HeritageSiteKt> {
        val heritageSitesKt = mutableListOf<HeritageSiteKt>()
        val size = heritageSites.size

        for (i in heritageSites.indices) {
            heritageSitesKt.add(
                HeritageSiteKt(
                    heritageSites[i].id,
                    heritageSites[i].name,
                    heritageSites[i].address,
                    heritageSites[i].angle,
                    heritageSites[i].image,
                    heritageSites[i].landscape,
                    heritageSites[i].latLng,
                    heritageSites[i].typeId,
                    heritageSites[i].color,
                    getWaypointColorId(i, size), //
                    heritageSites[i].fave,
                    i, //
                    size, //
                    getWaypointPositionText(
                        i.plus(1),
                        -3,
                        size
                    ), //
                    heritageSites[i].type
                )
            )
        }

        return heritageSitesKt
    }

    fun getHeritageSitesQuery(id: Long): SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("S.id,")
        sb.append(" ")
        sb.append("S.name,")
        sb.append(" ")
        sb.append("S.address,")
        sb.append(" ")
        sb.append("S.angle,")
        sb.append(" ")
        sb.append("S.image,")
        sb.append(" ")
        sb.append("S.landscape,")
        sb.append(" ")
        sb.append("S.latLng,")
        sb.append(" ")
        sb.append("S.typeId,")
        sb.append(" ")
        sb.append("T.id AS typeId,") // Must specify explicitly
        sb.append(" ")
        sb.append("S.url,")
        sb.append(" ")
        sb.append("T.color AS color,") //
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.type AS type,") //
        sb.append(" ")
        sb.append("T.selected")
        sb.append(" ")
        sb.append("FROM $HERITAGE_SITES_TABLE_NAME AS S")
        sb.append(" ")
        sb.append("INNER JOIN $HERITAGE_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON S.typeId = T.id")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (S.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_7)")
        when (id) {
            0L -> {
               sb.append(" ")
               sb.append("WHERE T.selected = 1")
               sb.append(" ")
               sb.append("ORDER BY S.name")
           }
            else -> {
                args = id
                sb.append(" ")
                sb.append("WHERE S.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }

    suspend fun getHeritageTypesKt(
        dao: DiscoverDao, heritageTypes: List<HeritageType>
    ) : List<HeritageTypeKt> {
        val heritageTypesKt = mutableListOf<HeritageTypeKt>()

        heritageTypes.forEach { heritageSite ->
            val count = dao.getHeritageSiteCountByType(heritageSite.id)

            heritageTypesKt.add(
                HeritageTypeKt(
                    heritageSite.id,
                    heritageSite.color,
                    heritageSite.type,
                    count, //
                    heritageSite.selected
                )
            )
        }

        return heritageTypesKt
    }
}
