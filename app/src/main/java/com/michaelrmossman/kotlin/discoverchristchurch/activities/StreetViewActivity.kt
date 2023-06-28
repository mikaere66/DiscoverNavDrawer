package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.StreetViewPanoramaCamera
import com.google.android.gms.maps.model.StreetViewPanoramaLocation
import com.google.android.gms.maps.model.StreetViewSource
import com.google.android.material.button.MaterialButtonToggleGroup
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Coords
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SHOW_STREET_VIEW_NAMES_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.getSelectedIndex
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StreetViewActivity: AppCompatActivity(),
    StreetViewPanorama.OnStreetViewPanoramaChangeListener,
    StreetViewPanorama.OnStreetViewPanoramaCameraChangeListener {

    private var streetViewPanorama: StreetViewPanorama? = null
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var coords: Coords

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_street_view)

        coords = viewModel.currentCoords

        supportActionBar?.title = coords.title
        supportActionBar?.subtitle = coords.subtitle

        val resetButton: Button = findViewById(R.id.street_view_reset_button)
        resetButton.setOnClickListener { setStreetViewPanorama(null,false) }

        setCountText() // Show */** count regardless of Street View availability

        setStreetViewPanorama(savedInstanceState)

        val showNamesPref = sharedPrefs.getInt(SHOW_STREET_VIEW_NAMES_PREF, 1)
        val toggleButtonGroup: MaterialButtonToggleGroup =
            findViewById(R.id.street_view_toggle_button_group)
        val toggleButton: View = toggleButtonGroup.getChildAt(showNamesPref)
        toggleButtonGroup.check(toggleButton.id)
        toggleButtonGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) { // Required, because listener also returns PREVIOUSLY checked id
                val index = toggleButtonGroup.getSelectedIndex(checkedId)
                if (sharedPrefs.edit().putInt(SHOW_STREET_VIEW_NAMES_PREF, index).commit()) {
                    showHideViews(false,index == 1)
                }
            }
        }
    }

    private fun setStreetViewPanorama(
        savedInstanceState: Bundle?, init: Boolean = true
    ) {
        val streetViewPanoramaFragment =
            supportFragmentManager.findFragmentById(R.id.street_view_map)
                    as SupportStreetViewPanoramaFragment?
        streetViewPanoramaFragment?.getStreetViewPanoramaAsync { panorama ->
            streetViewPanorama = panorama
            // Only set the panorama to LatLng on startup (when no panoramas
            // have been loaded, which is when the savedInstanceState is null)
            savedInstanceState ?: panorama.apply {
                setPosition(coords.latLng, StreetViewSource.OUTDOOR)
                val camera = StreetViewPanoramaCamera.Builder()
                    .bearing(coords.angle.toFloat())
                    .tilt(0F)
                    .build()
                animateTo(camera, 0L)
            }

            if (init) {
                panorama.setOnStreetViewPanoramaCameraChangeListener(
                    this@StreetViewActivity
                )

                panorama.setOnStreetViewPanoramaChangeListener(
                    this@StreetViewActivity
                )
            }
        }
    }

    override fun onStreetViewPanoramaChange(location: StreetViewPanoramaLocation?) {
        (findViewById<ProgressBar>(R.id.street_view_progress)).isVisible = false
        (findViewById<ProgressBar>(R.id.street_view_count_text)).isVisible = true
        when (location == null) {
            true -> showHideViews(all = true, show = false)
            else -> {
                val latText: TextView = findViewById(R.id.street_view_lat_text)
                latText.text =
                    String.format(getString(R.string.street_view_lat),
                        location.position.latitude)
                val lngText: TextView = findViewById(R.id.street_view_lng_text)
                lngText.text =
                    String.format(getString(R.string.street_view_lng),
                        location.position.longitude)
                showHideViews(
                false,
                sharedPrefs.getInt(SHOW_STREET_VIEW_NAMES_PREF, 1) == 1
                )
            }
        }
    }

    override fun onStreetViewPanoramaCameraChange(camera: StreetViewPanoramaCamera?) {
        if (sharedPrefs.getInt(SHOW_STREET_VIEW_NAMES_PREF, 1) == 1) {
            camera?.let {
                setAngleText(it.bearing)
            }
        }
    }

    private fun setAngleText(float: Float) {
        val angleText: TextView = findViewById(R.id.street_view_angle_text)
        angleText.text =
            String.format(getString(R.string.street_view_angle), float)
    }

    private fun setCountText() {
        val countText: TextView = findViewById(R.id.street_view_count_text)
        countText.text = coords.count
    }

    private fun showHideViews(all: Boolean, show: Boolean) {
        streetViewPanorama?.isStreetNamesEnabled = show
        (findViewById<TextView>(R.id.street_view_group)).isVisible = show
        if (all) {
            (findViewById<TextView>(
                R.id.street_view_toggle_button_group
            )).isVisible = false
            (findViewById<TextView>(
                R.id.street_view_reset_button
            )).isVisible = false
            (findViewById<TextView>(
                R.id.street_view_empty_text
            )).isVisible = true
        }
        if (show) {
            streetViewPanorama?.panoramaCamera?.let {
                setAngleText(it.bearing)
            }
        }
    }
}