package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
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

class FooterViewHolder(view: View): RecyclerView.ViewHolder(view) {

    companion object {

        private fun getFooterDrawableId(index: Int): Int? {
            return when (index) {
                ITEM_VIEW_TYPE_ITEM_1 -> R.drawable.ic_baseline_battery_2_bar_rotated_black_24
                ITEM_VIEW_TYPE_ITEM_2 -> R.drawable.ic_baseline_public_black_24
                ITEM_VIEW_TYPE_ITEM_3 -> R.drawable.ic_baseline_directions_walk_black_24
                ITEM_VIEW_TYPE_ITEM_4 -> R.drawable.ic_baseline_water_black_24
                ITEM_VIEW_TYPE_ITEM_5 -> R.drawable.ic_baseline_speaker_phone_black_24
                ITEM_VIEW_TYPE_ITEM_6 -> R.drawable.ic_baseline_local_florist_black_24
                ITEM_VIEW_TYPE_ITEM_7 -> R.drawable.ic_baseline_location_city_black_24
                ITEM_VIEW_TYPE_ITEM_8 -> R.drawable.ic_baseline_directions_bike_black_24
                ITEM_VIEW_TYPE_ITEM_9 -> R.drawable.ic_baseline_house_siding_black_24
                ITEM_VIEW_TYPE_ITEM_10 -> R.drawable.ic_baseline_emoji_nature_black_24
                ITEM_VIEW_TYPE_ITEM_11 -> R.drawable.ic_baseline_brush_black_24
                ITEM_VIEW_TYPE_ITEM_12 -> R.drawable.ic_baseline_toys_black_24
                else -> null
            }
        }

        fun from(index: Int, parent: ViewGroup): FooterViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(
                R.layout.community_footer, parent,false
            )
            getFooterDrawableId(index)?.let { drawableId ->
                val footerImage: ImageView =
                    view.findViewById(R.id.footer_image)
                footerImage.setImageResource(drawableId)
            }
            return FooterViewHolder(view)
        }
    }
}

