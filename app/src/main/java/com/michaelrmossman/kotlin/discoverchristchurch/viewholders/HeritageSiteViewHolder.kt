package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.HeritageSiteItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.HeritageSiteLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier

class HeritageSiteViewHolder private constructor(
    private val binding: HeritageSiteItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        heritageSiteItem: HeritageSiteKt,
        heritageSiteListener: HeritageSiteListener,
        heritageSiteLongListener: HeritageSiteLongListener
    ) {
        binding.apply {
            checkBox = heritageSiteFavourite
            heritageSite = heritageSiteItem
            clickListener = heritageSiteListener
            longClickListener = heritageSiteLongListener

            val imageId =
                getImageIdentifier(R.array.heritage_site_images, heritageSiteItem.id)
            val integerId = R.integer.community_logo_image_width_height
            val widthHeight = context.resources.getInteger(integerId)
            GlideApp.with(context)
                .load(imageId)
                .circleCrop()
                .apply(RequestOptions().override(widthHeight, widthHeight))
                .into(heritageSiteImage)

            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): HeritageSiteViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                HeritageSiteItemBinding.inflate(layoutInflater, parent,false)
            return HeritageSiteViewHolder(binding, parent.context)
        }
    }
}
