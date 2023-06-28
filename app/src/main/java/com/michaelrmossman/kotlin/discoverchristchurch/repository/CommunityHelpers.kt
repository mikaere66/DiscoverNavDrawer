package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery

object CommunityHelpers {

    fun getCommunityQuery(count: Int, itemId: Int): SimpleSQLiteQuery {
        val arg1: Any = count
        val arg2: Any = itemId.plus(1).toLong()
        val sb = StringBuilder()

        sb.append("UPDATE community_items_table")
        sb.append(" ")
        sb.append("SET count = ?")
        sb.append(" ")
        sb.append("WHERE id = ?")

        return SimpleSQLiteQuery(sb.toString(), arrayOf(arg1, arg2))
    }
}
