package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.UrbanPlayViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UrbanPlayAdapter(
    private val urbanPlayListener: UrbanPlayListener,
    private val urbanPlayLongListener: UrbanPlayLongListener
) : ListAdapter<UrbanPlayDataItem, RecyclerView.ViewHolder>(UrbanPlayDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is UrbanPlayViewHolder -> {
                val urbanPlayItem = getItem(position) as UrbanPlayDataItem.UrbanPlayItem
                holder.bind(urbanPlayItem.urbanPlay,
                    urbanPlayListener, urbanPlayLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_12, parent)
            ITEM_VIEW_TYPE_ITEM_12 -> UrbanPlayViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_12, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<UrbanPlayKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(UrbanPlayDataItem.Footer)
                else -> {
                    listOf(UrbanPlayDataItem.Header) +
                    list.map {
                        UrbanPlayDataItem.UrbanPlayItem(it)
                    } + listOf(UrbanPlayDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UrbanPlayDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is UrbanPlayDataItem.UrbanPlayItem -> ITEM_VIEW_TYPE_ITEM_12
            is UrbanPlayDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class UrbanPlayDataItem {
    abstract val id: Long

    object Header: UrbanPlayDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class UrbanPlayItem(val urbanPlay: UrbanPlayKt): UrbanPlayDataItem() {
        override val id = urbanPlay.id
    }

    object Footer: UrbanPlayDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class UrbanPlayDiffCallback: DiffUtil.ItemCallback<UrbanPlayDataItem>() {

    override fun areItemsTheSame(oldItem: UrbanPlayDataItem, newItem: UrbanPlayDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UrbanPlayDataItem, newItem: UrbanPlayDataItem): Boolean {
        return oldItem == newItem
    }
}
