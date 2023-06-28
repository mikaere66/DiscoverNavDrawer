package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.view.ViewTreeObserver
import androidx.annotation.ArrayRes
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.MenuCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.actionMenu
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.alertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.activities.BasicActivity
import com.michaelrmossman.kotlin.discoverchristchurch.activities.ChCh361Activity
import com.michaelrmossman.kotlin.discoverchristchurch.activities.ExtendedActivity
import com.michaelrmossman.kotlin.discoverchristchurch.activities.ImagesActivity
import com.michaelrmossman.kotlin.discoverchristchurch.entities.RouteKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.DimensionUtils.getScreenOrientation
import java.util.*
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

/**
 * Helper functions used throughout the app, relating directly to OTHER functions/values
 */
object SysUtils {

    fun applyConstraintSet(
        constraintLayout: ConstraintLayout,
        constraintSet: ConstraintSet,
        viewIds: List<Int>
    ) {
        for (i in viewIds.indices) {
            // Required with ScrollView, otherwise nothing shows
            constraintSet.constrainWidth(viewIds[i],
                ConstraintSet.MATCH_CONSTRAINT)
            constraintSet.constrainHeight(viewIds[i],
                ConstraintSet.WRAP_CONTENT)

            constraintSet.connect(
                viewIds[i], ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START,0
            )

            constraintSet.connect(
                viewIds[i], ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END,0
            )

            val constraintTopId =
                when (i) {
                    0 -> ConstraintSet.PARENT_ID // toTopOf parent
                    else -> viewIds[i.minus(1)] // toBottomOf prev
                }

            val constraintOfTopId =
                when (i) {
                    0 -> ConstraintSet.TOP
                    else -> ConstraintSet.BOTTOM
                }

            constraintSet.connect(
                viewIds[i], ConstraintSet.TOP,
                constraintTopId, constraintOfTopId,24 // Was 8, then 16
            )

            val constraintBottomId =
                when (i == viewIds.size.minus(1)) {
                    true -> ConstraintSet.PARENT_ID // toBottomOf parent
                    else -> viewIds[i.plus(1)] // toTopOf next
                }

            val bottomMargin: Int
            val constraintOfBottomId =
                when (i == viewIds.size.minus(1)) {
                    true -> {
                        bottomMargin = 0
                        ConstraintSet.BOTTOM
                    }
                    else -> {
                        bottomMargin = 4 // Was 16, then 8
                        ConstraintSet.TOP
                    }
                }

            constraintSet.connect(
                viewIds[i], ConstraintSet.BOTTOM,
                constraintBottomId, constraintOfBottomId, bottomMargin
            )

            constraintSet.applyTo(constraintLayout)
        }
    }

    // Used by both MapsLite and the community Filter by Type dialogs
    fun enableDisableDialogButton(enable: Boolean) {
        alertDialog?.apply {
            getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = enable
        }
    }

    fun getAppContext(): Context {
        return DiscoverApp.instance.applicationContext
    }

    fun getEmptyRouteKt(): RouteKt {
        return RouteKt(
            0L,
            0L,
            0L,
            String(),
            null,
            0,
            String(),
            String(),
            0,
            0,
            0,
            0,
            0,
            0,
            false,
            String(),
            LatLng(0.0,0.0),
            null,
            0,
            0,
            false
        )
    }

    fun getImageIdentifier(@ArrayRes arrayId: Int, itemId: Long): Int {
        val imagesArray = getResources().obtainTypedArray(arrayId)
        val imageId =
            imagesArray.getResourceId(itemId.toInt(),0)
        imagesArray.recycle()
        return imageId
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun getInstallerPackageName(packageName: String): String? {
        kotlin.runCatching {
            return getAppContext().packageManager.getInstallSourceInfo(
                packageName
            ).installingPackageName
        }

        return null
    }

    fun getMenuIcon(index: Int, @ArrayRes stringArrayId: Int): Drawable? {
        val drawables = getResources().obtainTypedArray(stringArrayId)
        val drawable = drawables.getDrawable(index)
        drawables.recycle()
        return drawable
    }

    fun getPermissionsState(
        activity: AppCompatActivity, requestCode: Int
    ) : Int {
        return when {
            (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) -> 2 // Both good

            (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) -> 1 // Only course

            (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION)) -> 0 // Never

            (ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_COARSE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED ||
                    ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_DENIED) -> -1 // Denied

            else -> requestCode // Need to request location permissions
        }
    }

