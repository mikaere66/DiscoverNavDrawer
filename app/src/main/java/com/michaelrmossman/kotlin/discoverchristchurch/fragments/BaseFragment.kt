package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.NavOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BatteryRecyclerKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.BikeTrackKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Kt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DrinkFountainKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FreeWiFiKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitTypeKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.HeritageSiteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.UrbanPlayKt
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.FabOnScrollListener
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.NavUtils.navigateTo
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.enableDisableDialogButton
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getImageIdentifier
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goFullscreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.setSortMenu
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.setSortOrderIcon
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFavouritesMessagePair
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

// AndroidEntryPoint-annotated classes cannot have type parameters
abstract class BaseFragment<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : Fragment() {

    /* See getCurrentLocation().addOnSuccessListener
       in SearchFragment re weird naming convention */
    private var _binding: T? = null
    val binding: T
        get() = _binding!!

    val viewModel: DiscoverViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) : View {
        _binding = DataBindingUtil.inflate(
            inflater, layoutResId, container,false
        )
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

    fun getCheckedCount(checkBoxes: List<CheckBox>): Int {
        var checkedCount = 0
        checkBoxes.forEach { checkBox ->
            if (checkBox.isChecked) {
                checkedCount = checkedCount.plus(1)
            }
        }
        return checkedCount
    }

    private fun getLinkedIds(linkedIds: String): List<Long> {
        return linkedIds.split(",").map { it.trim().toLong() }
    }

    fun getOverflowIconIndex(routes: Boolean = true): Int {
        val sortByPref = when (routes) {
            true -> SORT_LISTS_BY_PREF
            else -> SORT_BIKES_BY_PREF
        }
        return sharedPrefs.getInt(sortByPref, 0)
    }

    fun goBatteryRecyclerSingle(
        batteryRecycler: BatteryRecyclerKt, index: Int
    ) {
        viewModel.setCurrentBatteryRecyclerId(batteryRecycler.id)
        navigateTo(index) // BatteryRecyclersActivity
    }

    fun goBikeTrackSingle(bikeTrack: BikeTrackKt, index: Int) {
        viewModel.setCurrentBikeTrackId(bikeTrack.id)
        navigateTo(index) // BikeDetails|BikeTracks activities
    }

    fun goChCh360Details(chCh360: ChCh360Kt, index: Int) {
        viewModel.setChCh360ItemId(chCh360.id)
        navigateTo(index) // ChCh360Activity
    }

    fun goChCh360Maps(chCh360: ChCh360Kt, index: Int) {
        viewModel.setChCh360ItemId(chCh360.id)
        viewModel.setRoutesMenuVisible(false)
        navigateTo(index) // ChCh361Activity
    }

    fun goConvenienceSingle(convenience: ConvenienceKt, index: Int) {
        viewModel.setCurrentConvenienceId(convenience.id)
        navigateTo(index)
    }

    // DOG PARKS

    fun goDogPark(dogPark: DogParkKt, indices: List<Int>) {
        when (dogPark.dogInfo) {
            null -> {
                when (dogPark.linkedIds) {
                    null -> showToast()
                    else -> {
                        val linkedIds = getLinkedIds(dogPark.linkedIds)
                        when (linkedIds.size) { // DetailsActivity
                            1 -> goDogParkSingle(indices[0], linkedIds[0])
                            else -> { // DogLinksFragment
                                goDogParkMulti(indices[1], linkedIds)
                            }
                        }
                    }
                }
            }
            else -> {
                viewModel.setCurrentDogParkId(dogPark.id)
                navigateTo(indices[2]) // DogDetailsActivity
            }
        }
    }

    private fun goDogParkMulti(index: Int, linkedIds: List<Long>) {
        viewModel.setMultiRouteIds(linkedIds)
        viewModel.setMultiRouteIndex(Int.MIN_VALUE)
        navigateTo(index, findNavController()) // DogLinksFragment
    }

    private fun goDogParkSingle(index: Int, linkedId: Long) {
        viewModel.setCurrentRouteId(linkedId)
        navigateTo(index, findNavController()) // DetailsActivity
    }

    fun goEnvironmentMulti(index: Int) {
        viewModel.setCurrentDogParkId(0L)
        navigateTo(index, findNavController()) // DogParksActivity
    }

    fun goEnvironmentSingle(dogPark: DogParkKt, index: Int) {
        viewModel.setCurrentDogParkId(dogPark.id)
        navigateTo(index, findNavController()) // DogParksActivity
    }

    // END DOG PARKS

    fun goFacilitySingle(facility: FacilityKt, index: Int) {
        viewModel.setCurrentFacilityId(facility.id)
        navigateTo(index) // FacilitiesActivity
    }

    fun goFountainSingle(fountain: DrinkFountainKt, index: Int) {
        viewModel.setCurrentFountainId(fountain.id)
        navigateTo(index) // FountainsActivity
    }

    fun goFreeWiFiSingle(freeWiFi: FreeWiFiKt, index: Int) {
        viewModel.setCurrentFreeWiFiId(freeWiFi.id)
        navigateTo(index) // FreeWiFiActivity
    }

    fun goFruitTypeSingle(fruitType: FruitTypeKt, index: Int) {
        viewModel.setCurrentFruitTypeId(fruitType.id)
        navigateTo(index, findNavController()) // FruitTreesActivity
    }

    fun goHeritageSiteSingle(heritageSite: HeritageSiteKt, index: Int) {
        viewModel.setCurrentHeritageSiteId(heritageSite.id)
        navigateTo(index) // HeritageSitesActivity
    }

    fun goHome() {
        val navController = findNavController()
        val home = navController.graph.startDestinationId
        val options = NavOptions.Builder().setPopUpTo(home,false).build()
        navController.navigate(home,null, options)
    }

    fun goParkSingle(index: Int, park: ParkKt) {
        viewModel.setCurrentParkId(park.id)
        navigateTo(index) // ParksActivity
    }

    fun goStreetArtSingle(index: Int, streetArt: StreetArtKt) {
        viewModel.setCurrentStreetArtId(streetArt.id)
        navigateTo(index) // StreetDetails|StreetArt activities
    }

    fun goUrbanPlaySingle(index: Int, urbanPlay: UrbanPlayKt) {
        viewModel.setCurrentUrbanPlayId(urbanPlay.id)
        navigateTo(index) // UrbanPlayActivity
    }

    fun navigateTo(index: Int, route: RouteKt? = null) {
        when (route) {
            null ->  navigateTo(index, findNavController())
            else -> {
                viewModel.setCurrentRouteId(route.id)
                navigateTo(index, findNavController(), route.round)
            }
        }
    }

    // Only used by various community fragments
    fun processCheckChangeListener(
        eFab: ExtendedFloatingActionButton,
        fab: FloatingActionButton, checkBoxes: List<CheckBox>,
        isChecked: Boolean, selectAll: CheckBox,
        recyclerView: RecyclerView
    ) {
        when (isChecked) {
            true -> {
                var allChecked = true
                checkBoxes.forEach { checkBox ->
                    // Break on first false, if applicable
                    if (!checkBox.isChecked) {
                        allChecked = false
                        return@forEach
                    }
                }
                selectAll.isChecked = allChecked
                enableDisableDialogButton(true)
                setBackToTopFab(eFab, fab, recyclerView)
            }
            else -> {
                val checkedCount = getCheckedCount(checkBoxes)
                selectAll.apply {
                    this.isChecked = false
                    isEnabled = true
                }
                enableDisableDialogButton(checkedCount > 0)
                setBackToTopFab(eFab, fab, recyclerView)
            }
        }
    }

    fun scrollToPosition(recyclerView: RecyclerView) {
        val layoutManager = recyclerView.layoutManager
        val position =
            (layoutManager as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
        when (position) {
            in 0..99 -> {
                recyclerView.smoothScrollToPosition(0)
            }
            else -> {
                recyclerView.scrollToPosition(SCROLL_TO_THRESHOLD)
                recyclerView.smoothScrollToPosition(0)
            }
        }
    }

    // Used by various community fragments, plus ListFragment
    fun setBackToTopFab(
        eFab: ExtendedFloatingActionButton?,
        fab: FloatingActionButton,
        recyclerView: RecyclerView
    ) {
        val layoutManager = recyclerView.layoutManager
        val visible = (layoutManager as LinearLayoutManager)
            .findFirstCompletelyVisibleItemPosition() > SCROLL_TO_THRESHOLD
        fab.isVisible = visible
        eFab?.let {
            when (visible) {
                true -> it.shrink()
                else -> it.extend()
            }
        }
    }

    fun setBottomBar(bottomBar: AnimatedBottomBar, index: Int) {
        bottomBar.apply {
            visibility = View.VISIBLE
            selectTabAt(index)
            setOnTabSelectListener(object: AnimatedBottomBar.OnTabSelectListener {
                override fun onTabSelected(
                    lastIndex: Int,
                    lastTab: AnimatedBottomBar.Tab?,
                    newIndex: Int,
                    newTab: AnimatedBottomBar.Tab
                ) {
                    when (newIndex) {
                        2 -> findNavController().popBackStack()
                        1 -> {
                            when (viewModel.currentPlaceId) {
                                0L -> findNavController().popBackStack()
                                else -> {
                                    viewModel.setCurrentAreaId(0L)
                                    viewModel.setCurrentPlaceId(0L)
                                    // RoutesFragment to AreasFragment
                                    navigateTo(1815601186)
                                }
                            }
                        }
                        else -> { // Home
                            when (viewModel.currentAreaId) {
                                0L -> findNavController().popBackStack()
                                else -> {
                                    when (viewModel.currentPlaceId) {
                                        0L -> navigateTo(-1)
                                        else -> navigateTo(-2)
                                    }
                                }
                            }
                        }
                    }
                }
            })
        }
    }

    fun setFabsBasic(
        eFab: ExtendedFloatingActionButton,
        fab: FloatingActionButton,
        recyclerView: RecyclerView,
        selection: Int = 0, // Count minus total
        zeroResults: Boolean = false /* Search returns an empty
        list. Only applicable to Facilities|Parks fragments */
    ) {
        eFab.apply {
            isEnabled = !zeroResults
            text = getString(
                when (selection) {
                    0 -> R.string.extended_fab_basic_all_items // Not filtered
                    else -> R.string.extended_fab_basic_selection // Less than 0
                }
            )
        }

        recyclerView.addOnScrollListener(
            FabOnScrollListener(
                { setBackToTopFab(eFab, fab, recyclerView) }, eFab
            )
        )
    }

    fun setFabsExtra(
        eFab: ExtendedFloatingActionButton,
        fab: FloatingActionButton,
        recyclerView: RecyclerView,
        selection: Int = 0, // Count minus total
        zeroResults: Boolean = false /* Search returns an
        empty list. Only applicable to StreetArtFragment */
    ) {
        eFab.apply {
            isEnabled = !zeroResults
            text = getString(
                when (selection) {
                    0 -> R.string.extended_fab_extra_all_items // Not filtered
                    else -> R.string.extended_fab_extra_selection // Less than 0
                }
            )
        }

        recyclerView.addOnScrollListener(
            FabOnScrollListener(
                { setBackToTopFab(eFab, fab, recyclerView) }, eFab
            )
        )
    }

    private fun setSortBy(index: Int, routes: Boolean) {
        val sortByPref = when (routes) {
            true -> SORT_LISTS_BY_PREF
            else -> SORT_BIKES_BY_PREF
        }
        if (sharedPrefs.edit().putInt(sortByPref, index).commit()) {
            when (routes) {
                true -> viewModel.setSortListsBy(index)
                else -> viewModel.setSortBikesBy(index)
            }
            setSortByIcon(index, routes)
        }
    }

    private fun setSortByIcon(index: Int, routes: Boolean) {
        viewModel.setOverflowIconIndex(
            when (routes) {
                true -> index.plus(100)
                else -> index.plus(200)
            }
        )
    }

    fun setSortMenu(
        lifecycleOwner: LifecycleOwner,
        routes: Boolean = true
    ) {
        val sortByPref: String
        val sortOrderPref: String
        val menuHost: MenuHost = requireActivity()
        val menuRes = when (routes) {
            true -> {
                sortByPref = SORT_LISTS_BY_PREF
                sortOrderPref = SORT_ORDER_ASC_PREF
                R.menu.menu_areas_places_routes
            }
            else -> {
                sortByPref = SORT_BIKES_BY_PREF
                sortOrderPref = SORT_BIKES_ASC_PREF
                R.menu.menu_bike_tracks_fragment
            }
        }
        setSortMenu(lifecycleOwner,
            menuHost, menuRes,
            ::setSortBy, ::setSortOrder,
            sortByPref, sortOrderPref
        )
    }

    private fun setSortOrder(routes: Boolean) {
        val sortOrderPref = when (routes) {
            true -> SORT_ORDER_ASC_PREF
            else -> SORT_BIKES_ASC_PREF
        }
        val ascending = sharedPrefs.getBoolean(sortOrderPref, true)
        if (sharedPrefs.edit().putBoolean(sortOrderPref,
                !ascending).commit()) {
            when (routes) {
                true -> viewModel.setSortOrder(!ascending)
                else -> viewModel.setSortBikes(!ascending)
            }
            if (isLandscape()) { setSortOrderIcon(!ascending) }
        }
    }

    fun showHeritageSiteImage(heritageSite: HeritageSiteKt) {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.heritage_site_images, heritageSite.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, heritageSite.name, heritageSite.landscape
            )
        }
    }

    fun showStreetArtImage(streetArt: StreetArtKt) {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.street_art_images, streetArt.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, streetArt.title, streetArt.landscape
            )
        }
    }

    fun showUrbanPlayImage(urbanPlay: UrbanPlayKt) {
        activity?.let { fragmentActivity ->
            val imageId =
                getImageIdentifier(R.array.urban_play_images, urbanPlay.id)
            goFullscreen(
                fragmentActivity as AppCompatActivity,
                imageId, urbanPlay.name, urbanPlay.landscape
            )
        }
    }

    fun showToast() {
        fancyToast(
            requireContext(),4, R.string.no_additional_info
        )
    }

    fun toggleFave(checked: Boolean, itemId: Long, itemType: Int) {
        lifecycleScope.launch {
            val result = viewModel.toggleFavourite(checked, itemId, itemType)
            val messagePair = getFavouritesMessagePair(result)
            fancyToast(requireContext(), messagePair.first, messagePair.second)
        }
    }

    fun toggleFaveRoute(checked: Boolean, itemId: Long) {
        toggleFave(checked, itemId, ITEM_VIEW_TYPE_ITEM_01)
    }
}
