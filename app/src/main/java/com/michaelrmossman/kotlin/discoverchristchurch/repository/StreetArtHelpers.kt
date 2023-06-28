package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.STREET_ART_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getWaypointColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointPositionText

object StreetArtHelpers {

    fun getStreetArtFullKt(streetArt: List<StreetArtKt>): List<StreetArtKt> {
        val streetArtKt = mutableListOf<StreetArtKt>()
        val size = streetArt.size

        for (i in streetArt.indices) {
            streetArtKt.add(
                StreetArtKt(
                    streetArt[i].id,
                    streetArt[i].latLng,
                    streetArt[i].extra,
                    streetArt[i].image,
                    streetArt[i].landscape,
                    streetArt[i].title,
                    streetArt[i].artistIds,
                    streetArt[i].description,
                    getWaypointColorId(i, size), //
                    streetArt[i].credit,
                    streetArt[i].date,
                    streetArt[i].fave,
                    i, //
                    size, //
                    getWaypointPositionText(
                        i.plus(1),
                        -4,
                        size
                    ), //
                    streetArt[i].viewableId
                )
            )
        }

        return streetArtKt
    }

    fun getStreetArtQuery(
        itemId: Long, searchTerm: String?
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("S.id,")
        sb.append(" ")
        sb.append("S.latLng,")
        sb.append(" ")
        sb.append("S.extra,")
        sb.append(" ")
        sb.append("S.image,")
        sb.append(" ")
        sb.append("S.landscape,")
        sb.append(" ")
        sb.append("S.title,")
        sb.append(" ")
        sb.append("S.artistIds,")
        sb.append(" ")
        sb.append("S.description,")
        sb.append(" ")
        sb.append("S.credit,")
        sb.append(" ")
        sb.append("S.date,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("S.viewableId")
        sb.append(" ")
        sb.append("FROM $STREET_ART_TABLE_NAME AS S")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (S.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_11)")

//        when (searchTerm?.isNotBlank()) {
//            true -> {
//                args = "%$searchTerm%"
//                sb.append(" ")
//                sb.append("WHERE title")
//                sb.append(" ")
//                sb.append("LIKE ?")
//            }
//            else -> {
//                when (itemId) {
//                    0L -> {} // Do nothing
//                    -1L -> { // Random
//                        sb.append(" ")
//                        sb.append("ORDER BY RANDOM()")
//                    }
//                    else -> {
//                        args = itemId
//                        sb.append(" ")
//                        sb.append("WHERE S.id = ?")
//                    }
//                }
//            }
//        }

        when (itemId) {
            0L -> {
                if (searchTerm?.isNotBlank() == true) {
                    args = "%$searchTerm%"
                    sb.append(" ")
                    sb.append("WHERE title")
                    sb.append(" ")
                    sb.append("LIKE ?")
                }
            }
            -1L -> { // Random
                sb.append(" ")
                sb.append("ORDER BY RANDOM()")
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE S.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }
}
