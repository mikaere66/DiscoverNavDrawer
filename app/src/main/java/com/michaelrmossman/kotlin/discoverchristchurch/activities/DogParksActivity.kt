package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnPolygonClickListener
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParkMapListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParkMapLongListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParkSearchAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParkSearchListener
import com.michaelrmossman.kotlin.discoverchristchurch.adapters.DogParksMapAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityDogParksBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.DogParkSearchDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.DogParkKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.PolyPoint
import com.michaelrmossman.kotlin.discoverchristchurch.fragments.InfoDogParkFragment
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.BitmapHelper.vectorToBitmap
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getDistanceInMetres
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.setCameraToLatLng
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.contentDescrToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getDogParkBylaw
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class DogParksActivity: CommunityBaseActivity<ActivityDogParksBinding>(
    R.layout.activity_dog_parks
) , OnPolygonClickListener {

    private lateinit var polygonsScope: CoroutineScope
    private lateinit var adapter: DogParksMapAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_3

        if (viewModel.currentDogParkId == 0L) {
            setTitle(null)

            adapter = DogParksMapAdapter(
                DogParkMapListener { dogPark, index ->
                    when (index) {
                        0 -> scrollToPosition(dogPark)
                        1 -> showInfoFragment(dogPark)
                        else -> zoomInPolygon(dogPark) // 2
                    }
                },
                DogParkMapLongListener { index ->
                    contentDescrToast(index) // Remember: index - 1
                    true
                }
            )

            recyclerView = binding.recyclerView
            recyclerView.adapter = adapter
            recyclerView.isVisible = true
            layoutManager = LinearLayoutManager(
                this,
                when (isLandscape()) {
                    true -> RecyclerView.VERTICAL
                    else -> RecyclerView.HORIZONTAL
                },
                false
            )
            recyclerView.layoutManager = layoutManager
            val snapHelper: SnapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(recyclerView)

            viewModel.dogParksKt.observe(this) { dogParks ->
                adapter.submitList(dogParks)
                setSubtitle(null, dogParks.size)
            }

            viewModel.showStreetView.observe(this) { show ->
                if (show) {
                    goStreetView()
                    viewModel.setShowStreetView(false)
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        val context = this@DogParksActivity
        with (map) {
            setOnInfoWindowClickListener(context)
            setOnPolygonClickListener(context)
        }
        allPolyPoints = mutableListOf()
        allPolygons = HashMap()
        when (val dogParkId = viewModel.currentDogParkId) {
            0L -> {
                val layout = binding.bannerLayout
                layout.dogParksActivity = context
                val progressIndicator = layout.bannerProgress.apply {
                    // Ensure that value is reset each time
                    setProgressCompat(0,false)
                }
                polygonsScope = CoroutineScope(Dispatchers.Default)
                polygonsScope.launch {
                    val dogParks = adapter.currentList
                    if (dogParks.isNotEmpty()) {
                        val size = dogParks.size
                        for (i in dogParks.indices) {
                            when (size) {
                                in 1..99 -> {
                                    withContext(Dispatchers.Main) {
                                        addPolygon(dogParks[i],false)
                                    }
                                }
                                else -> {
                                    if (isActive) {
                                        val progress = i.getProgress(size).plus(1)
                                        val progressText = String.format(
                                            getString(R.string.polygons_progress),
                                            i, size
                                        )
                                        withContext(Dispatchers.Main) {
                                            addPolygon(dogParks[i],false)
                                            layout.apply {
                                                bannerText.text = progressText
                                                root.visibility = when (progress) {
                                                    in 0..99 -> View.VISIBLE
                                                    else -> View.GONE
                                                }
                                            }
                                            progressIndicator.apply {
                                                setProgressCompat(progress,true)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else -> {
                lifecycleScope.launch {
                    val dogPark = viewModel.getDogParkById(dogParkId)
                    val bylaw = getDogParkBylaw(dogPark.typeId.toInt())
                    setTitle(dogPark.dogPark)
                    setSubtitle(bylaw)
                    addPolygon(dogPark,true)
                    with(dogPark) {
                        setFavouriteButton(binding.toggleFaveButton,
                        id, ITEM_VIEW_TYPE_ITEM_3, fave,true)
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_show_all -> {
                zoomOutPolygons(false)
                true
            }
            R.id.menu_search -> {
                getDogParkName()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onPolygonClick(polygon: Polygon?) {
        polygon?.let { poly ->
            lifecycleScope.launch {
                clearSelectedMarker()
                val context = this@DogParksActivity
                val polyCentre = poly.getCenter()
                val dogParkId = poly.tag as Long
                val dogPark = viewModel.getDogParkById(dogParkId)
                val actualCentre =
                    getNearestActualPolyPoint(dogPark, polyCentre)
                selectedMarker = map.addMarker(MarkerOptions().apply {
                    position(actualCentre)
                    title(dogPark.dogPark)
                    snippet(getSnippet(dogPark))
                    icon(
                        vectorToBitmap(
                            context,
                            R.drawable.ic_baseline_circle_default_8,
                            ContextCompat.getColor(
                                context,
                                android.R.color.transparent
                            )
                        )
                    )
                })
                selectedMarker?.showInfoWindow()
                if (viewModel.currentDogParkId == 0L) {
                    scrollToPosition(dogPark.id)
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu) // Init default

        return when (viewModel.currentDogParkId) {
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

    private suspend fun addPolygon(dogPark: DogParkKt, init: Boolean) {
        val environmentsSplit = dogPark.environmentIds.split(",")
        val dogParkTypeBorder = Color.parseColor(dogPark.border)
        val dogParkTypeColor = Color.parseColor(dogPark.color)
        for (i in environmentsSplit.indices) {
            val environmentId = environmentsSplit[i].trim().toLong()
            val environmentList =
                viewModel.getEnvironmentListByDogParkId(environmentId)
            when (environmentList.size) {
                0 -> fancyToast(this,3, R.string.no_coords) // Debug
                else -> {
                    val polyPoints = environmentList.map { PolyPoint(dogPark.id, it.latLng) }
                    allPolyPoints.addAll(polyPoints)
                    val polygonOptions = PolygonOptions().clickable(true)
                    polyPoints.forEach { polyPoint ->
                        polygonOptions.add(polyPoint.latLng)
                    }

                    dogPark.holeIds?.let { holeIds ->
                        val holesSplit = holeIds.split(",")
                        holesSplit.forEach { holeId ->
                            val holesList =
                                viewModel.getEnvironmentHoleListById(holeId.trim().toLong())
                            val holeLatLngList = holesList.map { it.latLng }
                            val holePoints = holeLatLngList.map { it }
                            polygonOptions.addHole(holePoints)
                        }
                    }

                    allPolygons?.let { polygons ->
                        polygons[dogPark.id] = // Was environmentId
                            map.addPolygon(polygonOptions).apply {
                                fillColor = dogParkTypeColor
                                strokeColor = dogParkTypeBorder
                                strokeWidth = 14F
                                isClickable = true
                                // Find item ID for OnPolygonClickListener
                                tag = dogPark.id
                            }
                    }

                    if (init && i == environmentsSplit.size.minus(1)) {
                        zoomInOutPolygons(true, CAMERA_SMALL_PADDING)
                    }
                }
            }
        }
    }

    override fun cancelCoroutine() { // Also called from xml
        if (this::polygonsScope.isInitialized) {
            polygonsScope.cancel()
            val layoutRoot = binding.bannerLayout.root
            if (layoutRoot.isVisible) {
                layoutRoot.visibility = View.GONE
            }
        }
    }

    private fun getDogParkName() {
        val adapter = DogParkSearchAdapter(
            DogParkSearchListener { dogPark ->
                alertDialog?.dismiss()
                allPolygons?.let { polygons ->
                    onPolygonClick(polygons[dogPark.id])
                }
                scrollToPosition(dogPark.id)
                zoomInPolygon(dogPark)
            }
        )
        adapter.stateRestorationPolicy = PREVENT_WHEN_EMPTY

        val dogParkSearchDialogBinding = DogParkSearchDialogBinding.inflate(layoutInflater)
        dogParkSearchDialogBinding.apply {
            recyclerView.adapter = adapter
            dogParkSearchContainer.editText?.doOnTextChanged { cs, _, _, _ ->
                val filterTerm =
                    when (cs?.length) {
                        in 0..1 -> null
                        else -> cs?.trim()?.toString()
                    }
                if (viewModel.dogParkFilterTerm.value != filterTerm) {
                    viewModel.setDogParkFilterTerm(filterTerm)
                }
            }
        }

        viewModel.dogParksFilteredBy.observe(this) { dogParks ->
            adapter.submitList(dogParks)
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_nature_people_black_24)
            setTitle(R.string.search_dog_park_name_title)
            setView(dogParkSearchDialogBinding.root)
            setNegativeButton(android.R.string.cancel,null)
            setOnDismissListener { viewModel.setDogParkFilterTerm(null) }
        }

        alertDialog = alertDialogBuilder.create()
        alertDialog?.show()
    }

    private fun getNearestActualPolyPoint(
        dogPark: DogParkKt, polyCentre: LatLng
    ) : LatLng {
        val mutableList = allPolyPoints.filter {
            polyPoint -> dogPark.id == polyPoint.itemId
        }.toMutableList()

        mutableList.forEach { item ->
            item.nearest = getDistanceInMetres(
                item.latLng, polyCentre
            )
        }

        mutableList.sortBy { it.nearest }

        val nearest = mutableList[0].latLng

        setCameraToLatLng(false, nearest, map)

        return nearest
    }

    private fun getSnippet(dogPark: DogParkKt): String {
        val bylaw = getDogParkBylaw(dogPark.typeId.toInt())
        return when (dogPark.extra) {
            null -> {
                when (dogPark.dogPark.endsWith(bylaw)) {
                    true -> getString(R.string.dedicated_dog_park)
                    else -> bylaw
                }
            }
            else -> dogPark.extra
        }
    }

    // Called from viewModel.showStreetView observer (see above)
    private fun goStreetView() {
        val dogPark = viewModel.currentDogPark
        goStreetView(dogPark.angle != -1)
    }

    private fun scrollToPosition(dogPark: DogParkKt) {
        /* Shouldn't be able to get this far if single item,
           as onPolygonClick() not set, so just for safety */
        if (viewModel.currentDogParkId == 0L) {
            if (adapter.currentList.size > 1) {
                dogPark.position?.let { pos ->
                    dogPark.size?.let { qty ->
                        recyclerView.smoothScrollToPosition(when (pos) {
                                qty.minus(1) -> pos.minus(1)
                                else -> pos.plus(1)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun scrollToPosition(dogParkId: Long) {
        val index = adapter.currentList.indexOfFirst { dogPark ->
            dogPark.id == dogParkId
        }
        if (index != -1) {
            scrollToPosition(index, adapter.currentList.size)
        }
    }

    private fun setSubtitle(bylaw: String?, count: Int = 0) {
        lifecycleScope.launch {
            val subtitle =
                when (bylaw) {
                    null -> { // All selected
                        val total = viewModel.getDogParksCount()
                        val quantityString =
                            resources.getQuantityString(
                                R.plurals.dogs_map_subtitle,
                                count
                            )
                        String.format(quantityString, count, total)
                    }
                    else -> { // Single
                        String.format(
                            getString(R.string.common_map_subtitle),
                            bylaw
                        )
                    }
                }
            supportActionBar?.subtitle = subtitle
        }
    }

    private fun setTitle(environment: String?) {
        supportActionBar?.title = String.format(
            getString(R.string.app_title),
            when (environment) {
                null -> getString(R.string.dog_parks_title)
                else -> environment
            }
        )
    }

    private fun showInfoFragment(dogPark: DogParkKt) {
        viewModel.setCurrentDogPark(dogPark)
        InfoDogParkFragment().show(
            supportFragmentManager,
            FRAGMENT_INFO_TAG
        )
    }

    private fun zoomInPolygon(
        dogPark: DogParkKt
    ) {
        lifecycleScope.launch {
            val bounds = LatLngBounds.builder()
            val environmentsSplit = dogPark.environmentIds.split(",")
            for (i in environmentsSplit.indices) {
                val environmentId = environmentsSplit[i].trim().toLong()
                val environmentList =
                    viewModel.getEnvironmentListByDogParkId(environmentId)
                val polyPoints = environmentList.map { it.latLng }
                polyPoints.forEach { polyPoint ->
                    bounds.include(polyPoint)
                }
                if (i == environmentsSplit.size.minus(1)) {
                    zoomInPolygon(bounds.build(),false, CAMERA_SMALL_PADDING)
                }
            }
        }
    }
}
