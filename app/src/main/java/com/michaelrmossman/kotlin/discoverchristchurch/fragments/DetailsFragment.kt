package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentDetailsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_POSITION_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.applyConstraintSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment: DetailedFragment<FragmentDetailsBinding>(R.layout.fragment_details) {

    private var currentRoute: RouteKt? = null

    companion object {
        fun create(position: Int) =
            DetailsFragment().apply {
                arguments = Bundle(1).apply {
                    putInt(FRAGMENT_POSITION_EXTRA, position)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getInt(FRAGMENT_POSITION_EXTRA)?.let { position ->

            if (viewModel.routesKtList.isNotEmpty()) {
                currentRoute = viewModel.routesKtList[position]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        currentRoute?.let { route ->
            binding.route = route

            route.linkedIds?.let { ids ->
                val linkedIds: List<Long> =
                    ids.split(",").map { it.trim().toLong() }
                if (linkedIds.isNotEmpty()) {
                    binding.apply {
                        detailsFragment = this@DetailsFragment
                        detailsLinkedText.isVisible = true
                        detailsToggleButton.isVisible = true
                    }

                    val constraintLayout = binding.detailsLinkedConstraintLayout
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

                        binding.detailsToggleGroup.referencedIds =
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
                binding.detailsToggleGroup,
                binding.detailsToggleButton
            )
        }
    }

    override fun setLinkedText(count: Int, shown: Boolean) {
        val quantityStringId =
            when (shown) {
                true -> R.plurals.linked_text_hide
                else -> R.plurals.linked_text_show
            }
        binding.detailsLinkedText.text =
            resources.getQuantityString(
                quantityStringId,
                count
            )
    }
}
