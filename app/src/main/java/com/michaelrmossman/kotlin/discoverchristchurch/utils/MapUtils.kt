package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.SphericalUtil.computeDistanceBetween
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.MapLiteDialogBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.BitmapHelper.vectorToBitmap
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.enableDisableDialogButton
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getAppContext
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getPermissionsState
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getResources
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.goSystem
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isXLargeScreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isXSmallScreen
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWaypointSubtitleText
import com.michaelrmossman.kotlin.discoverchristchurch.views.RadioGridGroup
import kotlin.properties.Delegates
import kotlin.reflect.KFunction1

/**
 * Helper functions used throughout the app, relating directly to Maps
 */
object MapUtils: RadioGridGroup.OnCheckedChangeListener {

    /*
        Map type constants:
        MAP_TYPE_NONE = 0
        MAP_TYPE_NORMAL = 1
        MAP_TYPE_SATELLITE = 2
        MAP_TYPE_TERRAIN = 3
        MAP_TYPE_HYBRID = 4
    */

    private var radioGridGroupCheckedId by Delegates.notNull<Int>()
    private lateinit var normalMap: GoogleMap
    private lateinit var satelliteMap: GoogleMap
    private lateinit var terrainMap: GoogleMap
    private lateinit var hybridMap: GoogleMap

    @SuppressLint("MissingPermission")
    fun enableDisableMyLocation(
        activity: AppCompatActivity, enable: Boolean,
        map: GoogleMap, view: View
    ) {
        when (enable) {
            true -> {
                when (val requestCode = getPermissionsState(activity,
                        REQUEST_SHOW_MY_LOCATION_PERMISSION)) {
                    2 -> map.isMyLocationEnabled = true
                    in -1..0 -> {
                        if (!sharedPrefs.getBoolean(SHOW_LOCATION_NEVER_PREF,
                                false)) {
                            showPermissionsState(activity, view, requestCode)
                        }
                    }
                    else -> requestLocationPermission(activity, requestCode) // Only course
                }
            }
            else -> map.isMyLocationEnabled = false
        }
    }

    fun getAccessPointMarkerColorId(
        mapType: Int, position: Int, size: Int
    ) : Int {
        return when (position) {
            0 -> R.color.color_green
            size.minus(1) -> R.color.color_red
            else -> {
                when (mapType) {
                    1 or 3 -> R.color.white
                    else -> R.color.black
                }
            }
        }
    }

    fun getAccessPointMarkerOptions(
        latLng: LatLng, mapType: Int, position: Int, size: Int
    ) : MarkerOptions {
        val context = getAppContext()
        val colorId =
            getAccessPointMarkerColorId(mapType, position, size)
        return MarkerOptions()
            .position(latLng)
            .icon(
                vectorToBitmap(
                    context,
                    R.drawable.ic_baseline_local_parking_default_30,
                    ContextCompat.getColor(context, colorId)
                )
            )
    }

    fun getChCh360MarkerOptions(
        position: Int, chCh360: ChCh360Kt
    ) : MarkerOptions {
        val context = getAppContext()
        val color: Int
        val drawableId =
            when (position) {
                0 -> {
                    color = // Light or dark green
                        ContextCompat.getColor(context,
                            R.color.color_green)
                    R.drawable.ic_baseline_place_default_38
                }
                else -> {
                    color = Color.parseColor(chCh360.color)
                    R.drawable.ic_baseline_place_default_30
                }
            }
        val latLng = chCh360.latLng
        return MarkerOptions()
            .position(latLng)
            .icon(
                vectorToBitmap(
                    context,
                    drawableId,
                    color
                )
            )
    }

    fun getChCh360Polyline(
        chCh360: ChCh360Kt, coordinates: List<ChCh360CoordsKt>,
        map: GoogleMap, strokeWidth: Float
    ) : Polyline {
        val color = Color.parseColor(chCh360.color)
        val polylineOptions =
            PolylineOptions()
                .color(color)
                .jointType(JointType.ROUND)
                .width(strokeWidth)
        coordinates.forEach {
            polylineOptions.add(it.latLng)
        }
        return map.addPolyline(polylineOptions)
    }

