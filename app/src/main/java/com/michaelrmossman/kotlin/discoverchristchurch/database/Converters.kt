package com.michaelrmossman.kotlin.discoverchristchurch.database

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class Converters {

//    @TypeConverter
//    fun latLngToString(latLng: LatLng) : String {
//        return "${latLng.latitude}, ${latLng.longitude}"
//    }

    @TypeConverter
    fun stringToLatLng(latLng: String?): LatLng? {
        return when (latLng) {
            null -> null
            else -> {
                val split = latLng.split(",")
                LatLng(
                    split.first().trim().toDouble(),
                    split.last().trim().toDouble()
                )
            }
        }
    }
}