    fun getResources(): Resources {
        return DiscoverApp.instance.resources
    }

    private fun getScreenSize(): Int {
       return (getResources().configuration.screenLayout
               and Configuration.SCREENLAYOUT_SIZE_MASK)
    }

    fun goFullscreen(
        activity: AppCompatActivity,
        @DrawableRes imageId: Int,
        imageName: String,
        landscape: Boolean = true
    ) {
        val intent = Intent(activity, ImagesActivity::class.java)
        intent.putExtra(FULLSCREEN_IMAGE_ID_EXTRA, imageId)
        intent.putExtra(FULLSCREEN_IMAGE_LANDSCAPE_EXTRA, landscape)
        intent.putExtra(FULLSCREEN_IMAGE_NAME_EXTRA, imageName)
        activity.startActivity(intent)
    }

    fun getWaypointColorId(
        position: Int, size: Int
    ) : Int {
        val colorId =
            when (position) {
                0 -> R.color.color_green
                size.minus(1) -> R.color.color_red
                else -> R.color.color_orange
            }
        val context = getAppContext()
        return ContextCompat.getColor(context, colorId)
    }

    fun goSystem(activity: AppCompatActivity) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${
                    activity.applicationContext.packageName
                }"))
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        activity.startActivity(intent)
    }

    fun goWaypoints(activity: AppCompatActivity, round: Int) {
        activity.startActivity(
            Intent(activity,
                when (round) {
                    Int.MIN_VALUE -> ChCh361Activity::class.java
                    in base1way..baseRet -> BasicActivity::class.java
                    else -> ExtendedActivity::class.java
                }
            )
        )
    }

    fun initViewTreeObserver(
        action: () -> Unit, fm: FragmentManager, @IdRes mapId: Int
    ) {
        val mapView = fm.findFragmentById(mapId)?.view
        if (mapView?.viewTreeObserver?.isAlive == true) {
            mapView.viewTreeObserver?.addOnGlobalLayoutListener(
                object: ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        mapView.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                        action()
                    }
                }
            )
        }
    }