    fun getColorId(index: Int = 0): Int { // Not working right!
        val mapType =
            sharedPrefs.getInt(MAP_TYPE_PREF, MAP_TYPE_DEFAULT)
        return when (index) {
            0 -> when (mapType) {
                1 or 3 -> R.color.map_marker_light // Satellite
                else -> R.color.map_marker_dark // Normal|Terrain
            }
            else -> {
                when (mapType) {
                    1 or 3 -> R.color.color_green
                    else -> R.color.color_green_darker
                }
            }
        }
    }

    fun getCoords(
        waypoint: WaypointKt,
        waypoints: List<WaypointKt>
    ) : Coords {
        val res = getResources()
        /* Note that ChCh360|2 activities,
           & Details|DogDetails activities
           each create their own Coords */
        val subtitle =
            when (waypoint.itemType) {
                2 -> waypoint.extra as String
                else -> waypoint.waypoint
            }
        val stringArray =
            res.getStringArray(R.array.street_view_subtitles)
        val subtitlePrefix = stringArray[waypoint.itemType]
        return Coords(
            String.format(
                res.getString(R.string.app_title),
                waypoint.route),
            String.format(subtitlePrefix, subtitle),
            waypoint.latLng,
            waypoint.angle,
            getWaypointSubtitleText(
                waypoints.indexOf(waypoint).plus(1),
                waypoints.size,
                when (waypoint.itemType) {
                    0 -> R.string.ch_ch_360_a_p_count
                    1 -> R.string.ch_ch_360_count
                    2 -> R.string.route_count
                    else -> R.string.place_count // 3
                }
            )
        )
    }

    fun getDistanceInMetres(current: LatLng, dest: LatLng): Double {
        return computeDistanceBetween(current, dest)
    }

    fun getLatLng(latLng: String): LatLng {
        val split = latLng.split(",")
        val lat = split[0].trim().toDouble()
        val lng = split[1].trim().toDouble()
        return LatLng(lat, lng)
    }

    fun getMapType(
        activity: FragmentActivity,
        map: GoogleMap?,
        view: View,
        action: KFunction1<Int, Unit>? = null
    ) : AlertDialog {
        val layoutInflater = LayoutInflater.from(view.context)
        val binding = MapLiteDialogBinding.inflate(layoutInflater)
        with(binding.mapLiteNormalMap) {
            onCreate(null)
            getMapAsync(::onNormalMapReady)
            isClickable = false
        }
        with(binding.mapLiteSatelliteMap) {
            onCreate(null)
            getMapAsync(::onSatelliteMapReady)
            isClickable = false
        }
        with(binding.mapLiteTerrainMap) {
            onCreate(null)
            getMapAsync(::onTerrainMapReady)
            isClickable = false
        }
        with(binding.mapLiteHybridMap) {
            onCreate(null)
            getMapAsync(::onHybridMapReady)
            isClickable = false
        }

        // Only used to indicate that a preference has NOT been set
        val mapTypePref = sharedPrefs.getInt(MAP_TYPE_PREF, -1)
        enableDisableDialogButton(
            action != null || mapTypePref != -1
        )
        binding.mapTypeApplyToAll.apply {
            isChecked = mapTypePref != -1
        }

        val current =
            sharedPrefs.getInt(MAP_TYPE_PREF, MAP_TYPE_DEFAULT)
        val radioButton: RadioButton =
            binding.mapLiteRadioGroup.getChildAt(current)
                as RadioButton
        radioButton.isChecked = true
        radioGridGroupCheckedId = radioButton.id

        binding.mapLiteRadioGroup.setOnCheckedChangeListener(this@MapUtils)
        binding.mapTypeApplyToAll.setOnCheckedChangeListener { _, _ ->
            enableDisableDialogButton(true)
        }

        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.apply {
            setIcon(R.drawable.ic_baseline_layers_black_24)
            setTitle(R.string.menu_layers)
            setView(binding.root)
            setNegativeButton(android.R.string.cancel,null)
            setPositiveButton(android.R.string.ok) { _, _ ->
                val selectedIndex =
                    binding.mapLiteRadioGroup.getSelectedIndex(
                        radioGridGroupCheckedId
                    )
                when (map) {
                    null -> {
                        if (sharedPrefs.edit().putInt(MAP_TYPE_PREF,
                                selectedIndex).commit()) {
                            view.showSnackbar(R.string.setting_saved)
                        }
                    }
                    else -> {
                        when (binding.mapTypeApplyToAll.isChecked) {
                            true -> {
                                if (sharedPrefs.edit().putInt(MAP_TYPE_PREF,
                                        selectedIndex).commit()) {
                                    action?.let {
                                        it(selectedIndex)
                                    }
                                }
                            }
                            else -> action?.let { it(selectedIndex) }
                        }
                    }
                }
            }
        }
        return alertDialogBuilder.create()
    }

