package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.res.TypedArray
import android.graphics.Color
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.*
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getBikeTrackColorId
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getChCh360DescriptionText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getDogsExtra
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getDogParkFacilities
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getDogParkBylaw
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFormattedAreaQuantities
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFormattedDistance
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFormattedFaveAddedDate
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFormattedKilometres
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getFormattedTime
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getJourneyTypeIndicator
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getHeritageSiteAddress
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getParkPerimeterText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleBold
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getSpannableStyleSuperScript
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getStreetArtExtraText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getStreetViewLinkText
import com.michaelrmossman.kotlin.discoverchristchurch.utils.UiUtils.getWarning
import java.util.*

/**
 * Binding adapters used throughout the app
 */

@BindingAdapter("imageIndex", "images")
fun setImageResource( // BikeTrack grade|CommunityItem
    imageView: ImageView, imageIndex: Int, images: TypedArray
) {
    imageView.setImageResource(
        images.getResourceId(imageIndex,0)
    )
}

@BindingAdapter("areaAccessibilityIcon")
fun ImageView.setAreaAccessibilityIcon(accessible: Int) {
    val colorId = when (accessible) {
        in 1..2 -> R.color.color_orange
        else -> R.color.color_red
    }
    val drawableId = when (accessible) {
        2 -> R.drawable.ic_baseline_accessible_default_18
        1 -> R.drawable.ic_baseline_stroller_default_18
        else -> R.drawable.ic_baseline_not_accessible_default_18
    }
    val color =
        ContextCompat.getColor(context, colorId)
    setColorFilter(color)
    setImageResource(drawableId)
}

@BindingAdapter("areaPlaceDogsAllowedColor")
fun ImageView.setAreaPlaceDogsAllowedColor(dogs: Int) {
    val colorId = when (dogs) {
        2 -> R.color.color_green
        1 -> R.color.color_orange
        else -> R.color.color_red
    }
    val color =
        ContextCompat.getColor(context, colorId)
    setColorFilter(color)
}

@BindingAdapter("areaPlaceLinkedRoutesColor")
fun ImageView.setAreaPlaceLinkedRoutesColor(linked: Int) {
    val colorId = when (linked) {
        2 -> R.color.color_green
        1 -> R.color.color_orange
        else -> R.color.color_red
    }
    val color =
        ContextCompat.getColor(context, colorId)
    setColorFilter(color)
}

@BindingAdapter("areaPlaceSharedUseColor")
fun ImageView.setAreaPlaceSharedUseColor(shared: Int) {
    val colorId = when (shared) {
        2 -> R.color.color_green
        1 -> R.color.color_orange
        else -> R.color.color_red
    }
    val color =
        ContextCompat.getColor(context, colorId)
    setColorFilter(color)
}

@BindingAdapter("areaPlaceTransportReqdColor")
fun ImageView.setAreaPlaceTransportReqdColor(transport: Int) {
    val colorId = when (transport) {
        2 -> R.color.color_green
        1 -> R.color.color_orange
        else -> R.color.color_red
    }
    val color =
        ContextCompat.getColor(context, colorId)
    setColorFilter(color)
}

@BindingAdapter("batteryRecyclerColor")
fun ImageView.setBatteryRecyclerColor(batteryRecycler: BatteryRecyclerKt?) {
    batteryRecycler?.let {
        val color = Color.parseColor(it.color)
        setColorFilter(color)
    }
}

