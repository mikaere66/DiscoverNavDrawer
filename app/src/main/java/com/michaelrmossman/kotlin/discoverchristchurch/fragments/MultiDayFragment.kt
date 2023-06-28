package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.request.RequestOptions
import com.michaelrmossman.kotlin.discoverchristchurch.GlideApp
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentMultiDayBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MultiDayFragment: BaseFragment<FragmentMultiDayBinding>(R.layout.fragment_multi_day) {

    private var coastalPathwayRouteIds: List<Long>? = null
    private var craterRimRouteIds: List<Long>? = null
    private var headToHeadRouteIds: List<Long>? = null

    override fun onResume() {
        super.onResume() // Required

        setTitleSubtitle()

        viewModel.setCurrentRouteId(0L)
//        viewModel.setMultiRouteIds(listOf())
//        viewModel.setMultiRouteIndex(Int.MAX_VALUE)
        viewModel.setOverflowIconIndex(0)
        // viewModel.setRouteFilterTerm(null)
        // viewModel.setRoutesMenuVisible(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val index = sharedPrefs.getInt(SORT_LISTS_BY_PREF, 0)
//        viewModel.setOverflowIconIndex(index)

        lifecycleScope.launch {
            val chCh360ItemsCount = viewModel.getChCh360ItemsCount()

            val routes = viewModel.getRoutesList()

            val coastalPathwayRoutes =
                routes.filter { route ->
                    route.route.contains(MULTI_DAY_COASTAL_PATHWAY)
                }
            coastalPathwayRouteIds = coastalPathwayRoutes.map { route -> route.id }

            val craterRimRoutes =
                routes.filter { route ->
                    route.route.contains(MULTI_DAY_CRATER_RIM)
                }
            craterRimRouteIds = craterRimRoutes.map { route -> route.id }

            val headToHeadRoutes =
                routes.filter { route ->
                    route.route.contains(MULTI_DAY_HEAD_TO_HEAD)
                }
            headToHeadRouteIds = headToHeadRoutes.map { route -> route.id }

            binding.apply {
                if (chCh360ItemsCount > 0) {
                    val chCh360TipText = String.format(
                        getString(R.string.multi_day_tip_text_1),
                        chCh360ItemsCount
                    )
                    chCh360Layout.apply {
                        multiDayFragment = this@MultiDayFragment
                        chCh360TextTip.text = chCh360TipText
                    }
                }

                val coastalPathwayIndex = MULTI_DAY_COASTAL_ID
                coastalPathwayLayout.apply {
                    listIndex = coastalPathwayIndex
                    adjacentLogo.apply {
                        contentDescription =
                            getString(R.string.coastal_pathway_logo_descr)
                        val imageId =
                            getImageIdentifier(R.array.multi_day_logos,
                                coastalPathwayIndex.minus(1).toLong())
                        setLogoImage(imageId,this)
                    }
                    adjacentText.text = getString(R.string.coastal_pathway_text)

                    coastalPathwayRouteIds?.let { routeIds ->
                        // Only enable click listeners if list NOT empty
                        multiDayFragment = this@MultiDayFragment

                        val coastalPathwaySize = routeIds.size
                        val coastalPathwayTipText =
                            String.format(getString(R.string.multi_day_tip_text_2),
                                coastalPathwaySize)
                        adjacentText.append(coastalPathwayTipText)
                    }
                }

                val craterRimIndex = MULTI_DAY_CRATER_ID
                craterRimLayout.apply {
                    listIndex = craterRimIndex
                    adjacentLogo.apply {
                        contentDescription =
                            getString(R.string.crater_rim_logo_descr)
                        val imageId =
                            getImageIdentifier(R.array.multi_day_logos,
                                craterRimIndex.minus(1).toLong())
                        setLogoImage(imageId,this)
                    }
                    adjacentText.text = getString(R.string.crater_rim_text)

                    craterRimRouteIds?.let { routeIds ->
                        // Only enable click listeners if list NOT empty
                        multiDayFragment = this@MultiDayFragment

                        val craterRimSize = routeIds.size
                        val craterRimTipText =
                            String.format(getString(R.string.multi_day_tip_text_2),
                                craterRimSize)
                        adjacentText.append(craterRimTipText)
                    }
                }

                val headToHeadIndex = MULTI_DAY_HEADS_ID
                headToHeadLayout.apply {
                    listIndex = headToHeadIndex
                    adjacentLogo.apply {
                        contentDescription =
                            getString(R.string.head_to_head_logo_descr)
                        val imageId =
                            getImageIdentifier(R.array.multi_day_logos,
                                headToHeadIndex.minus(1).toLong())
                        setLogoImage(imageId,this)
                    }
                    adjacentText.text = getString(R.string.head_to_head_text)

                    headToHeadRouteIds?.let { routeIds ->
                        // Only enable click listeners if list NOT empty
                        multiDayFragment = this@MultiDayFragment

                        val headToHeadSize = routeIds.size
                        val headToHeadTipText =
                            String.format(getString(R.string.multi_day_tip_text_2),
                                headToHeadSize)
                        adjacentText.append(headToHeadTipText)
                    }
                }
            }
        }
    }

    fun goChCh360List() {
        navigateTo(1304603086) // ChCh360ListFragment
    }

    fun goChCh360Map(): Boolean {
        viewModel.setRoutesMenuVisible(false)
        navigateTo(1304603081) // ChCh362Activity
        return true // xml longClick
    }

    fun goMultiDayList(listIndex: Int) {
        val filterTerm = when (listIndex) {
            MULTI_DAY_COASTAL_ID -> MULTI_DAY_COASTAL_PATHWAY
            MULTI_DAY_CRATER_ID -> MULTI_DAY_CRATER_RIM
            else -> MULTI_DAY_HEAD_TO_HEAD
        }
        viewModel.setRouteFilterTerm(filterTerm)
        /* Reset currentAreaId to 0L to infer ALL
           routes, based on _routeFilterTerm */
        viewModel.setCurrentAreaId(0L)
        viewModel.setMultiRouteIndex(listIndex)
        navigateTo(1304613216) // MultiRouteFragment
    }

    fun goMultiDayMap(listIndex: Int): Boolean {
        val routeIds = when (listIndex) {
            MULTI_DAY_COASTAL_ID -> coastalPathwayRouteIds
            MULTI_DAY_CRATER_ID -> craterRimRouteIds
            else -> headToHeadRouteIds // MULTI_DAY_HEADS_ID
        }
        routeIds?.let { multiRouteIds ->
            viewModel.setMultiRouteIds(multiRouteIds)
            viewModel.setMultiRouteIndex(listIndex)
            // Set an empty Location to indicate NOT nearest
            // viewModel.setCurrentLocation(getEmptyLocation())
            viewModel.setRoutesMenuVisible(false)
            navigateTo(1304613211) // MultiDayActivity
        }
        return true // xml longClick
    }

    private fun setLogoImage(
        @DrawableRes imageId: Int, imageView: ImageView
    ) {
        val widthHeight = resources.getInteger(
            R.integer.adjacent_header_image_width_height
        )
        GlideApp.with(this)
            .load(imageId)
            .circleCrop()
            .apply(RequestOptions().override(widthHeight, widthHeight))
            .into(imageView)
    }

    private fun setTitleSubtitle() {
        val title = String.format(
            getString(R.string.app_title),
            getString(R.string.multi_day)
        )
        viewModel.setTitle(title)

        val subtitle = getString(R.string.adjacent_subtitle)
        viewModel.setSubtitle(subtitle)
    }
}
