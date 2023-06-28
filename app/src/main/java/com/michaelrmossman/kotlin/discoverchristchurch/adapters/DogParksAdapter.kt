package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.DogParkLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.DogParkViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DogParksAdapter(
    private val dogParkListener: DogParkListener,
    private val dogParkLongListener: DogParkLongListener
) : ListAdapter<DogDataItem, RecyclerView.ViewHolder>(DogParkDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is DogParkViewHolder -> {
                val dogParkItem = getItem(position) as DogDataItem.DogParkItem
                holder.bind(dogParkItem.dogPark,
                    dogParkListener, dogParkLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_3, parent)
            ITEM_VIEW_TYPE_ITEM_3 -> DogParkViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_3, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<DogParkKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(DogDataItem.Footer)
                else -> {
                    listOf(DogDataItem.Header) +
                    list.map {
                        DogDataItem.DogParkItem(it)
                    } + listOf(DogDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is DogDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is DogDataItem.DogParkItem -> ITEM_VIEW_TYPE_ITEM_3
            is DogDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class DogDataItem {
    abstract val id: Long

    object Header: DogDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class DogParkItem(val dogPark: DogParkKt): DogDataItem() {
        override val id = dogPark.id
    }

    object Footer: DogDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class DogParkDiffCallback: DiffUtil.ItemCallback<DogDataItem>() {

    override fun areItemsTheSame(oldItem: DogDataItem, newItem: DogDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DogDataItem, newItem: DogDataItem): Boolean {
        return oldItem == newItem
    }
}
