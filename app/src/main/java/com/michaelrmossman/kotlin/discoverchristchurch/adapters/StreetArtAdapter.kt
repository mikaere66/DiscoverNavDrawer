package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.StreetArtViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StreetArtAdapter(
    private val streetArtListener: StreetArtListener,
    private val streetArtLongListener: StreetArtLongListener
) : ListAdapter<StreetArtDataItem, RecyclerView.ViewHolder>(StreetArtDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is StreetArtViewHolder -> {
                val streetArtItem = getItem(position) as StreetArtDataItem.StreetArtItem
                holder.bind(streetArtItem.streetArt,
                    streetArtListener, streetArtLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_11, parent)
            ITEM_VIEW_TYPE_ITEM_11 -> StreetArtViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_11, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<StreetArtKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(StreetArtDataItem.Footer)
                else -> {
                    listOf(StreetArtDataItem.Header) +
                    list.map {
                        StreetArtDataItem.StreetArtItem(it)
                    } + listOf(StreetArtDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is StreetArtDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is StreetArtDataItem.StreetArtItem -> ITEM_VIEW_TYPE_ITEM_11
            is StreetArtDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class StreetArtDataItem {
    abstract val id: Long

    object Header: StreetArtDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class StreetArtItem(val streetArt: StreetArtKt): StreetArtDataItem() {
        override val id = streetArt.id
    }

    object Footer: StreetArtDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class StreetArtDiffCallback: DiffUtil.ItemCallback<StreetArtDataItem>() {

    override fun areItemsTheSame(oldItem: StreetArtDataItem, newItem: StreetArtDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: StreetArtDataItem, newItem: StreetArtDataItem): Boolean {
        return oldItem == newItem
    }
}
