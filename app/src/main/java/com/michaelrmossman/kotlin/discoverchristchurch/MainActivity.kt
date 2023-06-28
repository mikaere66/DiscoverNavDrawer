package com.michaelrmossman.kotlin.discoverchristchurch

import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityMainBinding
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.NavHeaderMainBinding
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.utils.DEBUG_NAV_HOME_DEFAULT
import com.michaelrmossman.kotlin.discoverchristchurch.utils.DEBUG_NAV_HOME_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.NAV_INDEX_ID_EXTRA
import com.michaelrmossman.kotlin.discoverchristchurch.utils.NO_PROMPT_ON_EXIT_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.showSnackbar
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SORT_BIKES_BY_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SORT_LISTS_BY_PREF
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getMenuIcon
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isSystemBrowserInstalled
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.fancyToast
import com.michaelrmossman.kotlin.discoverchristchurch.viewmodel.DiscoverViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class MainActivity: AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var backPressedMillis by Delegates.notNull<Long>()
    private val viewModel: DiscoverViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationIds: Set<Int>
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toolbar = binding.appBarMain.toolbar
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout
        val toggle = object: ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                viewModel.setIsNavDrawerOpen(true)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerOpened(drawerView)
                viewModel.setIsNavDrawerOpen(false)
            }
        }
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationIds = setOf(
            R.id.communityFragment, R.id.areasFragment, R.id.listFragment, R.id.multiDayFragment,
            R.id.searchFragment, R.id.favouritesFragment, R.id.settingsFragment, R.id.legendFragment
        )

        appBarConfiguration = AppBarConfiguration(navigationIds, drawerLayout)

        backPressedMillis = 0L

        checkPermissions()

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.nav_host_fragment_main
        ) as NavHostFragment
        navController = navHostFragment.navController

        val navHomeIndex =
            sharedPrefs.getInt(DEBUG_NAV_HOME_PREF, DEBUG_NAV_HOME_DEFAULT)
        if (navHomeIndex > 0) {
            val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
            navGraph.setStartDestination(navigationIds.elementAt(navHomeIndex))
            navController.graph = navGraph // The crucial step!
        }

        savedInstanceState?.let {
            val savedIndex = savedInstanceState.getInt(NAV_INDEX_ID_EXTRA)
            if (savedIndex > 0) { // Restore top-level destination if device rotated
                val options = NavOptions.Builder()
                  .setPopUpTo(navController.graph.startDestinationId,false).build()
                navController.navigate(navigationIds.elementAt(savedIndex),null, options)
            }
        }

        val navView: NavigationView = binding.navView
        navView.setupWithNavController(navController)

        val enabled = isSystemBrowserInstalled()
        val headerView = navView.getHeaderView(0)
        val headerBinding = NavHeaderMainBinding.bind(headerView)
        setTextViewListener(enabled, headerBinding.tracksAndSafety)
        setTextViewListener(enabled, headerBinding.checkTrackStatus)

        toolbar.setupWithNavController(navController, appBarConfiguration)

        onBackPressedDispatcher.addCallback(this,
                object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    true -> drawerLayout.closeDrawer(GravityCompat.START)
                    else -> {
                        when (navController.currentDestination?.id) {
                            navController.graph.startDestinationId -> { // Home
                                when (sharedPrefs.getBoolean(NO_PROMPT_ON_EXIT_PREF,
                                        false)) {
                                    true -> finish()
                                    else -> {
                                        when (backPressedMillis.plus(2_000) >
                                                System.currentTimeMillis()) {
                                            true -> finish()
                                            else -> {
                                                backPressedMillis =
                                                    System.currentTimeMillis()
                                                fancyToast(
                                                    this@MainActivity,
                                                    2, R.string.on_back_press
                                                )
                                                viewModel.setResetFlippedViews(true)
                                            }
                                        }
                                    }
                                }
                            }
                            else -> navController.popBackStack()
                        }
                    }
                }
            }
        })

        viewModel.favouritesCount.observe(this) { count ->
            val favesText = String.format(
                getString(R.string.favourites),
                count
            )
            navView.menu.findItem(R.id.favouritesFragment).apply {
                isEnabled = count > 0
                title = favesText
            }
        }

        viewModel.overflowIconIndex.observe(this) { index ->
            val arrayId = when (index) {
                0 -> R.array.overflow_icon // Default
                in 1..99 -> R.array.filter_by_icons // FruitTrees
                in 100..199 -> R.array.sort_by_icons // Routes
                else -> R.array.sort_bikes_icons // 200 ->
            }
            val arrayIndex = when (index) {
                0 -> 0
                in 1..99 -> index
                else -> {
                    val sortByPref = when (index) {
                        in 100..199 -> SORT_LISTS_BY_PREF
                        else -> SORT_BIKES_BY_PREF // 200 ->
                    }
                    sharedPrefs.getInt(sortByPref, 0)
                }
            }
            val drawable = getMenuIcon(arrayIndex, arrayId)
            toolbar.overflowIcon = drawable
        }

        viewModel.subtitle.observe(this) { subtitle ->
            supportActionBar?.subtitle = subtitle
        }

        viewModel.title.observe(this) { title ->
            supportActionBar?.title = title
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        val destination = navController.currentDestination?.id
        val navIndex = navigationIds.indexOf(destination)
        navIndex.let { outState.putInt(NAV_INDEX_ID_EXTRA, it) }
        super.onSaveInstanceState(outState)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionsLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->

        val length: Int
        val stringId = when (permissions.isNotEmpty()) {
            true -> {
                /* Either permission is sufficient at THIS stage. If only
                   COURSE is granted here, we can ask for FINE later */
                when (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                ) {
                    true -> {
                        length = Snackbar.LENGTH_SHORT
                        R.string.permissions_granted
                    }
                    else -> {
                        length = Snackbar.LENGTH_LONG
                        R.string.permissions_denied
                    }
                }
            }
            else -> {
                length = Snackbar.LENGTH_LONG
                R.string.permissions_denied
            }
        }

        binding.root.showSnackbar(stringId, length)
    }

    private fun setTextViewListener(
        enabled: Boolean, textView: TextView
    ) {
        textView.apply {
            isEnabled = enabled
            setOnClickListener {
                val url = getString(R.string.tracks_and_safety)
                startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                )
            }
        }
    }
}
