package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FacilitySearchAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.FacilitySearchListener
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FacilitySearchDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FacilityKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.CAMERA_UPDATE_PADDING
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorMultiIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_2
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FacilitiesActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    lateinit var facilities: List<FacilityKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_2

        lifecycleScope.launch {
            val title = when (viewModel.getFacilityTypesSelectedCount()) {
                1 -> {
                    val facility = viewModel.getFacilityTypeSelected()
                    String.format(
                        getString(R.string.facilities_activity_title_single),
                        facility.type
                    )
                }
                else -> getString(R.string.facilities_activity_title_multi)
            }
            setTitleSubtitle(title)

            facilities = viewModel.getFacilitiesList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (facilities.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_search -> {
                getFacilityName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu) // Init default

        return when (viewModel.currentFacilityId) {
            0L -> {
                menu.apply {
                    findItem(R.id.menu_search)?.apply {
                        isVisible = true
                    }
                }
                true
            }
            else -> false
        }
    }

    private fun addClusteredMarkers() {
        val context = this@FacilitiesActivity
        clusterRenderer = ColorMultiIconRenderer(
            context,
            R.drawable.ic_baseline_place_default_30,
            map, clusterManager
        )
        clusterManager.renderer = clusterRenderer
        for (i in facilities.indices) {
            val latLng = facilities[i].latLng
            val markerItem = getMarkerItem(
                latLng, facilities[i].facility,
                getSnippet(facilities[i]), facilities[i].color
            )
            allMarkerItems[facilities[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == facilities.size.minus(1)) {
                initClusterManager()
                when (val facilityId = viewModel.currentFacilityId) {
                    0L -> zoomInOutMarkers(true, CAMERA_UPDATE_PADDING)
                    else -> {
                        val facilityKt: FacilityKt? = facilities.stream()
                            .filter { facility ->
                                facility.id == facilityId
                            }
                            .findFirst()
                            .orElse(null)
                        facilityKt?.let { facility ->
                            zoomInMarker(facility.latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getFacilityName() {
        val adapter = FacilitySearchAdapter(
            FacilitySearchListener { facility ->
                alertDialog?.dismiss()
                zoomInOutMarker(facility.id)
            }
        )
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY

        val facilitySearchDialogBinding = FacilitySearchDialogBinding.inflate(layoutInflater)
        facilitySearchDialogBinding.apply {
            recyclerView.adapter = adapter
            facilitySearchContainer.editText?.doOnTextChanged { cs, _, _, _ ->
                val filterTerm =
                    when (cs?.length) {
                        in 0..1 -> null
                        else -> cs?.trim()?.toString()
                    }
                if (viewModel.facilityFilterTerm.value != filterTerm) {
                    viewModel.setFacilityFilterTerm(filterTerm)
                }
            }
        }

        viewModel.facilitiesFilteredBy.observe(this) { facilities ->
            adapter.submitList(facilities)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_interests_black_24)
            setTitle(R.string.search_facility_name_title)
            setView(facilitySearchDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setOnDismissListener { viewModel.setFacilityFilterTerm(null) }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun getSnippet(facility: FacilityKt): String {
        return when (facility.extra) {
            null -> facility.type
            else -> facility.extra
        }
    }
}
