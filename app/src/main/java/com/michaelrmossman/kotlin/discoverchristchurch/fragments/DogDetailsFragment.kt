package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentDogDetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_POSITION_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.applyConstraintSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogDetailsFragment: DetailedFragment<FragmentDogDetailsBinding>(R.layout.fragment_dog_details) {

    private var currentDogPark: DogParkKt? = null

    companion object {
        fun create(position: Int) =
            DogDetailsFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_POSITION_EXTRA, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(FRAGMENT_POSITION_EXTRA)?.let { position ->

            if (viewModel.dogParksList.isNotEmpty()) {
                currentDogPark = viewModel.dogParksList[position]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentDogPark?.let { dogPark ->
            binding.dogPark = dogPark

            dogPark.linkedIds?.let { ids ->
                val linkedIds: List<Long> =
                    ids.split(",").map { it.trim().toLong() }
                if (linkedIds.isNotEmpty()) {
                    binding.apply {
                        dogDetailsFragment = this@DogDetailsFragment
                        dogParkLinkedText.isVisible = true
                        dogParkToggleButton.isVisible = true
                    }

                    val constraintLayout = binding.dogParkLinkedConstraintLayout
                    /* IllegalStateException: Page(s) contain a ViewGroup with a
                       LayoutTransition (or animateLayoutChanges="true"), which
                       interferes with the scrolling animation. Make sure to call
                       getLayoutTransition().setAnimateParentHierarchy(false) on all
                       ViewGroups with a LayoutTransition before an animation is started */
                    constraintLayout.apply {
                        isVisible = true
                        layoutTransition.setAnimateParentHierarchy(false)
                    }

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)

                    lifecycleScope.launch {
                        val textViewIds = // Suspend function
                            getTextViews(constraintLayout, linkedIds)

                        binding.dogParkToggleGroup.referencedIds =
                            textViewIds.toIntArray()

                        applyConstraintSet(
                            constraintLayout,
                            constraintSet,
                            textViewIds
                        )
                    }
                }
            }
        }
    }

    override fun processOnClick() {
        activity?.let {
            toggleLinkedRoutes(
                it,
                binding.dogParkToggleGroup,
                binding.dogParkToggleButton
            )
        }
    }

    override fun setLinkedText(count: Int, shown: Boolean) {
        val quantityStringId =
            when (shown) {
                true -> R.plurals.linked_text_hide
                else -> R.plurals.linked_text_show
            }
        binding.dogParkLinkedText.text =
            resources.getQuantityString(
                quantityStringId,
                count
            )
    }
}
