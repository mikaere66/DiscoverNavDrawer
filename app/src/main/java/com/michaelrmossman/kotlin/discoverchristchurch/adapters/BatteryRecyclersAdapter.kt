package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BatteryRecyclerLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.BatteryRecyclerViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BatteryRecyclersAdapter(
    private val batteryRecyclerListener: BasicClickListener,
    private val batteryRecyclerLongListener: BatteryRecyclerLongListener
) : ListAdapter<BatteryDataItem, RecyclerView.ViewHolder>(BatteryRecyclerDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is BatteryRecyclerViewHolder -> {
                val batteryRecyclerItem = getItem(position) as BatteryDataItem.BatteryRecyclerItem
                holder.bind(batteryRecyclerItem.batteryRecycler,
                    batteryRecyclerListener, batteryRecyclerLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_1, parent)
            ITEM_VIEW_TYPE_ITEM_1 -> BatteryRecyclerViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_1, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<BatteryRecyclerKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(BatteryDataItem.Footer)
                else -> {
                    listOf(BatteryDataItem.Header) +
                    list.map {
                        BatteryDataItem.BatteryRecyclerItem(it)
                    } + listOf(BatteryDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BatteryDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is BatteryDataItem.BatteryRecyclerItem -> ITEM_VIEW_TYPE_ITEM_1
            is BatteryDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class BatteryDataItem {
    abstract val id: Long

    object Header: BatteryDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class BatteryRecyclerItem(val batteryRecycler: BatteryRecyclerKt): BatteryDataItem() {
        override val id = batteryRecycler.id
    }

    object Footer: BatteryDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class BatteryRecyclerDiffCallback: DiffUtil.ItemCallback<BatteryDataItem>() {

    override fun areItemsTheSame(oldItem: BatteryDataItem, newItem: BatteryDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BatteryDataItem, newItem: BatteryDataItem): Boolean {
        return oldItem == newItem
    }
}