    fun getMarkerPair(
        position: Int, round: Int, size: Int, shared: Int
    ) : Pair<Int, Int> {
        val colorId: Int
        /* Workaround for North/South Boundary end caps
           being bigger than those on regular tracks/routes,
           due to huge stroke width. Be aware that this icon
           size will only work for a stroke width of 17.5F */
        val startEndDrawableId =
            when (shared) {
                4 -> { /* Show All Polylines ...
                          North/South Boundary */
                    R.drawable.ic_baseline_place_default_16
                }
                3 -> { /* Show All Polylines ...
                          See getRoutePolyline() */
                    R.drawable.ic_baseline_place_default_22
                }
                2 -> { // North/South Boundary
                    R.drawable.ic_baseline_place_default_27
                }
                else -> {
                    R.drawable.ic_baseline_place_default_38
                }
            }
        val drawableId =
            when (position) {
                0 -> {
                    colorId = R.color.color_green
                    startEndDrawableId
                }
                size.minus(1) -> {
                    colorId =
                        when (round) {
                            baseRet -> R.color.color_orange
                            extRet -> R.color.color_orange
                            else -> R.color.color_red
                        }
                    startEndDrawableId
                }
                else -> {
                    colorId = R.color.color_orange
                    R.drawable.ic_baseline_place_default_30
                }
            }
        return Pair(colorId, drawableId)
    }

    fun getPolyCenter(polyPoints: List<LatLng>): LatLng {
        val builder = LatLngBounds.builder()
        polyPoints.forEach {
            builder.include(it)
        }
        val bounds = builder.build()
        return bounds.center
    }

    fun getRouteMarkerOptions(
        position: Int, route: Route
    ) : MarkerOptions {
        val colorId: Int
        val drawableId =
            when (position) {
                0 -> {
                    colorId = R.color.color_green
                    R.drawable.ic_baseline_place_default_38
                }
                else -> {
                    colorId =
                        when (route.shared) {
                            2 -> R.color.polyline_boundary
                            1 -> R.color.polyline_shared
                            else -> R.color.polyline_track
                        }
                    R.drawable.ic_baseline_place_default_30
                }
            }
        val context = getAppContext()
        val latLng = getLatLng(route.latLng)
        return MarkerOptions()
            .position(latLng)
            .icon(
                vectorToBitmap(
                    context,
                    drawableId,
                    ContextCompat.getColor(context, colorId)
                )
            )
    }

