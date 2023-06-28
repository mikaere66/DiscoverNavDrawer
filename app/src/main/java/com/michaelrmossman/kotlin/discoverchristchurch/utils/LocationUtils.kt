/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

const val locationInterval = 3000L
const val locationFastestInterval = 2000L
const val locationMaxWaitTime = 100L

/**
 * Helper functions used throughout the app, relating directly to Maps
 */
object LocationUtils {

//    fun createLocationRequest() = LocationRequest.create().apply {
//        interval = 3000
//        fastestInterval = 2000
//        priority = Priority.PRIORITY_HIGH_ACCURACY
//        maxWaitTime = 100 //
//    }

    fun createLocationRequest() = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, locationInterval
        )
        .setWaitForAccurateLocation(false)
        .setMinUpdateIntervalMillis(locationFastestInterval)
        .setMaxUpdateDelayMillis(locationMaxWaitTime)
        .build()

    fun Location.asString(format: Int = Location.FORMAT_DEGREES): String {
        val latitude = Location.convert(latitude, format)
        val longitude = Location.convert(longitude, format)
        return "Location is: $latitude, $longitude"
    }

    @SuppressLint("MissingPermission")
    suspend fun FusedLocationProviderClient.awaitLastLocation(): Location =
        suspendCancellableCoroutine { continuation ->
            lastLocation.addOnSuccessListener { location ->
                continuation.resume(location)
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }
}
