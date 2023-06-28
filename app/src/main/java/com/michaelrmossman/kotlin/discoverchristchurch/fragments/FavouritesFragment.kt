package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FavesDataItem
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FavouritesAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Favourite
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_00
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_01
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_1
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_3
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_4
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_5
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_6
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_7
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_8
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_10
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_11
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_12
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavouritesFragment: BaseFragment<FragmentCommonBinding>(R.layout.fragment_common) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setSubtitle(getString(R.string.faves_subtitle))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_favourites_fragment, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_clear -> {
                        confirmClearAll()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.CREATED)

        val adapter = FavouritesAdapter(
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_1)
                }
            },
            BatteryRecyclerLongListener { batteryRecyclerKt ->
                goBatteryRecyclerSingle(batteryRecyclerKt,601602181) // BatteryRecyclersActivity
                true
            },
            BikeTrackListener { bikeTrack, _, index ->
                when (index) {
                    0 ->  goBikeTrackSingle(bikeTrack,601602041) // BikeDetailsActivity
                    else -> toggleFave(false, bikeTrack.id, ITEM_VIEW_TYPE_ITEM_8)
                }
            },
            BikeTrackLongListener { bikeTrack ->
                goBikeTrackSingle(bikeTrack,601602201) // BikeTrackActivity
                true
            },
            ChCh360Listener { chCh360, _, index ->
                when (index) {
                    0 ->  goChCh360Details(chCh360,601603080) // Details viewPager
                    else -> toggleFave(false, chCh360.id, ITEM_VIEW_TYPE_ITEM_00)
                }
            },
            ChCh360LongListener { chCh360 ->
                goChCh360Maps(chCh360,601603081) // Single chCh360 map
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_9)
                }
            },
            ConvenienceLongListener { convenience ->
                goConvenienceSingle(convenience,601616031) // ConveniencesActivity
                true
            },
            DogParkListener { _, dogPark, index ->
                when (index) { // DogDetailsActivity, DogLinksFragment, DetailsActivity
                    0 -> goDogPark(dogPark, listOf(601504050, 601504126, 601504051))
                    else -> toggleFave(false, dogPark.id, ITEM_VIEW_TYPE_ITEM_3)
                }
            },
            DogParkLongListener { dogPark ->
                goEnvironmentSingle(dogPark,601504161) // DogParksActivity
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_2)
                }
            },
            FacilityLongListener { facility ->
                goFacilitySingle(facility,601506011) // FacilitiesActivity
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_4)
                }
            },
            FountainLongListener { fountain ->
                goFountainSingle(fountain,601506151) // FountainsActivity
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_5)
                }
            },
            FreeWiFiLongListener { freeWiFi ->
                goFreeWiFiSingle(freeWiFi,601506231) // FreeWiFiActivity
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_6)
                }
            },
            FruitTypeLongListener { fruitTypeKt ->
                goFruitTypeSingle(fruitTypeKt,601506181) // FruitTreesActivity
                true
            },
            HeritageSiteListener { _, heritageSite, index ->
                when (index) {
                    0 -> showHeritageSiteImage(heritageSite)
                    else -> toggleFave(false, heritageSite.id, ITEM_VIEW_TYPE_ITEM_7)
                }
            },
            HeritageSiteLongListener { heritageSite ->
                goHeritageSiteSingle(heritageSite,601508051) // HeritageSitesActivity
                true
            },
            BasicClickListener { _, itemId ->
                when (itemId) {
                    0L -> showToast()
                    else -> toggleFave(false, itemId, ITEM_VIEW_TYPE_ITEM_10)
                }
            },
            ParkLongListener { parkKt ->
                goParkSingle(601516011, parkKt) // ParksActivity
                true
            },
            RouteFaveListener { _, route ->
                toggleFave(false, route.id, ITEM_VIEW_TYPE_ITEM_01)
            },
            RouteListener { routeKt ->
                navigateTo(601604051, routeKt) // DetailsActivity
            },
            RouteLongListener { routeKt ->
                navigateTo(601602011, routeKt) // Basic|Extended
                true
            },
            StreetArtListener { _, index, streetArt ->
                when (index) {
                    0 ->  goStreetArtSingle(601519041, streetArt) // StreetDetailsActivity
                    1 -> showStreetArtImage(streetArt)
                    else -> toggleFave(false, streetArt.id, ITEM_VIEW_TYPE_ITEM_11)
                }
            },
            StreetArtLongListener { streetArt ->
                goStreetArtSingle(601519011, streetArt) // StreetArtActivity
                true
            },
            UrbanPlayListener { _, index, urbanPlay ->
                when (index) {
                    0 -> showUrbanPlayImage(urbanPlay)
                    else -> toggleFave(false, urbanPlay.id, ITEM_VIEW_TYPE_ITEM_12)
                }
            },
            UrbanPlayLongListener { urbanPlay ->
                goUrbanPlaySingle(           // (else, in NavUtils)
                    601521181, urbanPlay // UrbanPlayActivity
                )
                true
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        binding.recyclerView.adapter = adapter

        viewModel.faves.observe(viewLifecycleOwner) { faves ->
            when (faves.size) {
                0 -> goHome()
                else -> {
                    lifecycleScope.launch {
                        val favesList = mutableListOf<FavesDataItem>()
                        faves.forEach { fave ->
                            favesList.add(getFavesDataItem(fave))
                        }

                        adapter.submitList(favesList)
                    }
                }
            }
        }
    }

    private fun clearAll() {
        lifecycleScope.launch {
            val result =
                viewModel.nukeFavouritesTable()
            if (result > 0) {
                val quantityString =
                    resources.getQuantityString(
                        R.plurals.clear_confirm,
                        result
                    )
                val message =
                    String.format(quantityString, result)
                fancyToast(requireContext(),4, message)
            }
        }
    }

    private fun confirmClearAll() {
        activity?.let { fragmentActivity ->
            val alertDialogBuilder = AlertDialog.Builder(fragmentActivity)
            alertDialogBuilder.apply {
                setIcon(R.drawable.ic_baseline_clear_all_black_24)
                setTitle(R.string.menu_clear)
                setMessage(R.string.confirm_clear)
                setNegativeButton(android.R.string.cancel,null)
                setPositiveButton(android.R.string.ok) { _, _ ->
                    clearAll()
                }
            }
            alertDialogBuilder.create()
            alertDialog = alertDialogBuilder.show()
        }
    }

    private suspend fun getFavesDataItem(fave: Favourite): FavesDataItem {
        return when (fave.itemType) {
            ITEM_VIEW_TYPE_ITEM_12 -> {
                val urbanPlay = viewModel.getUrbanPlayItemById(fave.itemId)
                urbanPlay.zStamp = fave.id
                FavesDataItem.UrbanPlayItem(urbanPlay)
            }
            ITEM_VIEW_TYPE_ITEM_11 -> {
                val streetArt = viewModel.getStreetArtById(fave.itemId)
                streetArt.zStamp = fave.id
                FavesDataItem.StreetArtItem(streetArt)
            }
            ITEM_VIEW_TYPE_ITEM_10 -> {
                val park = viewModel.getParkById(fave.itemId)
                park.zStamp = fave.id
                FavesDataItem.ParkItem(park)
            }
            ITEM_VIEW_TYPE_ITEM_9 -> {
                val convenience = viewModel.getConvenienceById(fave.itemId)
                convenience.zStamp = fave.id
                FavesDataItem.ConvenienceItem(convenience)
            }
            ITEM_VIEW_TYPE_ITEM_8 -> {
                val bikeTrack = viewModel.getBikeTrackById(fave.itemId)
                bikeTrack.zStamp = fave.id
                FavesDataItem.BikeTrackItem(bikeTrack)
            }
            ITEM_VIEW_TYPE_ITEM_7 -> {
                val heritageSite = viewModel.getHeritageSiteById(fave.itemId)
                heritageSite.zStamp = fave.id
                FavesDataItem.HeritageSiteItem(heritageSite)
            }
            ITEM_VIEW_TYPE_ITEM_6 -> {
                val fruitType = viewModel.getFruitTypeById(fave.itemId)
                fruitType.zStamp = fave.id
                FavesDataItem.FruitTypeItem(fruitType)
            }
            ITEM_VIEW_TYPE_ITEM_5 -> {
                val freeWiFi = viewModel.getFreeWiFiLocationById(fave.itemId)
                freeWiFi.zStamp = fave.id
                FavesDataItem.FreeWiFiItem(freeWiFi)
            }
            ITEM_VIEW_TYPE_ITEM_4 -> {
                val fountain = viewModel.getFountainById(fave.itemId)
                fountain.zStamp = fave.id
                FavesDataItem.FountainItem(fountain)
            }
            ITEM_VIEW_TYPE_ITEM_3 -> {
                val dogPark = viewModel.getDogParkById(fave.itemId)
                dogPark.zStamp = fave.id
                FavesDataItem.DogParkItem(dogPark)
            }
            ITEM_VIEW_TYPE_ITEM_2 -> {
                val facility = viewModel.getFacilityById(fave.itemId)
                facility.zStamp = fave.id
                FavesDataItem.FacilityItem(facility)
            }
            ITEM_VIEW_TYPE_ITEM_1 -> {
                val recycler = viewModel.getBatteryRecyclerById(fave.itemId)
                recycler.zStamp = fave.id
                FavesDataItem.BatteryRecyclerItem(recycler)
            }
            ITEM_VIEW_TYPE_ITEM_00 -> {
                val chCh360 = viewModel.getChCh360ItemById(fave.itemId)
                chCh360.zStamp = fave.id
                FavesDataItem.ChCh360Item(chCh360)
            }
            else -> {
                // ITEM_VIEW_TYPE_ITEM_01 (Route)
                // ITEM_VIEW_TYPE_ITEM_02 (Linked Route)
                val route = viewModel.getRouteKtById(fave.itemId)
                route.zStamp = fave.id
                FavesDataItem.RouteItem(route)
            }
        }
    }
}
