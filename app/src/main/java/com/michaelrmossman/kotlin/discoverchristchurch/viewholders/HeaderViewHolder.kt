package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12

class HeaderViewHolder(view: View): RecyclerView.ViewHolder(view) {

    companion object {

        private fun getHeaderStringId(index: Int): Int? {
            return when (index) {
                ITEM_VIEW_TYPE_ITEM_1 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_2 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_3 -> R.string.header_mixed_purpose
                ITEM_VIEW_TYPE_ITEM_4 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_5 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_6 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_7 -> R.string.header_dual_purpose
                ITEM_VIEW_TYPE_ITEM_8 -> R.string.header_dual_purpose
                ITEM_VIEW_TYPE_ITEM_9 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_10 -> R.string.header_single_purpose
                ITEM_VIEW_TYPE_ITEM_11 -> R.string.header_multi_purpose
                ITEM_VIEW_TYPE_ITEM_12 -> R.string.header_dual_purpose
                else -> null
            }
        }

        fun from(index: Int, parent: ViewGroup): HeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.community_header, parent,false
            )
            getHeaderStringId(index)?.let { stringId ->
                val headerText: TextView =
                    view.findViewById(R.id.header_text)
                headerText.text = parent.context.getString(stringId)
            }
            return HeaderViewHolder(view)
        }
    }
}

