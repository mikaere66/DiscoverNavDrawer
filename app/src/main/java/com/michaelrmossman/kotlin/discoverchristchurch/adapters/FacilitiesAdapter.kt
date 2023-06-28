package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.BasicClickListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FacilityLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_FOOTER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_HEADER
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FacilityViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.HeaderViewHolder
import com.michaelrmossman.kotlin.discoverchristchurch.viewholders.FooterViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FacilitiesAdapter(
    private val facilityListener: BasicClickListener,
    private val facilityLongListener: FacilityLongListener
) : ListAdapter<FacilityDataItem, RecyclerView.ViewHolder>(FacilityDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, position: Int
    ) {
        when (holder) {
            is FacilityViewHolder -> {
                val facilityItem = getItem(position) as FacilityDataItem.FacilityItem
                holder.bind(facilityItem.facility,
                    facilityListener, facilityLongListener)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) : RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_HEADER -> HeaderViewHolder.from(ITEM_VIEW_TYPE_ITEM_2, parent)
            ITEM_VIEW_TYPE_ITEM_2 -> FacilityViewHolder.from(parent)
            ITEM_VIEW_TYPE_FOOTER -> FooterViewHolder.from(ITEM_VIEW_TYPE_ITEM_2, parent)
            else -> throw ClassCastException("Unknown viewType $viewType")
        }
    }

    fun addFooterAndSubmitList(list: List<FacilityKt>?) {
        adapterScope.launch {
            val items = when (list) {
                null -> listOf(FacilityDataItem.Footer)
                else -> {
                    listOf(FacilityDataItem.Header) +
                    list.map {
                        FacilityDataItem.FacilityItem(it)
                    } + listOf(FacilityDataItem.Footer)
                }
            }

            withContext(Dispatchers.Main) {
                submitList(items)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is FacilityDataItem.Header -> ITEM_VIEW_TYPE_HEADER
            is FacilityDataItem.FacilityItem -> ITEM_VIEW_TYPE_ITEM_2
            is FacilityDataItem.Footer -> ITEM_VIEW_TYPE_FOOTER
        }
    }
}

sealed class FacilityDataItem {
    abstract val id: Long

    object Header: FacilityDataItem() {
        override val id = Long.MAX_VALUE
    }

    data class FacilityItem(val facility: FacilityKt): FacilityDataItem() {
        override val id = facility.id
    }

    object Footer: FacilityDataItem() {
        override val id = Long.MIN_VALUE
    }
}

class FacilityDiffCallback: DiffUtil.ItemCallback<FacilityDataItem>() {

    override fun areItemsTheSame(oldItem: FacilityDataItem, newItem: FacilityDataItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FacilityDataItem, newItem: FacilityDataItem): Boolean {
        return oldItem == newItem
    }
}
