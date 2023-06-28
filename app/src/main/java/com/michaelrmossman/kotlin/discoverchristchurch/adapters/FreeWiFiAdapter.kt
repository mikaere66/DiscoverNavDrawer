package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FreeWiFiLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FreeWiFiViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FreeWiFiAdapter(
    private val freeWiFiListener: BasicClickListener,
    private val freeWiFiLongListener: FreeWiFiLongListener
) : ListAdapter<FreeWiFiDataItem, RecyclerView.ViewHolder>(FreeWiFiDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is FreeWiFiViewHolder -> {
                val freeWiFiItem = getItem(position) as FreeWiFiDataItem.FreeWiFiItem
                holder.bind(freeWiFiItem.freeWiFi,
                    freeWiFiListener, freeWiFiLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_5, parent)
            ITEM_VIEW_TYPE_ITEM_5 -> FreeWiFiViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_5, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<FreeWiFiKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(FreeWiFiDataItem.Footer)
                else -> {
                    listOf(FreeWiFiDataItem.Header) +
                    list.map {
                        FreeWiFiDataItem.FreeWiFiItem(it)
                    } + listOf(FreeWiFiDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FreeWiFiDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is FreeWiFiDataItem.FreeWiFiItem -> ITEM_VIEW_TYPE_ITEM_5
            is FreeWiFiDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class FreeWiFiDataItem {
    abstract val id: Long

    object Header: FreeWiFiDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class FreeWiFiItem(val freeWiFi: FreeWiFiKt): FreeWiFiDataItem() {
        override val id = freeWiFi.id
    }

    object Footer: FreeWiFiDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class FreeWiFiDiffCallback: DiffUtil.ItemCallback<FreeWiFiDataItem>() {

    override fun areItemsTheSame(oldItem: FreeWiFiDataItem, newItem: FreeWiFiDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FreeWiFiDataItem, newItem: FreeWiFiDataItem): Boolean {
        return oldItem == newItem
    }
}
