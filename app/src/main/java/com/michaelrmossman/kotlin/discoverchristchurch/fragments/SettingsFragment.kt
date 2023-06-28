package com.michaelrmossman.kotlin.discoverchristchurch.fragments

import android.os.Bundle
import android.view.View
import android.animation.ObjectAnimator
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.FragmentSettingsBinding
import com.michaelrmossman.kotlin.discoverchristchurch.utils.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getMapType
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getPermissionsState
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.showSystemDialog
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleBold
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class SettingsFragment: BaseFragment<FragmentSettingsBinding>(
    R.layout.fragment_settings
) , CompoundButton.OnCheckedChangeListener {

    private lateinit var invisibleToVisible: ObjectAnimator
    private lateinit var visibleToInvisible: ObjectAnimator

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (buttonView?.id == binding.locationSwitch.id) {
            if (sharedPrefs.edit().putBoolean(
                    SHOW_MY_LOCATION_PREF,
                    !isChecked
                ).commit()
            ) binding.root.showSnackbar(R.string.setting_saved)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setSubtitle(getString(R.string.app_settings))
    }

    override fun onPause() {
        // Disable listener temporarily, to avoid snackBar upon return from map
        binding.locationSwitch.setOnCheckedChangeListener(null)

        if (this::invisibleToVisible.isInitialized) {
            invisibleToVisible.cancel()
        }

        if (this::visibleToInvisible.isInitialized) {
            visibleToInvisible.cancel()
        }

        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        val enabled = getPermissionsState(
            (activity as AppCompatActivity),
            REQUEST_CURRENT_LOCATION_PERMISSION
        ) > 0
        binding.locationSwitch.apply {
            isEnabled = enabled
            if (enabled) {
                isChecked =
                    !sharedPrefs.getBoolean(
                        SHOW_MY_LOCATION_PREF,
                        true
                    )
                setOnCheckedChangeListener(this@SettingsFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            settingsFragment = this@SettingsFragment

            mapTypeButton.setOnClickListener {
                activity?.let {
                    alertDialog =
                        getMapType(it,null, binding.root)
                    alertDialog?.show()

//                    val enable =
//                       sharedPrefs.getInt(MAP_TYPE_PREF, -1) != -1
//                    enableDisableDialogButton(enable)
                }
            }

            systemSettingsButton.setOnClickListener {
                activity?.let {
                    showSystemDialog(
                        it as AppCompatActivity, R.string.system_dialog
                    )
                }
            }

            markersSwitch.apply {
                isChecked =
                    sharedPrefs.getBoolean(
                        ALWAYS_SHOW_MARKERS_PREF,
                        false
                    )
                setOnCheckedChangeListener { _, isChecked ->
                    if (sharedPrefs.edit().putBoolean(
                            ALWAYS_SHOW_MARKERS_PREF,
                            isChecked
                        ).commit()
                    ) binding.root.showSnackbar(R.string.setting_saved)
                }
            }

            polylineSwitch.apply {
                isChecked =
                    sharedPrefs.getBoolean(
                        ALWAYS_SHOW_POLYLINE_PREF,
                        false
                    )
                setOnCheckedChangeListener { _, isChecked ->
                    if (sharedPrefs.edit().putBoolean(
                            ALWAYS_SHOW_POLYLINE_PREF,
                            isChecked
                        ).commit()
                    ) binding.root.showSnackbar(R.string.setting_saved)
                }
            }

            lifecycleScope.launch {
                val routesCount = viewModel.getRoutesCount()
                randomMaxText.text = routesCount.toString()
                if (routesCount > 0) {
                    randomIdSlider.apply {
                        addOnSliderTouchListener(sliderTouchListener)
                        valueTo = routesCount.toFloat()
                        value = sharedPrefs.getFloat(RANDOM_ROUTE_ID_PREF, valueTo.div(2))
                    }
                }
            }

            mapSwitch.apply {
                isChecked =
                    sharedPrefs.getBoolean(
                        ALWAYS_GO_MAPS_PREF,
                        false
                    )
                setOnCheckedChangeListener { _, isChecked ->
                    if (sharedPrefs.edit().putBoolean(
                            ALWAYS_GO_MAPS_PREF,
                            isChecked
                        ).commit()
                    ) binding.root.showSnackbar(R.string.setting_saved)
                }
            }

            footerSwitch.apply {
                isChecked =
                    !sharedPrefs.getBoolean(
                        SHOW_FOOTER_PREF,
                        true
                    )
                setOnCheckedChangeListener { _, isChecked ->
                    if (sharedPrefs.edit().putBoolean(
                            SHOW_FOOTER_PREF,
                            !isChecked
                        ).commit()
                    ) binding.root.showSnackbar(R.string.setting_saved)
                }
            }

            promptSwitch.apply {
                isChecked =
                    sharedPrefs.getBoolean(
                        NO_PROMPT_ON_EXIT_PREF,
                        false
                    )
                setOnCheckedChangeListener { _, isChecked ->
                    if (sharedPrefs.edit().putBoolean(
                            NO_PROMPT_ON_EXIT_PREF,
                            isChecked
                        ).commit()
                    ) binding.root.showSnackbar(R.string.setting_saved)
                }
                setSpannableStylePromptText()
            }
        }
    }

    // ImageButtons longClick from xml
    fun contentDescrToast(index: Int): Boolean {
        activity?.let {
            val stringId =
                when (index) {
                    2 -> R.string.random_id
                    1 -> R.string.menu_details
                    else -> R.string.menu_map
                }
            fancyToast(it,2, stringId)
            return true
        }
        return false
    }

    fun getRandom() {
        binding.apply {
            val quantity = randomIdSlider.valueTo.toInt()

            var random: Float
            do {
                random = Random.nextInt(1, quantity).toFloat()
            } while (random == randomIdSlider.value)

            randomIdSlider.value = random
            setPref(RANDOM_ROUTE_ID_PREF, random,false)
            when (sharedPrefs.getBoolean(
                    ALWAYS_GO_MAPS_PREF,
                    false
                )
            ) {
                true -> goMap(true)
                else -> goDetails(true)
            }
        }
    }

    fun goDetails(initDelay: Boolean) {
        lifecycleScope.launch {
            // Provide brief feedback upon setting the slider
            if (initDelay) { delay(100L) }

            val routeId = binding.randomIdSlider.value.toLong()
            viewModel.setCurrentRouteId(routeId)

            val route = viewModel.getRouteKtById(routeId)
            navigateTo(1905604051, route) // DetailsActivity
        }
    }

    fun goMap(initDelay: Boolean) {
        lifecycleScope.launch {
            // Provide brief feedback after setting the slider
            if (initDelay) { delay(100L) }

            val routeId = binding.randomIdSlider.value.toLong()
            viewModel.setCurrentRouteId(routeId)

            val route = viewModel.getRouteKtById(routeId)
            navigateTo(1905602011, route) // Basic|Extended
        }
    }

    private fun setPref(
        prefString: String, sliderValue: Float, showSnackbar: Boolean
    ) {
        if (sharedPrefs.edit().putFloat(prefString, sliderValue).commit()) {
            if (showSnackbar) {
                binding.root.showSnackbar(R.string.setting_saved)
            }
        }
    }

    private fun setSpannableStylePromptText() {
        val string1b = getString(R.string.toast_on_back_press_b)
        val string1a =
            String.format(getString(R.string.toast_on_back_press_a),
                getString(R.string.on_back_press))
        val spannable = getSpannableStyleBold(string1b, string1a)
        binding.promptSwitch.text = spannable
    }

    private val sliderTouchListener: Slider.OnSliderTouchListener =
        object: Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}
            override fun onStopTrackingTouch(slider: Slider) {
                val sliderPref: String =
                    when (slider.id) {
                        binding.randomIdSlider.id -> RANDOM_ROUTE_ID_PREF
                        else -> String() // Empty string, just for safety
                     }
                if (sliderPref.isNotEmpty()) {
                    setPref(sliderPref, slider.value,true)
                }
            }
        }
}
