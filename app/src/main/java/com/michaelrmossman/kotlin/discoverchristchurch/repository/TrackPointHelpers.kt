package com.michaelrmossman.kotlin.discoverchristchurch.repository

import android.content.Context
import com.michaelrmossman.kotlin.discoverchristchurch.dao.DiscoverDao
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360
import com.michaelrmossman.kotlin.discoverchristchurch.entities.ChCh360Coords
import io.ticofab.androidgpxparser.parser.GPXParser
import io.ticofab.androidgpxparser.parser.domain.Gpx
import io.ticofab.androidgpxparser.parser.domain.Track
import io.ticofab.androidgpxparser.parser.domain.TrackPoint
import io.ticofab.androidgpxparser.parser.domain.TrackSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

object TrackPointHelpers {

    private fun getCoordinates(
        id: Long, track:Int, trackPoint: TrackPoint
    ) : ChCh360Coords {
        val lat = trackPoint.latitude
        val lng = trackPoint.longitude
        return ChCh360Coords(
            legId = id,
            trackId = track,
            latLng = "$lat, $lng"
        )
    }

    fun insertAllTrackPoints(
        appContext: Context, chCh360Items: List<ChCh360>, dao: DiscoverDao
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val coordinates = mutableListOf<ChCh360Coords>()
            val parser = GPXParser()
            runCatching {
                try {
                    chCh360Items.forEach { item ->
                        val parsedGpx: Gpx?
                        val inputStream: InputStream =
                            appContext.assets.open(item.filename)
                        parsedGpx = parser.parse(inputStream)
                        parsedGpx?.let { gpx ->
                            val tracks: List<Track> = gpx.tracks
                            for (i in tracks.indices) {
                                val segments: List<TrackSegment> =
                                    tracks[i].trackSegments
                                segments.forEach { segment ->
                                    val trackPoints = segment.trackPoints
                                    trackPoints.forEach { trackPoint ->
                                        coordinates.add(
                                            getCoordinates(item.id, i, trackPoint)
                                        )
                                    }
                                }
                            }
                        }
                    }

                } catch (e: XmlPullParserException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            // Log.d("HEY", coordinates.size.toString())

            dao.insertAllCoordinates(coordinates)
        }
    }
}
