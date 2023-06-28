package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BikeTrackLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.BikeTrackViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BikeTracksAdapter(
    private val bikeTrackListener: BikeTrackListener,
    private val bikeTrackLongListener: BikeTrackLongListener
) : ListAdapter<BikeDataItem, RecyclerView.ViewHolder>(BikeTrackDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is BikeTrackViewHolder -> {
                val bikeTrackItem = getItem(position) as BikeDataItem.BikeTrackItem
                holder.bind(bikeTrackItem.bikeTrack,
                    bikeTrackListener, bikeTrackLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_8, parent)
            ITEM_VIEW_TYPE_ITEM_8 -> BikeTrackViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_8, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<BikeTrackKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(BikeDataItem.Footer)
                else -> {
                    listOf(BikeDataItem.Header) +
                    list.map {
                        BikeDataItem.BikeTrackItem(it)
                    } + listOf(BikeDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BikeDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is BikeDataItem.BikeTrackItem -> ITEM_VIEW_TYPE_ITEM_8
            is BikeDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class BikeDataItem {
    abstract val id: Long

    object Header: BikeDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class BikeTrackItem(val bikeTrack: BikeTrackKt): BikeDataItem() {
        override val id = bikeTrack.id
    }

    object Footer: BikeDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class BikeTrackDiffCallback: DiffUtil.ItemCallback<BikeDataItem>() {

    override fun areItemsTheSame(oldItem: BikeDataItem, newItem: BikeDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: BikeDataItem, newItem: BikeDataItem): Boolean {
        return oldItem == newItem
    }
}
