package com.michaelrmossman.kotlin.discoverchristchurch.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.StreetArtItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtListener
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.StreetArtLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier

class StreetArtViewHolder private constructor(
    private val binding: StreetArtItemBinding,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        streetArtItem: StreetArtKt,
        streetArtListener: StreetArtListener,
        streetArtLongListener: StreetArtLongListener
    ) {
        binding.apply {
            checkBox = streetArtFavourite
            clickListener = streetArtListener
            longClickListener = streetArtLongListener
            streetArt = streetArtItem

            val imageId =
                getImageIdentifier(R.array.street_art_images, streetArtItem.id)
            GlideApp.with(context)
                .load(imageId)
                .into(streetArtThumbnail)

            executePendingBindings()
        }
    }

    companion object {
        fun from(parent: ViewGroup): StreetArtViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding =
                StreetArtItemBinding.inflate(layoutInflater, parent,false)
            return StreetArtViewHolder(binding, parent.context)
        }
    }
}