@BindingAdapter("bikeTrackArrowLandscape")
fun ImageView.setBikeTrackArrowLandscape(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_baseline_arrow_circle_down_default_24
                        qty.minus(1) -> R.drawable.ic_baseline_arrow_circle_up_default_24
                        else -> R.drawable.ic_outline_swap_vertical_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("bikeTrackArrowPortrait")
fun ImageView.setBikeTrackArrowPortrait(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_outline_arrow_circle_right_default_24
                        qty.minus(1) -> R.drawable.ic_outline_arrow_circle_left_default_24
                        else -> R.drawable.ic_outline_swap_horizontal_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("bikeTrackIconColor")
fun ImageView.setBikeTrackIconColor(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        val index = it.shared
        val colorId =
            getBikeTrackColorId(context, index, R.array.bike_track_colors)
        setColorFilter(colorId)
    }
}

@BindingAdapter("bikeTrackIcon")
fun ImageView.setBikeTrackIcon(gradient: Double) {
    val drawableId = when (gradient) {
        in 0.1..30.0 -> R.drawable.ic_baseline_trending_up_black_24
        in -30.0..-0.1 -> R.drawable.ic_baseline_trending_down_black_24
        else -> R.drawable.ic_baseline_trending_flat_black_24 // 0.0
    }
    setImageResource(drawableId)
}

@BindingAdapter("chCh360BackgroundColor")
fun ImageView.setChCh360BackgroundColor(chCh360: ChCh360Kt?) {
    chCh360?.let {
        val color = Color.parseColor(it.color)
        setBackgroundColor(color)
    }
}

@BindingAdapter("chCh360IconColor")
fun ImageView.setChCh360IconColor(chCh360: ChCh360Kt?) {
    chCh360?.let {
        val color = Color.parseColor(it.color)
        setColorFilter(color)
    }
}

@BindingAdapter("convenienceTypeIcon")
fun ImageView.setConvenienceTypeIcon(typeId: Long) {
    val drawableId =
        when (typeId) {
            1L -> R.drawable.ic_baseline_family_restroom_black_24
            else -> R.drawable.ic_baseline_wc_black_24
        }
    setImageResource(drawableId)
}

@BindingAdapter("distanceTimeIcon")
fun ImageView.setDistanceTimeIcon(route: RouteKt?) {
    route?.let {
        val drawable =
            getJourneyTypeIndicator(context, it.round)
        setImageDrawable(drawable)
    }
}

@BindingAdapter("dogParkArrowLandscape")
fun ImageView.setDogParkArrowLandscape(dogPark: DogParkKt?) {
    dogPark?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_baseline_arrow_circle_down_default_24
                        qty.minus(1) -> R.drawable.ic_baseline_arrow_circle_up_default_24
                        else -> R.drawable.ic_outline_swap_vertical_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("dogParkArrowPortrait")
fun ImageView.setDogParkArrowPortrait(dogPark: DogParkKt?) {
    dogPark?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_outline_arrow_circle_right_default_24
                        qty.minus(1) -> R.drawable.ic_outline_arrow_circle_left_default_24
                        else -> R.drawable.ic_outline_swap_horizontal_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("dogParkIconColor")
fun ImageView.setDogParkIconColor(dogPark: DogParkKt?) {
    dogPark?.let {
        val color = Color.parseColor(it.color)
        setColorFilter(color)
    }
}

@BindingAdapter("facilityTypeIconColor")
fun ImageView.setFacilityTypeIconColor(facility: FacilityKt?) {
    facility?.let {
        val color = Color.parseColor(it.color)
        setColorFilter(color)
    }
}

@BindingAdapter("fruitTypeIconColor")
fun ImageView.setFruitTypeIconColor(fruitType: FruitTypeKt?) {
    fruitType?.let {
        val color = Color.parseColor(it.color)
        setColorFilter(color)
    }
}

@BindingAdapter("heritageSiteArrowLandscape")
fun ImageView.setHeritageSiteArrowLandscape(heritageSite: HeritageSiteKt?) {
    heritageSite?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_baseline_arrow_circle_down_default_24
                        qty.minus(1) -> R.drawable.ic_baseline_arrow_circle_up_default_24
                        else -> R.drawable.ic_outline_swap_vertical_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("heritageSiteArrowPortrait")
fun ImageView.setHeritageSiteArrowPortrait(heritageSite: HeritageSiteKt?) {
    heritageSite?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_outline_arrow_circle_right_default_24
                        qty.minus(1) -> R.drawable.ic_outline_arrow_circle_left_default_24
                        else -> R.drawable.ic_outline_swap_horizontal_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("journeyType")
fun ImageView.setJourneyType(route: RouteKt?) {
    route?.let {
        val drawable =
            getJourneyTypeIndicator(context, it.round)
        setImageDrawable(drawable)
    }
}

@BindingAdapter("parkTypeIconColor")
fun ImageView.setParkTypeIconColor(park: ParkKt?) {
    park?.let {
        val color = Color.parseColor(it.border) // Note: border
        setColorFilter(color)
    }
}

@BindingAdapter("placeAccessibilityIcon")
fun ImageView.setPlaceAccessibilityIcon(place: PlaceKt?) {
    place?.let {
        val colorId = when (it.accessibilityColor) {
            3 -> R.color.color_green
            in 1..2 -> R.color.color_orange
            else -> R.color.color_red
        }
        val color =
            ContextCompat.getColor(context, colorId)
        val drawableId = when (it.accessibilityIcon) {
            in 2..3 -> R.drawable.ic_baseline_accessible_default_18
            1 -> R.drawable.ic_baseline_stroller_default_18
            else -> R.drawable.ic_baseline_not_accessible_default_18
        }
        setColorFilter(color)
        setImageResource(drawableId)
    }
}

@BindingAdapter("routeAccessibilityColor")
fun ImageView.setRouteAccessibilityColor(route: RouteKt?) {
    route?.let {
        val colorId = when (it.accessible) {
            2 -> R.color.color_green
            1 -> R.color.color_orange
            else -> R.color.color_red
        }
        val drawableId = when (it.accessible) {
            2 -> R.drawable.ic_baseline_accessible_default_18
            1 -> R.drawable.ic_baseline_stroller_default_18
            else -> R.drawable.ic_baseline_not_accessible_default_18
        }
        val color =
            ContextCompat.getColor(context, colorId)
        setColorFilter(color)
        setImageResource(drawableId)
    }
}

@BindingAdapter("routeDogsAllowedColor")
fun ImageView.setRouteDogsAllowedColor(route: RouteKt?) {
    route?.let {
        val colorId = when (it.dogs) {
            2 -> R.color.color_green
            1 -> R.color.color_orange
            else -> R.color.color_red
        }
        val color =
            ContextCompat.getColor(context, colorId)
        setColorFilter(color)
    }
}

@BindingAdapter("routesLinkedColor")
fun ImageView.setRoutesLinkedColor(route: RouteKt?) {
    route?.let {
        val colorId = when (it.linkedIds) {
            null -> R.color.color_red
            else -> R.color.color_green
        }
        val color =
            ContextCompat.getColor(context, colorId)
        setColorFilter(color)
    }
}

@BindingAdapter("routeSharedUseColor")
fun ImageView.setRouteSharedUseColor(route: RouteKt?) {
    route?.let {
        val colorId = when (it.shared) {
            1 -> R.color.color_green
            else -> R.color.color_red
        }
        val color =
            ContextCompat.getColor(context, colorId)
        setColorFilter(color)
    }
}

@BindingAdapter("routeTransportReqdColor")
fun ImageView.setTransportReqdColor(route: RouteKt?) {
    route?.let {
        val colorId = when (it.transport) {
            true -> R.color.color_red
            else -> R.color.color_green
        }
        val color =
            ContextCompat.getColor(context, colorId)
        setColorFilter(color)
    }
}

@BindingAdapter("streetArtArrowLandscape")
fun ImageView.setStreetArtArrowLandscape(streetArt: StreetArtKt?) {
    streetArt?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_baseline_arrow_circle_down_default_24
                        qty.minus(1) -> R.drawable.ic_baseline_arrow_circle_up_default_24
                        else -> R.drawable.ic_outline_swap_vertical_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("streetArtArrowPortrait")
fun ImageView.setStreetArtArrowPortrait(streetArt: StreetArtKt?) {
    streetArt?.let {
        it.colorId?.let { color ->
            it.position?.let { pos ->
                it.size?.let { qty ->
                    val drawableId = when (pos) {
                        0 -> R.drawable.ic_outline_arrow_circle_right_default_24
                        qty.minus(1) -> R.drawable.ic_outline_arrow_circle_left_default_24
                        else -> R.drawable.ic_outline_swap_horizontal_circle_default_24
                    }
                    setColorFilter(color)
                    setImageResource(drawableId)
                }
            }
        }
    }
}

@BindingAdapter("waypointArrowLandscape")
fun ImageView.setWaypointArrowLandscape(waypoint: WaypointKt?) {
    waypoint?.let {
        val drawableId = when (it.position) {
            0 -> R.drawable.ic_baseline_arrow_circle_down_default_24
            it.size.minus(1) -> R.drawable.ic_baseline_arrow_circle_up_default_24
            else -> R.drawable.ic_outline_swap_vertical_circle_default_24
        }
        setColorFilter(waypoint.colorId)
        setImageResource(drawableId)
    }
}

@BindingAdapter("waypointArrowPortrait")
fun ImageView.setWaypointArrowPortrait(waypoint: WaypointKt?) {
    waypoint?.let {
        val drawableId = when (it.position) {
            0 -> R.drawable.ic_outline_arrow_circle_right_default_24
            it.size.minus(1) -> R.drawable.ic_outline_arrow_circle_left_default_24
            else -> R.drawable.ic_outline_swap_horizontal_circle_default_24
        }
        setColorFilter(waypoint.colorId)
        setImageResource(drawableId)
    }
}

@BindingAdapter("accessText")
fun TextView.setAccessText(dogPark: DogParkKt?) {
    dogPark?.let {
        isVisible = it.startPoint?.isNotBlank() == true
        if (it.startPoint?.isNotBlank() == true) {
            text = String.format(
                context.getString(R.string.access_point),
                it.startPoint
            )
        }
    }
}

@BindingAdapter("areaQuantities")
fun TextView.setAreaQuantities(area: AreaKt?) {
    area?.let {
        text = getFormattedAreaQuantities(
            it, context.resources
        )
    }
}

@BindingAdapter("bikeTrackColorText")
fun TextView.setBikeTrackColorText(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        val colours =
            context.resources.getStringArray(R.array.bike_track_colors_text)
        val colour = colours[it.shared]
        text = String.format(
            context.getString(R.string.bike_track_color),
            colour
        )
    }
}

@BindingAdapter("bikeTrackDifference")
fun TextView.setBikeTrackDifference(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        val difference = it.highest.minus(it.lowest)
        text = String.format(
            context.getString(R.string.bike_track_diff),
            difference
        )
    }
}

@BindingAdapter("bikeTrackGradient")
fun TextView.setBikeTrackGradient(gradient: Double) {
    text = when (gradient) {
        0.0 -> context.getString(R.string.unspecified)
        else -> String.format(
            context.getString(R.string.bike_track_gradient),
            gradient
        )
    }
}

@BindingAdapter("bikeTrackLength")
fun TextView.setBikeTrackLength(length: Double) {
    text = String.format(
        context.getString(R.string.bike_track_length),
        getFormattedKilometres(length)
    )
}

@BindingAdapter("bikeTrackShared")
fun TextView.setBikeTrackShared(shared: Int) {
    val array =
        context.resources.getStringArray(R.array.bike_track_shared)
    text = String.format(
        context.getString(R.string.bike_track_shared),
        array[shared]
    )
}

//@BindingAdapter("bikeTrackSubtitle")
//fun TextView.setBikeTrackSubtitle(subtitle: String?) {
//    subtitle?.let { text = it }
//}

@BindingAdapter("bikeTrackWarningText")
fun TextView.setBikeTrackWarningText(bikeTrack: BikeTrackKt?) {
    bikeTrack?.let {
        isVisible = it.notes?.isNotBlank() == true
        it.notes?.let { notes ->
            text = notes
        }
    }
}

@BindingAdapter("convenienceExtra")
fun TextView.setConvenienceExtra(
    convenience: ConvenienceKt?
) {
    convenience?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

@BindingAdapter("convenienceHours")
fun TextView.setConvenienceHours(
    convenience: ConvenienceKt?
) {
    convenience?.let {
        isVisible =
            it.hours?.isNotBlank() == true
        if (it.hours?.isNotBlank() == true) {
            text = it.hours
        }
    }
}

@BindingAdapter("conveniencesKt")
fun TextView.setConveniencesKt(route: RouteKt?) {
    route?.let {
        isVisible =
            it.conveniences?.isNotBlank() == true
        if (it.conveniences?.isNotBlank() == true) {
            text = it.conveniences
        }
    }
}

@BindingAdapter("directions")
fun TextView.setDirections(waypoint: WaypointKt?) {
    waypoint?.let {
        isVisible =
            it.directions?.isNotBlank() == true
        if (it.directions?.isNotBlank() == true) {
            text = it.directions
        }
    }
}

@BindingAdapter("distance")
fun TextView.setDistance(route: RouteKt?) {
    route?.let {
        text = getFormattedDistance(it.distance)
    }
}

// Note that this function is totally diff from below!
@BindingAdapter("distanceTimeKt")
fun TextView.setDistanceTime(route: RouteKt?) {
    route?.let {
        val drawable =
            getJourneyTypeIndicator(context, it.round)
        setCompoundDrawablesWithIntrinsicBounds(
            drawable,null,null,null
        )

        val sb = StringBuilder()
        val distance = getFormattedDistance(it.distance)
        val distanceText =
            String.format(context.getString(R.string.route_distance),
                context.getString(R.string.journey_types),
                    distance)
        sb.append(distanceText)
        sb.append(" ~ ")
        val timeText = getFormattedTime(it.time)
        sb.append(timeText)

        text = sb.toString()
    }
}

// Note that this function is totally diff from above!
@BindingAdapter("distanceTimeText")
fun TextView.setDistanceTimeText(route: RouteKt?) {
    route?.let {
        val sb = StringBuilder()
        val distance = getFormattedDistance(it.distance)
        val distanceText =
            String.format(context.getString(R.string.route_distance),
                context.getString(R.string.journey_types),
                    distance)
        sb.append(distanceText)
        sb.append(" ~ ")
        val timeText = getFormattedTime(it.time)
        sb.append(timeText)
        text = sb.toString()
    }
}

@BindingAdapter("dogParkBylaw")
fun TextView.setDogParkBylaw(dogPark: DogParkKt?) {
    dogPark?.let {
        text = getDogParkBylaw(it.typeId.toInt())
    }
}

@BindingAdapter("dogParkDetails")
fun TextView.setDogParkDetails(dogPark: DogParkKt?) {
    dogPark?.let {
        text = when (it.details?.isNotBlank() == true) {
            true -> it.details
            // e.g. Bottle Lake Forest Park|Rawhiti Domain
            else -> {
                val stringId =
                    when (it.typeId) {
                        8L -> R.string.generic_dog_exercise
                        else -> R.string.generic_dog_park
                    }
                context.getString(stringId)
            }
        }
    }
}

@BindingAdapter("dogParkDrawableColor")
fun TextView.setDogParkDrawableColor(dogPark: DogParkKt?) {
    dogPark?.let {
        val color = Color.parseColor(it.color)
        for (drawable in compoundDrawablesRelative) {
            drawable?.setTint(color)
        }
    }
}

@BindingAdapter("dogParkExtra")
fun TextView.setDogParkExtra(dogPark: DogParkKt?) {
    dogPark?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

//@BindingAdapter("dogParkSubtitle")
//fun TextView.setDogParkSubtitle(subtitle: String?) {
//    subtitle?.let { text = it }
//}

@BindingAdapter("dogsExtraKt")
fun TextView.setDogsExtra(route: RouteKt?) {
    route?.let {
        text = getDogsExtra(it.dogsExtra)
    }
}

@BindingAdapter("facilitiesText")
fun TextView.setFacilitiesText(dogPark: DogParkKt?) {
    dogPark?.let {
        isVisible = it.dogFacilities?.isNotBlank() == true
        if (it.dogFacilities?.isNotBlank() == true) {
            text = getDogParkFacilities(context, it.dogFacilities)
        }
    }
}

// Note that facilitiesText above is NOT related to this Facility item
@BindingAdapter("facilityExtra")
fun TextView.setFacilityExtra(facility: FacilityKt?) {
    facility?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

@BindingAdapter("faveAddedDate")
fun TextView.setFaveAddedDate(millis: Long) {
    if (millis > 0L) {
        text = getFormattedFaveAddedDate(
            context, millis
        )
    }
}

@BindingAdapter("fountainExtra")
fun TextView.setFountainExtra(
    fountain: DrinkFountainKt?
) {
    fountain?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

@BindingAdapter("freeWiFiExtra")
fun TextView.setFreeWiFiExtra(
    freeWiFi: FreeWiFiKt?
) {
    freeWiFi?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

@BindingAdapter("heritageSiteAddress") // Only landscape
fun TextView.setHeritageSiteAddress(site: HeritageSiteKt?) {
    site?.let { text = getHeritageSiteAddress(it.address) }
}

@BindingAdapter("heritageSiteDrawableColor")
fun TextView.setHeritageSiteDrawableColor(site: HeritageSiteKt?) {
    site?.let {
        val color = Color.parseColor(it.color)
        for (drawable in compoundDrawablesRelative) {
            drawable?.setTint(color)
        }
    }
}

//@BindingAdapter("heritageSiteSubtitle")
//fun TextView.setHeritageSiteSubtitle(subtitle: String?) {
//    subtitle?.let { text = it }
//}

@BindingAdapter("infoDogParkStreetViewLink")
fun TextView.setInfoDogParkStreetViewLink(dogPark: DogParkKt?) {
    dogPark?.let {
        isEnabled = it.angle != -1
        text = getStreetViewLinkText(it.angle, context)
    }
}

@BindingAdapter("infoHeritageSiteStreetViewLink")
fun TextView.setInfoHeritageSiteStreetViewLink(site: HeritageSiteKt?) {
    site?.let {
        isEnabled = it.angle != -1
        text = getStreetViewLinkText(it.angle, context)
    }
}

@BindingAdapter("infoMegaStatic")
fun TextView.setInfoMegaStatic(chCh360: ChCh360Kt?) {
    chCh360?.let {
        text = getChCh360DescriptionText(
            2,true, it.description
        )
    }
}

@BindingAdapter("infoMegaToggled")
fun TextView.setInfoMegaToggled(chCh360: ChCh360Kt?) {
    chCh360?.let {
        text = getChCh360DescriptionText(
            2,false, it.description
        )
    }
}

@BindingAdapter("infoMegaStreetViewLink")
fun TextView.setInfoMegaStreetViewLink(chCh360: ChCh360Kt?) {
    chCh360?.let {
        isEnabled = it.angle != -1
        text = getStreetViewLinkText(it.angle, context)
    }
}

@BindingAdapter("infoMiniStreetViewLink")
fun TextView.setInfoMiniStreetViewLink(waypoint: WaypointKt?) {
    waypoint?.let {
        isEnabled = it.angle != -1
        text = getStreetViewLinkText(it.angle, context)
    }
}

@BindingAdapter("introChCh360First")
fun TextView.setIntroChCh360First(chCh360: ChCh360Kt?) {
    chCh360?.let {
        val paragraph = getChCh360DescriptionText(
            1,true, it.intro
        )
        val boldText = it.leg // e.g. "The <bold>"..."</bold> leg"
        text = getSpannableStyleBold(boldText, paragraph)
    }
}

@BindingAdapter("introChCh360Subs")
fun TextView.setIntroChCh360Subs(chCh360: ChCh360Kt?) {
    chCh360?.let {
        text = getChCh360DescriptionText(
            1,false, it.intro
        )
    }
}

@BindingAdapter("introKt")
fun TextView.setIntro(route: RouteKt?) {
    route?.let {
        isVisible =
            it.intro?.isNotBlank() == true
        if (it.intro?.isNotBlank() == true) {
            text = it.intro
        }
    }
}

@BindingAdapter("introOrStartPoint")
fun TextView.setIntroOrStartPoint(route: RouteKt?) {
    route?.let {
        text =
            when (route.intro == null) {
                true -> route.start
                else -> route.intro
            }
    }
}

@BindingAdapter("noteText")
fun TextView.setNoteText(dogPark: DogParkKt?) {
    dogPark?.let {
        isVisible =
            it.dogNote?.isNotBlank() == true
        if (it.dogNote?.isNotBlank() == true) {
            text = it.dogNote
        }
    }
}

@BindingAdapter("nullableText")
fun TextView.setNullableText(string: String?) {
    string?.let {
        isVisible = it.isNotBlank() == true
        if (it.isNotBlank()) {
            text = it
        }
    }
}

@BindingAdapter("parkArea")
fun TextView.setParkArea(park: ParkKt?) {
    park?.let {
        text =
            getSpannableStyleSuperScript(
                it.area,
                context.getString(R.string.park_area)
            )
    }
}

@BindingAdapter("parkPerimeter")
fun TextView.setParkPerimeter(park: ParkKt?) {
    park?.let {
        text =
            getParkPerimeterText(
                it.perimeter,
                context.getString(R.string.park_perimeter)
            )
    }
}

@BindingAdapter("streetArtCredit")
fun TextView.setStreetArtCredit(streetArt: StreetArtKt?) {
    streetArt?.let {
        isVisible = it.credit?.isNotBlank() == true
        if (it.credit?.isNotBlank() == true) {
            text = String.format(
                context.getString(R.string.street_art_credit),
                it.credit
            )
        }
    }
}

@BindingAdapter("streetArtDate")
fun TextView.setStreetArtDate(streetArt: StreetArtKt?) {
    streetArt?.let {
        isVisible = it.date?.isNotBlank() == true
        if (it.date?.isNotBlank() == true) {
            text = String.format(
                context.getString(R.string.street_art_date),
                it.date
            )
        }
    }
}

@BindingAdapter("streetArtDescription")
fun TextView.setStreetArtDescription(streetArt: StreetArtKt?) {
    streetArt?.let {
        isVisible =
            it.description?.isNotBlank() == true
        if (it.description?.isNotBlank() == true) {
            text = it.description
        }
    }
}

@BindingAdapter("streetArtExtra")
fun TextView.setStreetArtExtra(streetArt: StreetArtKt?) {
    streetArt?.let {
        isVisible =
            it.extra?.isNotBlank() == true
        if (it.extra?.isNotBlank() == true) {
            text = it.extra
        }
    }
}

@BindingAdapter("streetArtExtraKt")
fun TextView.setStreetArtExtraKt(streetArt: StreetArtKt?) {
    streetArt?.let {
        text = getStreetArtExtraText(context,false, it)
    }
}

//@BindingAdapter("streetArtSubtitle")
//fun TextView.setStreetArtSubtitle(subtitle: String?) {
//    subtitle?.let { text = it }
//}

@BindingAdapter("streetArtViewable")
fun TextView.setStreetArtViewable(streetArt: StreetArtKt?) {
    streetArt?.let {
        isVisible = it.viewableId > 0
        if (it.viewableId > 0) {
            val stringArray =
                context.resources.getStringArray(R.array.street_art_viewable)
            val stringId = stringArray[it.viewableId]
            text = String.format(
                context.getString(R.string.street_art_viewable),
                stringId
            )
        }
    }
}

@BindingAdapter("subtitleKt")
fun TextView.setSubtitle(route: RouteKt?) {
    route?.let {
        val stringId = when (it.shared) {
            1 -> R.string.info_shared_subtitle
            else -> {
                when (it.round) {
                    in base1way..baseRet -> {
                        R.string.info_basic_subtitle
                    }
                    else -> R.string.info_extended_subtitle
                }
            }
        }
        text = context.getString(stringId)
    }
}

@BindingAdapter("time")
fun TextView.setTime(route: RouteKt?) {
    route?.let {
        text = getFormattedTime(it.time)
    }
}

@BindingAdapter("warningText")
fun TextView.setWarningText(route: RouteKt?) {
    route?.let {
        isVisible = it.warning > 0
        if (it.warning > 0) {
            text = getWarning(it.warning)
        }
    }
}

@BindingAdapter("waypointSubtitle")
fun TextView.setWaypointSubtitle(subtitle: String?) {
    subtitle?.let { text = it }
}
