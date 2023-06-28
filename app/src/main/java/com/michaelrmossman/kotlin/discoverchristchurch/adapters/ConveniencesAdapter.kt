package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ConvenienceLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.ConvenienceViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConveniencesAdapter(
    private val convenienceListener: BasicClickListener,
    private val convenienceLongListener: ConvenienceLongListener
) : ListAdapter<ConvenienceDataItem, RecyclerView.ViewHolder>(ConvenienceDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is ConvenienceViewHolder -> {
                val convenienceItem = getItem(position) as ConvenienceDataItem.ConvenienceItem
                holder.bind(convenienceItem.convenience,
                    convenienceListener, convenienceLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_9, parent)
            ITEM_VIEW_TYPE_ITEM_9 -> ConvenienceViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_9, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<ConvenienceKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(ConvenienceDataItem.Footer)
                else -> {
                    listOf(ConvenienceDataItem.Header) +
                    list.map {
                        ConvenienceDataItem.ConvenienceItem(it)
                    } + listOf(ConvenienceDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ConvenienceDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is ConvenienceDataItem.ConvenienceItem -> ITEM_VIEW_TYPE_ITEM_9
            is ConvenienceDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class ConvenienceDataItem {
    abstract val id: Long

    object Header: ConvenienceDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class ConvenienceItem(val convenience: ConvenienceKt): ConvenienceDataItem() {
        override val id = convenience.id
    }

    object Footer: ConvenienceDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class ConvenienceDiffCallback: DiffUtil.ItemCallback<ConvenienceDataItem>() {

    override fun areItemsTheSame(oldItem: ConvenienceDataItem, newItem: ConvenienceDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ConvenienceDataItem, newItem: ConvenienceDataItem): Boolean {
        return oldItem == newItem
    }
}