//    fun isEmptyLocation(location: Location): Boolean {
//        return location.latitude == DEFAULT_LAT_AND_LNG ||
//               location.longitude == DEFAULT_LAT_AND_LNG
//    }

    fun isLandscape(): Boolean {
        return getScreenOrientation() == Configuration.ORIENTATION_LANDSCAPE
    }

    @Suppress("DEPRECATION")
    fun isSystemBrowserInstalled(): Boolean {
        var systemBrowserInstalled: Boolean

        val url = getResources().getString(R.string.tracks_and_safety)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        systemBrowserInstalled = // Check for Google Chrome or other browser
            getAppContext().packageManager?.resolveActivity(intent,0) != null

        if (!systemBrowserInstalled) {
            systemBrowserInstalled = // Alternatively, check for WebView
                try {
                    val webViewPackage = getResources().getString(R.string.web_view_package)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            getInstallerPackageName(webViewPackage)
                    } else {
                        getAppContext().packageManager.getInstallerPackageName(
                            webViewPackage
                        )
                    }
                    true
                } catch (ex: PackageManager.NameNotFoundException) {
                    false
                }
        }

        return systemBrowserInstalled
    }

    fun isXLargeScreen(): Boolean {
        return (getScreenSize() >= Configuration.SCREENLAYOUT_SIZE_XLARGE)
    }

    fun isXSmallScreen(): Boolean {
        return (getScreenSize() >= Configuration.SCREENLAYOUT_SIZE_SMALL)
    }

    @Suppress("AlwaysShowAction")
    fun setSortMenu(
        lifecycleOwner: LifecycleOwner,
        menuHost: MenuHost,
        @MenuRes menuRes: Int,
        setSortByAction: KFunction2<Int, Boolean, Unit>,
        setSortOrderAction: KFunction1<Boolean, Unit>,
        sortByPref: String,
        sortOrderPref: String,
    ) {
        val landscape = isLandscape()
        val routes = sortByPref == SORT_LISTS_BY_PREF
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(menuRes, menu)
                actionMenu = menu
                when (landscape) {
                    true -> {
                        val menuSortOrder = menu.findItem(R.id.menu_sort_order)
                        menuSortOrder.setShowAsActionFlags(SHOW_AS_ACTION_ALWAYS)
                        val ascending =
                            sharedPrefs.getBoolean(sortOrderPref, true)
                        setSortOrderIcon(ascending)
                    }
                    else -> {
                        MenuCompat.setGroupDividerEnabled(menu,true)
                    }
                }
            }

            override fun onPrepareMenu(menu: Menu) {
                val checkedId =
                    when (sharedPrefs.getInt(sortByPref, 0)) {
                        6 -> R.id.menu_distance
                        5 -> R.id.menu_time
                        4 -> when (routes) {
                            true -> R.id.menu_transport
                            else -> R.id.menu_shared
                        }
                        3 -> when (routes) {
                            true -> R.id.menu_shared
                            else -> R.id.menu_length
                        }
                        2 -> when (routes) {
                            true -> R.id.menu_dogs_ok
                            else -> R.id.menu_gradient
                        }
                        1 -> when (routes) {
                            true -> R.id.menu_accessibility
                            else -> R.id.menu_grade
                        }
                        else -> R.id.menu_name
                    }
                actionMenu?.findItem(checkedId)?.isChecked = true
                if (!landscape) {
                    actionMenu?.findItem(R.id.menu_sort_order)?.isChecked =
                        sharedPrefs.getBoolean(sortOrderPref, true)
                }
                menuHost.invalidateMenu()
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return  when (menuItem.itemId) {
                    R.id.menu_name -> {
                        setSortByAction(0, routes)
                        true
                    }
                    R.id.menu_accessibility -> {
                        setSortByAction(1, routes)
                        true
                    }
                    R.id.menu_grade -> {
                        setSortByAction(1, routes)
                        true
                    }
                    R.id.menu_dogs_ok -> {
                        setSortByAction(2, routes)
                        true
                    }
                    R.id.menu_gradient -> {
                        setSortByAction(2, routes)
                        true
                    }
                    R.id.menu_shared -> {
                        setSortByAction(
                            when (routes) {
                                true -> 3
                                else -> 4
                            },
                            routes
                        )
                        true
                    }
                    R.id.menu_length -> {
                        setSortByAction(3, routes)
                        true
                    }
                    R.id.menu_transport -> {
                        setSortByAction(4, routes)
                        true
                    }
                    R.id.menu_time -> {
                        setSortByAction(5, routes)
                        true
                    }
                    R.id.menu_distance -> {
                        setSortByAction(6, routes)
                        true
                    }
                    R.id.menu_sort_order -> {
                        setSortOrderAction(routes)
                        true
                    }
                    else -> false
                }
            }
             /* Although State.CREATED is preferred, RESUMED seems to
                avoid the flicker when switching between fragments */
        }, lifecycleOwner, Lifecycle.State.RESUMED)
    }

    fun setSortOrderIcon(ascending: Boolean) {
        val stringId: Int
        val drawableId =
            when (ascending) {
                true -> {
                    stringId = R.string.menu_sort_desc
                    R.drawable.ic_baseline_sort_white_24
                }
                else -> {
                    stringId = R.string.menu_sort_asc
                    R.drawable.ic_baseline_sort_reversed_white_24
                }
            }
        actionMenu?.findItem(R.id.menu_sort_order)?.apply {
            setIcon(drawableId)
            setTitle(stringId)
        }
    }

    fun showSystemDialog(
        activity: AppCompatActivity, @StringRes stringId: Int
    ) {
        AlertDialog.Builder(activity)
            .setIcon(R.drawable.ic_baseline_app_settings_alt_black_24)
            .setTitle(R.string.system_settings)
            .setMessage(stringId)
            .setNegativeButton(android.R.string.cancel,null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                goSystem(activity)
            }.show()
    }
}
