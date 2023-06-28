package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.LinkedItemBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.FRAGMENT_INFO_TAG
import com.michaelrmossman.kotlin.discoverchristchurch.utils.getReferencedViews
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import kotlinx.coroutines.launch

// AndroidEntryPoint-annotated classes cannot have type parameters
abstract class DetailedFragment<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment() {

    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    val viewModel: DiscoverViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding =
            DataBindingUtil.inflate(inflater, layoutResId, container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
    }

    suspend fun getTextViews(
        constraintLayout: ConstraintLayout, linkedIds: List<Long>
    ) : List<Int> {
        val textViewIds = mutableListOf<Int>()

        linkedIds.forEach { id ->
            val linkedRoute = viewModel.getRouteKtById(id)
            val textView = getTextView(linkedRoute)
            val textViewId = View.generateViewId()
            textViewIds.add(textViewId)
            textView.id = textViewId
            constraintLayout.addView(textView)
        }

        setLinkedText(linkedIds.size,false)

        return textViewIds
    }

    private fun getTextView(linkedRoute: RouteKt): TextView {
        val linkedItemBinding =
            LinkedItemBinding.inflate(layoutInflater)
        return linkedItemBinding.apply {
            detailedFragment = this@DetailedFragment
            route = linkedRoute
        }.root as TextView
    }

    open fun processOnClick() {
        /* See Details|DogDetails fragments */
    }

    open fun setLinkedText(count: Int, shown: Boolean) {
        /* See Details|DogDetails fragments */
    }

    fun showInfoFragment(linkedRoute: RouteKt) {
        /* Although the linked route ID won't change,
           it's favourite status MAY have, so we need
           to re-initialise viewModel linkedRoute */
        activity?.let {
            lifecycleScope.launch {
                val route =
                    viewModel.getRouteKtById(linkedRoute.id)
                viewModel.setLinkedRoute(route)
                InfoDetailFragment().show(
                    it.supportFragmentManager,
                    FRAGMENT_INFO_TAG
                )
            }
        }
    }

    fun toggleLinkedRoutes(
        context: Context, group: Group, toggleButton: View
    ) {
        val quantity = group.getReferencedViews().size
        toggleButton.startAnimation(
            when (group.isVisible) {
                false -> { // Was at END of function
                    setLinkedText(quantity,true)
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_clockwise
                    )
                }
                else -> {
                    setLinkedText(quantity,false)
                    AnimationUtils.loadAnimation(
                        context,
                        R.anim.rotate_anticlockwise
                    )
                }
            }
        )

        val slideIn = Slide()
        slideIn.slideEdge = Gravity.BOTTOM
        slideIn.duration =
            when (group.isVisible) {
                true -> resources.getInteger(
                    R.integer.toggle_close_anim_duration
                ).toLong()
                else -> resources.getInteger(
                    R.integer.toggle_open_anim_duration
                ).toLong()
            }

        slideIn.mode =
            when (group.isVisible) {
                true -> Slide.MODE_OUT
                else -> Slide.MODE_IN
            }

        slideIn.addTarget(group)

        TransitionManager.beginDelayedTransition(
            binding.root as ViewGroup, slideIn
        )

        // Must use "with"
        with (group) {
            visibility =
                if (isVisible) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
        }
    }
}
