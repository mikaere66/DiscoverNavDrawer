package com.michaelrmossman.kotlin.discoverchristchurch

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.view.Menu
import androidx.appcompat.app.AlertDialog
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SHARED_PREFERENCES_KEY
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DiscoverApp: Application() {

    companion object {
        var actionMenu: Menu? = null

        var alertDialog: AlertDialog? = null

        lateinit var instance: DiscoverApp

        val sharedPrefs: SharedPreferences by lazy {
            instance.getSharedPreferences(
                SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
