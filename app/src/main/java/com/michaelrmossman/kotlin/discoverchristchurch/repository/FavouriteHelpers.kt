package com.michaelrmossman.kotlin.discoverchristchurch.repository

import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Favourite
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_01
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_02

object FavouriteHelpers {

    suspend fun toggleFavourite(
        checked: Boolean,
        dao: DiscoverDao,
        itemId: Long,
        itemType: Int
    ) : Long {
        return when (checked) {
            true -> { // Returns new ID of inserted favourite as Long
                val actualType = when (itemType) {
                    /* Need to allow for linked routes, which are
                       handled slightly differently in viewModel */
                    ITEM_VIEW_TYPE_ITEM_02 -> ITEM_VIEW_TYPE_ITEM_01
                    else -> itemType
                }
                dao.insertFavourite(getFavourite(actualType, itemId))
            }
            else -> { // A negative value indicates favourite removed
                dao.deleteFavourite(itemId).toLong().unaryMinus()
            }
        }
    }

    private fun getFavourite(
        actualType: Int,
        itemId: Long
    ) : Favourite {
        val now = System.currentTimeMillis()
        return Favourite(
            now,
            itemId,
            actualType,
            true
        )
    }
}
