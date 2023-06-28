package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ParkLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.ParkViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ParksAdapter(
    private val parkListener: BasicClickListener,
    private val parkLongListener: ParkLongListener
) : ListAdapter<ParkDataItem, RecyclerView.ViewHolder>(ParkDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is ParkViewHolder -> {
                val parkItem = getItem(position) as ParkDataItem.ParkItem
                holder.bind(parkItem.park,
                    parkListener, parkLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_10, parent)
            ITEM_VIEW_TYPE_ITEM_10 -> ParkViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_10, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<ParkKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(ParkDataItem.Footer)
                else -> {
                    listOf(ParkDataItem.Header) +
                    list.map {
                        ParkDataItem.ParkItem(it)
                    } + listOf(ParkDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ParkDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is ParkDataItem.ParkItem -> ITEM_VIEW_TYPE_ITEM_10
            is ParkDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class ParkDataItem {
    abstract val id: Long

    object Header: ParkDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class ParkItem(val park: ParkKt): ParkDataItem() {
        override val id = park.id
    }

    object Footer: ParkDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class ParkDiffCallback: DiffUtil.ItemCallback<ParkDataItem>() {

    override fun areItemsTheSame(oldItem: ParkDataItem, newItem: ParkDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ParkDataItem, newItem: ParkDataItem): Boolean {
        return oldItem == newItem
    }
}
