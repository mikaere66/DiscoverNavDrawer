package com.michaelrmossman.kotlin.discoverchristchurch.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.database.FAVOURITES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FRUIT_CATS_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FRUIT_TREES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.database.FRUIT_TYPES_TABLE_NAME
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCat
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCatKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6

object FruitTreeHelpers {

    suspend fun getFruitCategoriesKt(
        dao: DiscoverDao, fruitCats: List<FruitCat>
    ) : List<FruitCatKt> {
        val fruitCatsKt = mutableListOf<FruitCatKt>()

        fruitCats.forEach { cat ->
            val count = dao.getFruitTreesCountByCatId(cat.id)
            fruitCatsKt.add(
                FruitCatKt(
                    cat.id,
                    cat.category,
                    cat.color,
                    count, //
                    cat.months,
                    cat.selected
                )
            )
        }

        return fruitCatsKt
    }

    suspend fun getFruitCatsWithTypes(
        cats: List<FruitCat>, dao: DiscoverDao
    ) : List<FruitTypeKt> {
        val fruitCatsWithTypes = mutableListOf<FruitTypeKt>()

        cats.forEach { cat ->
            val types = dao.getFruitTypesByCatId(cat.id)
            types.forEach { type ->
                fruitCatsWithTypes.add(
                    dao.getFruitTypeByCatId(
                        getFruitTypeQuery(
                            cat.id, type.id
                        )
                    )
                )
            }
        }

        fruitCatsWithTypes.sortBy { it.type }

        return fruitCatsWithTypes
    }

    fun getFruitTypeQuery(
        catId: Long, typeId: Long
    ) : SimpleSQLiteQuery {
        val arg1: Any = catId
        val arg2: Any = typeId
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("T.id,")
        sb.append(" ")
        sb.append("T.categoryId,")
        sb.append(" ")
        sb.append("C.category,") //
        sb.append(" ")
        sb.append("C.color,") //
        sb.append(" ")
        sb.append("F.count AS count,") //
        sb.append(" ")
        sb.append("V.fave AS fave,") //
        sb.append(" ")
        sb.append("T.type")
        sb.append(" ")
        sb.append("FROM $FRUIT_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("INNER JOIN $FRUIT_CATS_TABLE_NAME AS C")
        sb.append(" ")
        sb.append("ON T.categoryId = C.id")
        sb.append(" ")
        sb.append("INNER JOIN (") // Note curly bracket
        sb.append("SELECT COUNT (*) AS count FROM $FRUIT_TREES_TABLE_NAME")
        sb.append(" ")
        sb.append("WHERE typeId = ?")
        sb.append(") AS F") // Note curly bracket
        sb.append(" ")
        sb.append("ON T.categoryId = ?")
        sb.append(" ")
        sb.append("LEFT JOIN $FAVOURITES_TABLE_NAME AS V")
        sb.append(" ")
        sb.append("ON (T.id = V.itemId") // Note curly brackets
        sb.append(" ")
        sb.append("AND V.itemType = $ITEM_VIEW_TYPE_ITEM_6)")
        sb.append(" ")
        sb.append("WHERE C.id = ? AND T.id = ?")
        sb.append(" ")
        sb.append("LIMIT 1") // Just for safety

        return SimpleSQLiteQuery(sb.toString(), arrayOf(arg2, arg1, arg1, arg2))
    }

    fun getFruitTreesQuery(id: Long): SimpleSQLiteQuery {
        val args: Any = id
        val sb = StringBuilder()

        sb.append("SELECT")
        sb.append(" ")
        sb.append("F.id,")
        sb.append(" ")
        sb.append("C.category,") //
        sb.append(" ")
        sb.append("C.color,") //
        sb.append(" ")
        sb.append("F.latLng,")
        sb.append(" ")
        sb.append("T.type") //
        sb.append(" ")
        sb.append("FROM $FRUIT_TREES_TABLE_NAME AS F")
        sb.append(" ")
        sb.append("INNER JOIN $FRUIT_CATS_TABLE_NAME AS C")
        sb.append(" ")
        sb.append("ON F.categoryId = C.id")
        sb.append(" ")
        sb.append("INNER JOIN $FRUIT_TYPES_TABLE_NAME AS T")
        sb.append(" ")
        sb.append("ON F.typeId = T.id")
        sb.append(" ")
        sb.append("WHERE F.typeId = ?")

        return SimpleSQLiteQuery(sb.toString(), arrayOf(args))
    }
}