    fun getRoutePolyline(
        activity: AppCompatActivity, map: GoogleMap, route: RouteKt,
        waypoints: List<WaypointKt>, caps: Int = 2, index: Int = Int.MAX_VALUE
    ) : Polyline {
        val colorId =
            when (route.shared) {
                2 -> R.color.polyline_boundary
                1 -> R.color.polyline_shared
                else -> R.color.polyline_track
            }
        val strokeWidth = when (index) {
            // BaseActivity: for original polyline, when showing linked routes
            Int.MIN_VALUE -> POLYLINE_OBESE_STROKE_WIDTH
            else -> when (route.shared) {
                2 -> POLYLINE_OBESE_STROKE_WIDTH
                1 -> {
                    when (index) {
                        /* Workaround for MultiDayActivity shared use end caps
                           being bigger than those on regular tracks/routes */
                        in MULTI_DAY_COASTAL_ID..MULTI_DAY_HEADS_ID -> {
                            POLYLINE_LIGHT_STROKE_WIDTH
                        }
                        else -> POLYLINE_MEDIUM_STROKE_WIDTH
                    }
                }
                else -> POLYLINE_LIGHT_STROKE_WIDTH
            }
        }
        val polylineOptions =
            PolylineOptions()
                .color(ContextCompat.getColor(activity, colorId))
                .jointType(JointType.ROUND)
                .width(strokeWidth)
        waypoints.forEach {
            polylineOptions.add(it.latLng)
        }
        val polyline = map.addPolyline(polylineOptions)
        if (index in MULTI_DAY_COASTAL_ID..MULTI_DAY_HEADS_ID
                || index == Int.MIN_VALUE) { // Note OR
            polyline.apply { /* Only used for MultiDayActivity
                Refer ChCh362Activity for that ClickListener */
                isClickable = true
                // To find position for OnPolylineClickListener
                tag = route.id
            }
        }
        if (route.shared > 0) { // North/South Boundary|Shared
            polyline.pattern =
                listOf(
                    Gap(POLYLINE_PATTERN_GAP_LENGTH),
                    Dot(),
                    Gap(POLYLINE_PATTERN_GAP_LENGTH)
                )
        }
        if (caps > 0) {
            val endPair =
                getMarkerPair(
                    waypoints.size.minus(1),
                    route.round, // Colour
                    waypoints.size,
                    when (index) { // Drawable
                        Int.MIN_VALUE -> 4 // Force smaller (16dp) for Linked
                        else -> when (caps) {
                            2 -> route.shared
                            else -> {
                                when (route.shared) {
                                    2 -> 4 // Force smaller (16dp) for Boundaries
                                    1 -> 3 // Force smaller (22dp) for Shared Use
                                    else -> 2 // Use regular North/South Boundary
                                }
                            }
                        }
                    }
                )
            val endCap =
                CustomCap(
                    vectorToBitmap(
                        activity,
                        endPair.second,
                        ContextCompat.getColor(activity, endPair.first)
                    )
                )
            polyline.endCap = endCap
            if (caps > 1) {
                val startPair =
                    getMarkerPair(
                        0, route.round, waypoints.size, when (index) {
                            Int.MIN_VALUE -> 4
                            else -> route.shared
                        }
                    )
                val startCap =
                    CustomCap(
                        vectorToBitmap(
                            activity,
                            startPair.second,
                            ContextCompat.getColor(activity, startPair.first)
                        )
                    )
                polyline.startCap = startCap
            }
        }
        return polyline
    }

    fun getWaypointMarkerOptions(
        index: Int, latLng: LatLng, round: Int, size: Int, shared: Int
    ) : MarkerOptions {
        val context = getAppContext()
        val drawablePair = getMarkerPair(index, round, size, shared)
        return MarkerOptions()
            .position(latLng)
            .icon(
                vectorToBitmap(
                    context,
                    drawablePair.second,
                    ContextCompat.getColor(context, drawablePair.first)
                )
            )
    }

    override fun onCheckedChanged(group: RadioGridGroup?, checkedId: Int) {
        radioGridGroupCheckedId = checkedId
        enableDisableDialogButton(
            group?.getSelectedIndex(checkedId) != -1
        )
    }

