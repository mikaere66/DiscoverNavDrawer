package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentLegendBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleBold
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleBoldMulti
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LegendFragment: Fragment() {

    private val viewModel: DiscoverViewModel by activityViewModels()
    private var _binding: FragmentLegendBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setSubtitle(getString(R.string.icons_legend))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding = FragmentLegendBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.legendFragment = this

        setSpannableStyleDistanceDuration()
        setSpannableStyleIndicatorTypes()
//        setSpannableStyleListView()
//        setSpannableStyleMapsView()
    }

    fun jumpAround(jumpDown: Boolean) { // Called from xml
        val direction =
            when (jumpDown) {
                true -> View.FOCUS_DOWN
                else -> View.FOCUS_UP
            }
        with(binding.legendScrollView) {
            post {
                this.fullScroll(direction)
            }
        }
    }

    private fun setSpannableStyleDistanceDuration() {
        val string = getString(R.string.journey_extended_note_b)
        val spannable =
            getSpannableStyleBold(string, R.string.journey_extended_note_a)
        binding.legendItemTextExtended.text = spannable
    }

    private fun setSpannableStyleIndicatorTypes() {
        val string1b = getString(R.string.icon_intro_1b)
        val stringId1a = R.string.icon_intro_1a
        val string2b = getString(R.string.icon_intro_2b)
        val stringId2a = R.string.icon_intro_2a
//        binding.apply {
//            recyclerView.adapter = adapter
//            footerLayout.walksText.text =
//                getSpannableStyleBoldMulti(
//                    string1b, stringId1a,
//                    string2b, stringId2a
//                )
//        }

//        val string1b = getString(R.string.icon_intro_1b)
//        val string2b = getString(R.string.icon_intro_2b)
//        val spannableStringBuilder = SpannableStringBuilder()
//        val spannable1 =
//            getSpannableStyleBold(string1b, R.string.icon_intro_1a)
//        spannableStringBuilder.append(spannable1)
//        spannableStringBuilder.append(SpannableString(" "))
//        val spannable2 =
//            getSpannableStyleBold(string2b, R.string.icon_intro_2a)
//        spannableStringBuilder.append(spannable2)
//        binding.legendIntroText.text = getSpannableStyleBoldMulti
        binding.legendIntroText.text =
            getSpannableStyleBoldMulti(
                string1b, stringId1a,
                string2b, stringId2a
            )
    }

//    private fun setSpannableStyleListView() {
//        val string = getString(R.string.legend_linear_b)
//        val spannable =
//            getSpannableStyleBold(string, R.string.legend_linear_a)
//        binding.legendItemText03.text = spannable
//    }

//    private fun setSpannableStyleMapsView() {
//        val string = getString(R.string.legend_grid_b)
//        val spannable =
//            getSpannableStyleBold(string, R.string.legend_grid_a)
//        binding.legendItemText02.text = spannable
//    }
}
