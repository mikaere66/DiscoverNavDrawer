package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FountainLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FountainViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FountainsAdapter(
    private val fountainListener: BasicClickListener,
    private val fountainLongListener: FountainLongListener
) : ListAdapter<FountainDataItem, RecyclerView.ViewHolder>(FountainDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is FountainViewHolder -> {
                val fountainItem = getItem(position) as FountainDataItem.FountainItem
                holder.bind(fountainItem.fountain,
                    fountainListener, fountainLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_4, parent)
            ITEM_VIEW_TYPE_ITEM_4 -> FountainViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_4, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<DrinkFountainKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(FountainDataItem.Footer)
                else -> {
                    listOf(FountainDataItem.Header) +
                    list.map {
                        FountainDataItem.FountainItem(it)
                    } + listOf(FountainDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FountainDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is FountainDataItem.FountainItem -> ITEM_VIEW_TYPE_ITEM_4
            is FountainDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class FountainDataItem {
    abstract val id: Long

    object Header: FountainDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class FountainItem(val fountain: DrinkFountainKt): FountainDataItem() {
        override val id = fountain.id
    }

    object Footer: FountainDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class FountainDiffCallback: DiffUtil.ItemCallback<FountainDataItem>() {

    override fun areItemsTheSame(oldItem: FountainDataItem, newItem: FountainDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FountainDataItem, newItem: FountainDataItem): Boolean {
        return oldItem == newItem
    }
}
