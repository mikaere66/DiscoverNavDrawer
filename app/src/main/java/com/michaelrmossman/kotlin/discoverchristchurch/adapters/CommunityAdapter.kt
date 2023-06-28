package com.michaelrmossman.kotlin.discoverchristchurch.adapters

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.CommunityItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.CommunityItem

class CommunityAdapter(
    private val communityListener: CommunityListener
) : ListAdapter<CommunityItem, CommunityAdapter.ViewHolder>(CommunityItemDiffCallback()) {

    private var resetViews = false // Value that will change

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), communityListener)
        holder.resetViews(resetViews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    fun resetViews(resetViews: Boolean) {
        this.resetViews = resetViews
        currentList.forEachIndexed { i, _ ->
            notifyItemChanged(i, resetViews)
        }
    }

    class ViewHolder private constructor(
        private val binding: CommunityItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var invisibleToVisible: ObjectAnimator
        private lateinit var visibleToInvisible: ObjectAnimator
        private var flippedViews: HashMap<Int, Boolean> = HashMap()
        private var resetViews = false // Triggers the change

        fun bind(
            communityItem: CommunityItem,
            communityListener: CommunityListener
        ) {
            binding.communityItem = communityItem
            binding.apply {
                clickListener = communityListener
                communityConstraintLayout1.setOnLongClickListener {
                    flipViews()
                    true
                }
                communityConstraintLayout2.setOnClickListener { flipViews() }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = CommunityItemBinding.inflate(layoutInflater, parent,false)
                return ViewHolder(binding)
            }
        }

        fun resetViews(resetViews: Boolean) {
            this.resetViews = resetViews
            for (flipped in flippedViews.values) {
                if (flipped) flipViews()
            }
            this.resetViews = false
        }

        private fun flipViews() {
            val visibleItem: MaterialCardView
            val invisibleItem: MaterialCardView
            val visibleItemEnd: Float
            val invisibleItemStart: Float
            val position = absoluteAdapterPosition

            when (binding.itemCardView2.isVisible || resetViews) {
                true -> {
                    // Back --> Front
                    visibleItem = binding.itemCardView2
                    invisibleItem = binding.itemCardView1
                    visibleItemEnd = 90F
                    invisibleItemStart = -90F
                    flippedViews[position] = false
                }
                else -> {
                    // Front <-- Back
                    visibleItem = binding.itemCardView1
                    invisibleItem = binding.itemCardView2
                    visibleItemEnd = -90F
                    invisibleItemStart = 90F
                    flippedViews[position] = true
                }
            }

            val accelerator: Interpolator = AccelerateInterpolator()
            val decelerator: Interpolator = DecelerateInterpolator()
            val flipDuration = 250L

            visibleToInvisible =
                ObjectAnimator.ofFloat(
                    visibleItem,"rotationY",
                    0F, visibleItemEnd
                )
            visibleToInvisible.duration = flipDuration
            visibleToInvisible.interpolator = accelerator

            invisibleToVisible =
                ObjectAnimator.ofFloat(
                    invisibleItem,"rotationY",
                    invisibleItemStart, 0F
                )
            invisibleToVisible.duration = flipDuration
            invisibleToVisible.interpolator = decelerator

            visibleToInvisible.addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(anim: Animator) {
                    visibleItem.visibility = View.GONE

                    invisibleToVisible.start()
                    invisibleItem.visibility = View.VISIBLE
                }
            })

            visibleToInvisible.start()
        }
    }
}

class CommunityItemDiffCallback: DiffUtil.ItemCallback<CommunityItem>() {
    override fun areItemsTheSame(oldItem: CommunityItem, newItem: CommunityItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CommunityItem, newItem: CommunityItem): Boolean {
        return oldItem == newItem
    }
}

class CommunityListener(val clickListener: (communityItem: CommunityItem) -> Unit) {
    fun onClick(communityItem: CommunityItem) = clickListener(communityItem)
}
