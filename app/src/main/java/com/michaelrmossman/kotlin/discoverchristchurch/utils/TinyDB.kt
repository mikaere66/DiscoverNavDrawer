/*
 * Copyright 2014 KC Ochibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.text.TextUtils
import com.google.gson.Gson
import com.michaelrmossman.kotlin.discoverchristchurch.DiscoverApp.Companion.sharedPrefs
import com.michaelrmossman.kotlin.discoverchristchurch.entities.Favourite

object TinyDB {

    // Singleton
    // https://github.com/kcochibili/TinyDB--Android-Shared-Preferences-Turbo/pull/22/commits/7914233f53a2ca3f7f16c7d429162ded501b4a27
    // https://stackoverflow.com/questions/51834996/singleton-class-in-kotlin

    /**
     * null keys would corrupt the shared pref file and make them unreadable this is a preventive measure
     * @param key the pref key to check
     */
    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

    fun getListObjFaves(key: String?, mClass: Class<Favourite>?): List<Favourite> {
        val gson = Gson()

        val objStrings = getListString(key)
        val objects = mutableListOf<Favourite>()
        for (jObjString in objStrings) {
            val value = gson.fromJson(jObjString, mClass)
            objects.add(value)
        }

        return objects
    }

    /**
     * Get parsed List of String from SharedPreferences at 'key'
     * @param key SharedPreferences key
     * @return List of String
     */
    private fun getListString(key: String?): List<String> {
        return listOf(*TextUtils.split(sharedPrefs.getString(key, String()),"‚‗‚"))
    }
    /*
     *  The "‚‗‚" character is not a comma, it is the SINGLE LOW-9 QUOTATION MARK unicode 201A
     *  and unicode 2017 that are used for separating the items in a list.
     */

    fun putListObjFaves(key: String?, objArray: List<Favourite?>) {
        checkForNullKey(key)

        val gson = Gson()

        val objStrings = mutableListOf<String>()
        for (obj in objArray) {
            objStrings.add(gson.toJson(obj))
        }

        putListString(key, objStrings)
    }

    /**
     * Put List of String into SharedPreferences with 'key' and save
     * @param key SharedPreferences key
     * @param stringList List of String to be added
     */
    private fun putListString(key: String?, stringList: List<String>) {
        checkForNullKey(key)

        val myStringList = stringList.toTypedArray()

        sharedPrefs.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply()
    }
}
