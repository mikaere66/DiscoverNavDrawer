package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FruitTypeLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FruitTypeViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FruitTypesAdapter(
    private val fruitTypeListener: BasicClickListener,
    private val fruitTypeLongListener: FruitTypeLongListener
) : ListAdapter<FruitDataItem, RecyclerView.ViewHolder>(FruitTypeDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is FruitTypeViewHolder -> {
                val fruitTypeItem = getItem(position) as FruitDataItem.FruitTypeItem
                holder.bind(fruitTypeItem.fruitType,
                    fruitTypeListener, fruitTypeLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_6, parent)
            ITEM_VIEW_TYPE_ITEM_6 -> FruitTypeViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_6, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<FruitTypeKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(FruitDataItem.Footer)
                else -> {
                    listOf(FruitDataItem.Header) +
                    list.map {
                        FruitDataItem.FruitTypeItem(it)
                    } + listOf(FruitDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FruitDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is FruitDataItem.FruitTypeItem -> ITEM_VIEW_TYPE_ITEM_6
            is FruitDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class FruitDataItem {
    abstract val id: Long

    object Header: FruitDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class FruitTypeItem(val fruitType: FruitTypeKt): FruitDataItem() {
        override val id = fruitType.id
    }

    object Footer: FruitDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class FruitTypeDiffCallback: DiffUtil.ItemCallback<FruitDataItem>() {

    override fun areItemsTheSame(oldItem: FruitDataItem, newItem: FruitDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FruitDataItem, newItem: FruitDataItem): Boolean {
        return oldItem == newItem
    }
}
