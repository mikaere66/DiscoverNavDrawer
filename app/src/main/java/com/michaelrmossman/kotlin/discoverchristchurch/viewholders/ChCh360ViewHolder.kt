package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ChCh360ItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360Listener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.ChCh360LongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier

class ChCh360ViewHolder private constructor(
    private val binding: ChCh360ItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        item: ChCh360Kt,
        chCh360Listener: ChCh360Listener,
        chCh360LongListener: ChCh360LongListener
    ) {
        binding.apply {
            chCh360 = item
            checkBox = chCh360Favourite
            clickListener = chCh360Listener
            longClickListener = chCh360LongListener

            val imageId =
                getImageIdentifier(R.array.ch_ch_360_logos, (0).toLong())
            val integerId = R.integer.ch_ch_360_logo_image_width_height
            val widthHeight = context.resources.getInteger(integerId)
            GlideApp.with(context)
                .load(imageId)
                .circleCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(chCh360LogoSmall)

            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): ChCh360ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ChCh360ItemBinding.inflate(layoutInflater, parent,false)
            return ChCh360ViewHolder(binding, parent.context)
        }
    }
}