    private fun onNormalMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(getAppContext())
        normalMap = googleMap ?: return
        setMapLocationAndType(normalMap, MAP_TYPE_NORMAL)
    }

    private fun onSatelliteMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(getAppContext())
        satelliteMap = googleMap ?: return
        setMapLocationAndType(satelliteMap, MAP_TYPE_SATELLITE)
    }

    private fun onTerrainMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(getAppContext())
        terrainMap = googleMap ?: return
        setMapLocationAndType(terrainMap, MAP_TYPE_TERRAIN)
    }

    private fun onHybridMapReady(googleMap: GoogleMap?) {
        MapsInitializer.initialize(getAppContext())
        hybridMap = googleMap ?: return
        setMapLocationAndType(hybridMap, MAP_TYPE_HYBRID)
    }

    private fun processSnackbarAction(
        activity: AppCompatActivity, requestCode: Int, snackbar: Snackbar
    ) {
        snackbar.dismiss()
        when (requestCode) {
            -2 -> sharedPrefs.edit().putBoolean(SHOW_LOCATION_NEVER_PREF,
                true).apply()
            -1 -> goSystem(activity)
            else -> requestLocationPermission(activity, requestCode)
        }
    }

    private fun requestLocationPermission(
        activity: AppCompatActivity, requestCode: Int
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), requestCode
        )
    }

    fun setCameraToLatLng(
        init: Boolean,
        latLng: LatLng,
        map: GoogleMap,
        zoom: Float = CAMERA_DOG_PARK_ZOOM
    ) {
        val update =
            CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        when (init) {
            true -> map.moveCamera(update)
            else -> map.animateCamera(update)
        }
    }

    fun setMapCameraToView(
        bounds: LatLngBounds,
        init: Boolean,
        map: GoogleMap,
        mapView: View?,
        padding: Int = CAMERA_UPDATE_PADDING
    ) {
        mapView?.let { view ->
            val width = view.width
            val height = view.height
            val min = minOf(width, height)
            val max = maxOf(width, height)
            val ratio = max.div(min)
            // A smaller number here (Double) means greater padding
            val divisor = when {
                isXLargeScreen() -> 2.0
                isLandscape() -> {
                    when (isXSmallScreen()) {
                        true -> 4.0
                        else -> 1.4
                    }
                }
                else -> when (isXSmallScreen()) {
                    true -> 2.4
                    else -> 1.2
                }
            }
            val update = CameraUpdateFactory.newLatLngBounds(
                bounds,
                width,
                height,
                padding.div(divisor).times(ratio).toInt()
            )
            when (init) {
                true -> map.moveCamera(update)
                else -> map.animateCamera(update)
            }
        }
    }

    fun setMapCameraToView(
        init: Boolean,
        map: GoogleMap,
        mapView: View?,
        waypoints: List<WaypointKt>,
        padding: Int = CAMERA_UPDATE_PADDING
    ) {
        mapView?.let { view ->
            val bounds = LatLngBounds.builder()
            waypoints.forEach { waypoint ->
                bounds.include(waypoint.latLng)
            }
            setMapCameraToView(
                bounds.build(), init, map, view, padding
            )
        }
    }

    // Used for the various community activities
    fun setMapLocation(index: Int, map: GoogleMap) {
        val lat = when (index) {// REMEMBER THE ZOOM BELOW !!
            ITEM_VIEW_TYPE_ITEM_12 -> -43.531482 // UrbanPlay
            ITEM_VIEW_TYPE_ITEM_11 -> -43.612771 // Street Art
            ITEM_VIEW_TYPE_ITEM_10 -> -43.615967 // (Public) Parks
            ITEM_VIEW_TYPE_ITEM_9 -> -43.607431 // (Public) Conveniences
            ITEM_VIEW_TYPE_ITEM_8 -> -43.5469252 // (MTB) BikeTracks
            ITEM_VIEW_TYPE_ITEM_7 -> -43.610911 // HeritageSites
            ITEM_VIEW_TYPE_ITEM_6 -> -43.606165 // FruitTrees
            ITEM_VIEW_TYPE_ITEM_5 -> -43.631129 // FreeWiFi
            ITEM_VIEW_TYPE_ITEM_4 -> -43.617902 // (Drink) Fountains
            ITEM_VIEW_TYPE_ITEM_3 -> -43.631017 // DogParks
            ITEM_VIEW_TYPE_ITEM_2 -> -43.606785 // (Community) FacilitiesFragment
            ITEM_VIEW_TYPE_ITEM_1 -> -43.423961 // BatteryRecyclersFragment
            else -> -43.5824 // Placeholder: not actually used
        }
        val lng = when (index) {
            ITEM_VIEW_TYPE_ITEM_12 -> 172.635350 // Strand Ln, City
            ITEM_VIEW_TYPE_ITEM_11-> 172.746055 // Lyttelton Harbour
            ITEM_VIEW_TYPE_ITEM_10 -> 172.761070 // Pile Bay/Purau
            ITEM_VIEW_TYPE_ITEM_9 -> 172.784557 // Lyttelton Harbour
            ITEM_VIEW_TYPE_ITEM_8 -> 172.6181685 // Ward St, Addington
            ITEM_VIEW_TYPE_ITEM_7 -> 172.759637 // Lyttelton Harbour
            ITEM_VIEW_TYPE_ITEM_6 -> 172.719818 // Lyttelton Port
            ITEM_VIEW_TYPE_ITEM_5 -> 172.742906 // Purau Ave, D.H
            ITEM_VIEW_TYPE_ITEM_4 -> 172.719175 // Lyttelton Harbour
            ITEM_VIEW_TYPE_ITEM_3 -> 172.795001 // Port Levy
            ITEM_VIEW_TYPE_ITEM_2 -> 172.776826 // Lyttelton Harbour
            ITEM_VIEW_TYPE_ITEM_1 -> 172.468013 // Swannanoa, Nth Canterbury
            else -> 172.6695 // Mt Vernon Park, as below
        }
        val latLng = LatLng(lat, lng)
        val floatId = when (index) {
            ITEM_VIEW_TYPE_ITEM_11 -> R.dimen.street_art_initial_zoom
            ITEM_VIEW_TYPE_ITEM_9 -> R.dimen.conveniences_initial_zoom
            ITEM_VIEW_TYPE_ITEM_8 -> R.dimen.bike_tracks_initial_zoom
            ITEM_VIEW_TYPE_ITEM_3 -> R.dimen.dog_parks_initial_zoom
            ITEM_VIEW_TYPE_ITEM_1 -> R.dimen.facilities_initial_zoom
            else -> R.dimen.common_initial_zoom
        }
        val typedValue = TypedValue()
        getResources().getValue(floatId,
            typedValue,true)
        val zoom = typedValue.float
        val update = CameraUpdateFactory.newLatLngZoom(
            latLng, zoom
        )
        map.moveCamera(update)
    }

    /* Used for MapsLite (mapType) Dialog as well as the
       community activities IF there is NO current ID */
    fun setMapLocationAndType(
        map: GoogleMap, type: Int? = null
    ) {
        val latLng =
            LatLng(-43.5824, 172.6695) // Mt Vernon Park
        val update = CameraUpdateFactory.newLatLngZoom(
            latLng, CAMERA_LITE_LIST_ZOOM
        )
        if (type != null) { // Only for MapsLite
            with(map) {
                moveCamera(update)
                mapType = type
                uiSettings.apply {
                    isMapToolbarEnabled = false
                }
            }
        }
    }

    /*Used for both the various community
       activities, & MapsLite Dialog */
    fun setMapType(
        index: Int, map: GoogleMap, markers: Boolean = false
    ) : Boolean {
        val current = map.mapType.minus(1)
        // Do nothing if type has NOT actually changed
        if (index == current) {
            return false
        }
        val newMarkers: Boolean
        val mapType =
            when (index) {
                3 -> {
                    newMarkers = current == 2 || current == 0
                    MAP_TYPE_HYBRID // Const: 4
                }
                2 -> {
                    newMarkers = current == 3 || current == 1
                    MAP_TYPE_TERRAIN // 3
                }
                1 -> {
                    newMarkers = current == 2 || current == 0
                    MAP_TYPE_SATELLITE // 2
                }
                else -> {
                    newMarkers = current == 3 || current == 1
                    MAP_TYPE_NORMAL // 1
                }
            }
        map.mapType = mapType
        return when (markers) {
            true -> newMarkers
            else -> false
        }
    }

