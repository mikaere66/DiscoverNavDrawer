package com.michaelrmossman.kotlin.discoverchristchurch.activities

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.GoogleMap
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.databinding.ActivityCommonBinding
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ConvenienceKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ColorSingleIconRenderer
import com.michaelrmossman.kotlin.discoverchristchurch.utils.ITEM_VIEW_TYPE_ITEM_9
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getColorId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConveniencesActivity: CommunityBaseActivity<ActivityCommonBinding>(R.layout.activity_common) {

    private lateinit var conveniences: List<ConvenienceKt>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        communityIndex = ITEM_VIEW_TYPE_ITEM_9

        setTitleSubtitle(R.string.conveniences_activity_title)

        lifecycleScope.launch {
            conveniences = viewModel.getConveniencesList()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        super.onMapReady(googleMap)

        if (conveniences.isNotEmpty()) {
            addClusteredMarkers()
        }
    }

    private fun addClusteredMarkers() {
        val context = this@ConveniencesActivity
        clusterManager.apply {
            setRenderer(
                ColorSingleIconRenderer(
                    getColorId(),
                    context,
                    R.drawable.ic_baseline_wc_default_30,
                    map,this
                )
            )
        }
        for (i in conveniences.indices) {
            val snippet = getSnippet(conveniences[i].extra)
            val markerItem = getMarkerItem(
                conveniences[i].latLng, conveniences[i].name, snippet
            )
            allMarkerItems[conveniences[i].id] = markerItem
            clusterManager.addItem(markerItem)
            if (i == conveniences.size.minus(1)) {
                initClusterManager()
                when (val convenienceId = viewModel.currentConvenienceId) {
                    0L -> zoomOutMarkers(true)
                    else -> {
                        val convenienceKt: ConvenienceKt? = conveniences.stream()
                            .filter { convenience ->
                                convenience.id == convenienceId
                            }
                            .findFirst()
                            .orElse(null)
                        convenienceKt?.let { convenience ->
                            zoomInMarker(convenience.latLng)
                        }
                    }
                }
            }
        }
    }

    private fun getSnippet(extra: String?): String {
        return when (extra) {
            null -> getString(R.string.snippet_no_info)
            else -> extra
        }
    }
}
