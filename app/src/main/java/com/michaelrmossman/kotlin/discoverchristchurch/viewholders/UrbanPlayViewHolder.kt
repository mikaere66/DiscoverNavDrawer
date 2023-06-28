package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.bumptech.glide.request.RequestOptions
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.UrbanPlayItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.UrbanPlayLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier

class UrbanPlayViewHolder private constructor(
    private val binding: UrbanPlayItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        urbanPlayItem: UrbanPlayKt,
        urbanPlayListener: UrbanPlayListener,
        urbanPlayLongListener: UrbanPlayLongListener
    ) {
        binding.apply {
            checkBox = urbanPlayFavourite
            clickListener = urbanPlayListener
            longClickListener = urbanPlayLongListener
            urbanPlay = urbanPlayItem

            val imageId =
                getImageIdentifier(R.array.urban_play_images, urbanPlayItem.id)
            val integerId = R.integer.community_logo_image_width_height
            val widthHeight = context.resources.getInteger(integerId)
            GlideApp.with(context)
                .load(imageId)
                .circleCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(urbanPlayLogoSmall)

            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): UrbanPlayViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                UrbanPlayItemBinding.inflate(layoutInflater, parent,false)
            return UrbanPlayViewHolder(binding, parent.context)
        }
    }
}