//    fun showMarkerDebugMessage(latLng: LatLng?) {
//        latLng?.let {
//            val lat = it.latitude
//            val lng = it.longitude
//            Log.i("HEY","LatLng: $lat, $lng")
//        }
//    }

    private fun showPermissionsState(
        activity: AppCompatActivity, aView: View, requestCode: Int
    ) {
        val aMessage = R.string.no_permissions
        val aDuration = Snackbar.LENGTH_INDEFINITE
        val snackbar = Snackbar.make(aView, aMessage, aDuration)
        snackbar.setAction(android.R.string.cancel) {
            snackbar.dismiss()
        }
        snackbar.addAction1(
            R.layout.snackbar_button,
            R.string.not_location
        ) {
            processSnackbarAction(activity,-2, snackbar)
        }
        snackbar.addAction1(
            R.layout.snackbar_button,
            android.R.string.ok
        ) {
            processSnackbarAction(activity, requestCode, snackbar)
        }
        snackbar.show()
    }

    fun showPoiInfoWindow(
        context: Context, map: GoogleMap, poi: PointOfInterest
    ) : Marker {
        val marker = map.addMarker(MarkerOptions()
            .position(poi.latLng)
            .title(poi.name.replace("\n"," "))
            .snippet(
                getResources().getString(R.string.poi_snippet)
            )
            .icon(
                vectorToBitmap(
                    context,
                    R.drawable.ic_baseline_circle_default_8,
                    ContextCompat.getColor(
                        context,
                        android.R.color.transparent
                    )
                )
            )
        )
        marker.apply {
            tag = -1 // Important for recyclerView.scrollToPosition
            showInfoWindow()
        }
        return marker
    }

    /* As well as zoomInOutMarkers() below, also called from
       the various Community activities, plus RoutesActivity */
    fun zoomInMarker(
        init: Boolean, latLng: LatLng, map: GoogleMap, padding: Float,
        callback: CancelableCallback? = null
    ) {
        val cameraPosition =
            CameraPosition.Builder().target(latLng).zoom(padding).build()
        val update =
            CameraUpdateFactory.newCameraPosition(cameraPosition)
        when (init) {
            true -> map.moveCamera(update)
            else -> map.animateCamera(update, callback)
        }
    }

    fun zoomInOutPolylines(
        init: Boolean,
        map: GoogleMap,
        mapView: View?,
        polys: MutableCollection<Polyline>
    ) {
        mapView?.let { view ->
            val bounds = LatLngBounds.builder()

            val polyPoints = polys.map { it.points }
            for (i in polyPoints.indices) {
                polyPoints[i].forEach {
                    bounds.include(
                        LatLng(it.latitude, it.longitude)
                    )
                }

                if (i == polys.size.minus(1)) {
                    val padding = when (init) {
                        true -> CAMERA_UPDATE_PADDING
                        else -> CAMERA_MEDIUM_PADDING
                    }

                    setMapCameraToView(
                        bounds.build(), init, map, view, padding
                    )
                }
            }
        }
    }
}
