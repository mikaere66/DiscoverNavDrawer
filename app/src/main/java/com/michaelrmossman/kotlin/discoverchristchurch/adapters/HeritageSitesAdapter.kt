package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeritageSiteViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HeritageSitesAdapter(
    private val heritageSiteListener: HeritageSiteListener,
    private val heritageSiteLongListener: HeritageSiteLongListener
) : ListAdapter<SiteDataItem, RecyclerView.ViewHolder>(HeritageSiteDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is HeritageSiteViewHolder -> {
                val heritageSiteItem = getItem(position) as SiteDataItem.HeritageSiteItem
                holder.bind(heritageSiteItem.heritageSite,
                    heritageSiteListener, heritageSiteLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_7, parent)
            ITEM_VIEW_TYPE_ITEM_7 -> HeritageSiteViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_7, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<HeritageSiteKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(SiteDataItem.Footer)
                else -> {
                    listOf(SiteDataItem.Header) +
                    list.map {
                        SiteDataItem.HeritageSiteItem(it)
                    } + listOf(SiteDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SiteDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is SiteDataItem.HeritageSiteItem -> ITEM_VIEW_TYPE_ITEM_7
            is SiteDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class SiteDataItem {
    abstract val id: Long

    object Header: SiteDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class HeritageSiteItem(val heritageSite: HeritageSiteKt): SiteDataItem() {
        override val id = heritageSite.id
    }

    object Footer: SiteDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class HeritageSiteDiffCallback: DiffUtil.ItemCallback<SiteDataItem>() {

    override fun areItemsTheSame(oldItem: SiteDataItem, newItem: SiteDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SiteDataItem, newItem: SiteDataItem): Boolean {
        return oldItem == newItem
    }
}
