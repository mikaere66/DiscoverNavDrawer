package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.database.BIKE_TRACKS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrack
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.enums.SortOrder
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getWaypointColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointPositionText

object BikeTrackHelpers {

    fun getBikeTracksQuery(
        itemId: Long, sortBy: Int, sortOrder: SortOrder
    ) : SimpleSQLiteQuery {
        var args: Any? = null
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("B.id,")
        sb.append(" ")
        sb.append("B.track,")
        sb.append(" ")
        sb.append("B.gain,")
        sb.append(" ")
        sb.append("B.grade,")
        sb.append(" ")
        sb.append("B.gradient,")
        sb.append(" ")
        sb.append("B.highest,")
        sb.append(" ")
        sb.append("B.landscape,")
        sb.append(" ")
        sb.append("B.length,")
        sb.append(" ")
        sb.append("B.lowest,")
        sb.append(" ")
        sb.append("B.max,")
        sb.append(" ")
        sb.append("B.notes,")
        sb.append(" ")
        sb.append("B.shared,")
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("B.textColor")
        sb.append(" ")
        sb.append("FROM $BIKE_TRACKS_TABLE_NAME AS B")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (B.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_8)")
        when (itemId) {
            0L -> {
                sb.append(" ")
                sb.append("ORDER BY")
                sb.append(" ")
                when (sortBy) {
                    4 -> sb.append("B.shared")
                    3 -> sb.append("B.length")
                    2 -> sb.append("B.gradient")
                    1 -> sb.append("B.grade")
                    else -> sb.append("B.track")
                }
                if (sortOrder == SortOrder.DESC ) {
                    sb.append(" ")
                    sb.append("DESC")
                }
            }
            else -> {
                args = itemId
                sb.append(" ")
                sb.append("WHERE B.id = ?")
            }
        }

        return when (args) {
            null -> SimpleSQLiteQuery(sb.toString())
            else -> SimpleSQLiteQuery(sb.toString(), arrayOf(args))
        }
    }

    fun getBikeTracksKt(
        bikeTracks: List<BikeTrack>
    ) : List<BikeTrackKt> {
        val bikeTracksKt = mutableListOf<BikeTrackKt>()
        val size = bikeTracks.size

        for (i in bikeTracks.indices) {
            bikeTracksKt.add(
                getBikeTrackKt(bikeTracks[i], i, size)
            )
        }

        return bikeTracksKt
    }

    private fun getBikeTrackKt(
        bikeTrack: BikeTrack, index: Int, size: Int
    ) : BikeTrackKt {
        return BikeTrackKt(
            bikeTrack.id,
            bikeTrack.track,
            bikeTrack.gain,
            bikeTrack.grade,
            bikeTrack.gradient,
            bikeTrack.highest,
            bikeTrack.landscape,
            bikeTrack.length,
            bikeTrack.lowest,
            bikeTrack.max,
            bikeTrack.notes,
            bikeTrack.shared,
            getWaypointColorId(index, size),
            false,
            index,
            size,
            getWaypointPositionText(
                index.plus(1),
                2,
                size
            ),
            bikeTrack.textColor
        )
    }

    //

    fun getBikeTracksKtWithFave(
        bikeTracks: List<BikeTrackKt>
    ) : List<BikeTrackKt> {
        val bikeTracksKt = mutableListOf<BikeTrackKt>()
        val size = bikeTracks.size

        for (i in bikeTracks.indices) {
            // Log.d("HEY1",i.toString())
            val bikeTrackIds =
                bikeTracksKt.map { bikeTrack -> bikeTrack.id }
            /* Workaround for some weird bug where two of the same
               item appear in the list after adding it to faves */
            if (!bikeTrackIds.contains(bikeTracks[i].id)) {
                // Log.d("HEY2",i.toString())
                bikeTracksKt.add(
                    getBikeTrackKtWithFave(bikeTracks[i], i, size)
                )
            }
        }

        return bikeTracksKt
    }

    fun getBikeTrackKtWithFave(
        bikeTrack: BikeTrackKt, index: Int, size: Int
    ) : BikeTrackKt {
        return BikeTrackKt(
            bikeTrack.id,
            bikeTrack.track,
            bikeTrack.gain,
            bikeTrack.grade,
            bikeTrack.gradient,
            bikeTrack.highest,
            bikeTrack.landscape,
            bikeTrack.length,
            bikeTrack.lowest,
            bikeTrack.max,
            bikeTrack.notes,
            bikeTrack.shared,
            getWaypointColorId(index, size),
            bikeTrack.fave,
            index,
            size,
            getWaypointPositionText(
                index.plus(1),
                2,
                size
            ),
            bikeTrack.textColor
        )
    }
}
