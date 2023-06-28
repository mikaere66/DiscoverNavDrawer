package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.listeners.RouteListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.RouteSearchAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.RouteSearchDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFavouritesMessagePair
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import kotlinx.coroutines.async

// Extended by Detailed|all Maps activities, for RouteSearch dialog
abstract class CommonDialogActivity<T: ViewDataBinding>(
    @LayoutRes private val layoutResId: Int
) : AppCompatActivity() {

    val viewModel: DiscoverViewModel by viewModels()
    lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
    }

    override fun onDestroy() {
        // Reset parameters created for activities
        viewModel.unsetParams()

        super.onDestroy()
    }

    fun getRouteName() {
        val adapter = RouteSearchAdapter(
            RouteListener { route ->
                alertDialog?.dismiss()
                setPosition(route.id)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        val routeSearchDialogBinding = RouteSearchDialogBinding.inflate(layoutInflater)
        routeSearchDialogBinding.apply {
            recyclerView.adapter = adapter
            routeSearchContainer.editText?.doOnTextChanged { cs, _, _, _ ->
                val filterTerm =
                    when (cs?.length) {
                        in 0..1 -> null
                        else -> cs?.trim()?.toString()
                    }
                if (viewModel.routeFilterTerm.value != filterTerm) {
                    viewModel.setRouteFilterTerm(filterTerm)
                }
            }
        }

        viewModel.routesFilteredBy.observe(this) { routes ->
            adapter.submitList(routes)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_route_black_24)
            setTitle(R.string.search_route_name_title)
            setView(routeSearchDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setOnDismissListener { viewModel.setRouteFilterTerm(null) }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    fun goStreetView(validated: Boolean) {
        if (validated) {
            val dogPark = viewModel.currentDogPark
            when (dogPark.angle) {
                // Shouldn't actually be able to get this far, so just for safety
                -1 -> fancyToast(this,4, R.string.no_street_view_a)
                else -> {
                    dogPark.latLng?.let { latLng ->
                        dogPark.startPoint?.let { start ->
                            val coords = Coords(
                                String.format(
                                    getString(R.string.app_title),
                                    dogPark.dogPark
                                ),
                                String.format(
                                    getString(R.string.street_view_start_point_subtitle),
                                    start
                                ),
                                latLng,
                                dogPark.angle,
                                getWaypointSubtitleText(1,2)
                            )
                            viewModel.setCurrentCoords(coords)
                            startActivity(
                                Intent(
                                    this, StreetViewActivity::class.java
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    open fun setPosition(routeId: Long) {}

    suspend fun toggleFavourite(
        // Here, fave refers to the current fave state, NOT the new value
        fave: Boolean, itemId: Long, itemType: Int, reInit: Boolean = false
    ) : Pair<Long, Int> {
        val job = lifecycleScope.async {
            val result =
                viewModel.toggleFavourite(!fave, itemId, itemType, reInit)
            val messagePair = getFavouritesMessagePair(result)
            fancyToast( // Will also show an error message, if applicable
                this@CommonDialogActivity,
                messagePair.first, messagePair.second
            )
            return@async result
        }
        return job.await()
    }
}
