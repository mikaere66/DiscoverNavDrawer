package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.michaelrmossman.kotlin.discoverchristchurch.R

class TextOnlyInfoWindowAdapter(
    private val context: Context,
    @StringRes private val stringId: Int
) : GoogleMap.InfoWindowAdapter {

    override fun getInfoContents(marker: Marker): View? {
        val viewGroup: ViewGroup? = null
        val view =
            LayoutInflater.from(context).inflate(
                R.layout.marker_info_plain, viewGroup
            )
        view.findViewById<TextView>(R.id.text_view_title).text =
            marker.title
        view.findViewById<TextView>(R.id.text_view_snippet).text =
            String.format(
                context.getString(stringId),
                marker.snippet
            )
        return view
    }

    override fun getInfoWindow(marker: Marker?): View? {
        /* Return null to indicate that the default
           window (white bubble) should be used */
        return null
    }
}
