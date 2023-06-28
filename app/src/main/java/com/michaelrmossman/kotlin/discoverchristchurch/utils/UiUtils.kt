package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SuperscriptSpan
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.constraintlayout.widget.Group
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Slide
import androidx.transition.TransitionManager
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.entities.AreaKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.FruitCatKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.StreetArtKt
import com.michaelrmossman.kotlin.discoverchristchurch.entities.WaypointKt
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getAppContext
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getResources
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.isLandscape
import spencerstudios.com.fab_toast.FabToast
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Helper functions used throughout the app, relating directly to the User Interface
 */
object UiUtils {

    fun contentDescrToast(
        index: Int,
        waypoint: WaypointKt? = null,
        waypoints: List<WaypointKt> = listOf()
    ) {
        val string: String? = when (index) {
            0 -> {
                waypoint?.let {
                    val stringId = when (waypoint.position ==
                            waypoints[waypoints.size.minus(1)].position) {
                        true -> R.string.scroll_to_prev_descr
                        else -> R.string.scroll_to_next_descr
                    }
                    getResources().getString(stringId)
                }
            }
            else -> {
                val stringArray =
                    getResources().getStringArray(R.array.content_descr)
                stringArray[index.minus(1)]
            }
        }
        string?.let { fancyToast(getAppContext(),2, it) }
    }

    fun fancyToast(
        context: Context, format: Int, @StringRes
        stringId: Int, coroutine: Boolean = false
    ) {
        fancyToast(context, format,
            context.getString(stringId), coroutine)
    }

    fun fancyToast(
        context: Context, format: Int, string:
        String, coroutine: Boolean = false
    ) {
        // Format :   Success, Information, Error, Warning (1-4)
        // Duration : Short = 0, Long = 1
        // Position : Default, Center, Top (1-3)
        val duration =
            when (format) {
                4 -> Toast.LENGTH_LONG
                else -> Toast.LENGTH_SHORT
            }

        if (coroutine) { // If called by a suspend function
            Handler(Looper.getMainLooper()).post {
                FabToast.makeText(context,
                    string, duration, format,2).show()
            }

        } else FabToast.makeText(context,
            string, duration, format,2).show()
    }

    fun getAccessPointPositionText(position: Int, size: Int): String {
        val res = getResources()
        return when (position) {
            size -> res.getString(R.string.ch_ch_360_end)
            else -> String.format(
                res.getString(R.string.ch_ch_360_a_p_count),
                position, size
            )
        }
    }

    fun getBikeTrackColorId(
        context: Context, index: Int, @ArrayRes intArray: Int
    ) : Int {
        val array = context.resources.getIntArray(intArray)
        return array[index]
    }

    fun getChCh360DescriptionText(
        paragraphs: Int, static: Boolean, text: String
    ) : String {
        val sb = StringBuilder()
        for (i in 0 until paragraphs) {
            sb.append("\n")
        }
        // Static vs Toggled refers to shown/hidden text
        return when (val index = text.indexOf(sb.toString())) {
            -1 -> text
            else -> {
                when (static) {
                    true -> text.substring(0, index)
                    else -> text.substring(index)
                }
            }
        }
    }

    fun getChCh360PositionText(position: Int, size: Int): String {
        val res = getResources()
        return String.format(res.getString(R.string.ch_ch_360_count),
                position, size)
    }

    fun getDogsExtra(index: Int): String {
        val dogsExtra =
            getResources().getStringArray(R.array.route_dogs)
        return (dogsExtra[index])
    }

    fun getDogParkBylaw(type: Int): String {
        val bylawsArray =
            getResources().getStringArray(R.array.dog_park_bylaws)
        // Remember, type IDs start at 1
        return bylawsArray[type.minus(1)]
    }

    fun getDogParkBylawWithQuantity(type: Int, qty: Int): String {
        val bylaw = getDogParkBylaw(type)
        return "$bylaw ($qty)"
    }

    fun getDogParkFacilities(
        context: Context, dogFacilities: String
    ) : String {
        val sb = StringBuilder()
        sb.append(context.getString(R.string.park_facilities))
        val facilitiesSplit = dogFacilities.split(";")
        facilitiesSplit.forEach { facility ->
            sb.append(
                String.format(
                    context.getString(R.string.bullet_point),
                    facility.trim()
                )
            )
        }
        return sb.toString()
    }

    private fun getFacilityType(typeId: Int): String {
        val typesArray =
            getResources().getStringArray(R.array.facility_types)
        // Remember, type IDs start at 1
        return typesArray[typeId.minus(1)]
    }

    fun getFacilityTypeWithQuantity(typeId: Int, qty: Int): String {
        val type = getFacilityType(typeId)
        return "$type ($qty)"
    }

    fun getFavouritesMessagePair(result: Pair<Long, Int>): Pair<Int, Int> {
        val format: Int
        val stringId = when (result.first) {
            in 1L..Long.MAX_VALUE -> {
                format = 1
                R.string.fave_added
            }
            in Long.MIN_VALUE..-1L -> {
                when (result.second) {
                    0 -> {
                        format = 4
                        R.string.faves_gone
                    }
                    else -> {
                        format = 2
                        R.string.fave_removed
                    }
                }
            }
            else -> {
                format = 3
                R.string.fave_failed
            }
        }
        return Pair(format, stringId)
    }

    fun getFormattedAreaQuantities(
        area: AreaKt, resources: Resources
    ) : String {
        val placesString =
            resources.getQuantityString(R.plurals.places_quantity,
                area.places)
        val routesString =
            resources.getQuantityString(R.plurals.routes_quantity,
                area.routes)
        val places = String.format(placesString, area.places)
        val routes = String.format(routesString, area.routes)
        return "$places | $routes"
    }

    fun getFormattedDistance(distance: Int): String {
        return when (distance) {
            0 -> "Variable"
            in 1..999 -> {
                String.format(
                    Locale.getDefault(),
                    "%d mtrs",
                    distance
                )
            }
            else -> {
                val kilometres =
                    distance.div(1_000.toDouble())
                val kms =
                    when (kilometres > 1.0) {
                        true -> "kms"
                        else -> "km"
                    }
                val format =
                    when (distance % 100) {
                        0 -> "%.1f $kms"
                        else -> "%.2f $kms"
                    }
                String.format(
                    Locale.getDefault(),
                    format,
                    kilometres
                )
            }
        }
    }

    fun getFormattedKilometres(metres: Double): String {
        return getFormattedDistance(
            metres.times(1_000).toInt()
        )
    }

    fun getFormattedFaveAddedDate(
        context: Context, millis: Long
    ) : String {
        val dateString = SimpleDateFormat(
            FAVE_DATE_FORMAT, Locale.getDefault()
        ).format(Date(millis))
        val stringRes = context.getString(R.string.date_added)
        val time = Calendar.getInstance()
        time.timeInMillis = millis
        val amPm = when (time.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "am"
            else -> "pm"
        }
        return String.format(stringRes, dateString, amPm)
    }

    fun getFormattedTime(time: Int): String {
        return when (time < 60) {
            true -> {
                val oneMin =
                    TimeUnit.MILLISECONDS.convert(1,
                        TimeUnit.MINUTES)
                val millis = time.times(oneMin)
                String.format(
                    Locale.getDefault(),
                    "%d mins", // No need to worry about plurals
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                )
            }
            else -> {
                val millis = TimeUnit.MINUTES.toMillis(time.toLong())
                val hrs =
                    when (time < 120) {
                        true -> "hr"
                        else -> "hrs"
                    }
                String.format(
                    Locale.getDefault(),
                    "%d $hrs %d m",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis).minus(
                        TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(millis)
                        )
                    )
                )
            }
        }
    }

    fun getFruitCatWithQuantity(fruitCat: FruitCatKt): String {
        return "${ fruitCat.category } (${ fruitCat.count })"
    }

    fun getHeritageSiteAddress(address: String): String {
        return when (val end = address.indexOf(", ")) {
            -1 -> address
            else -> {
                val sb = StringBuilder()
                sb.append(address.substring(0, end))
                sb.append(",\n") // Note comma
                sb.append(address.substring(end.plus(1)))
                sb.toString()
            }
        }
    }

    private fun getHeritageType(typeId: Int): String {
        val typesArray =
            getResources().getStringArray(R.array.heritage_site_types)
        // Remember, type IDs start at 1
        return typesArray[typeId.minus(1)]
    }

    fun getHeritageTypeWithQuantity(typeId: Int, qty: Int): String {
        val type = getHeritageType(typeId)
        return "$type ($qty)"
    }

    fun getJourneyTypeIndicator(
        context: Context, round: Int
    ) : Drawable? {
        return ContextCompat.getDrawable(context,
            when (round) {
                extRet -> R.drawable.ic_baseline_code_black_24
                ext1way -> R.drawable.ic_baseline_keyboard_double_arrow_right_black_24
                baseRet -> R.drawable.ic_baseline_code_black_24
                else -> R.drawable.ic_baseline_keyboard_arrow_right_black_24 // base1way
            }
        )
    }

    fun getParkPerimeterText(
        integer: Int, string: String
    ) : String {
        return String.format(string, integer)
    }

    private fun getParkType(typeId: Int): String {
        val typesArray =
            getResources().getStringArray(R.array.park_types)
        // Remember, type IDs start at 1
        return typesArray[typeId.minus(1)]
    }

    fun getParkTypeWithQuantity(typeId: Int, qty: Int): String {
        val type = getParkType(typeId)
        return "$type ($qty)"
    }

    fun getRoutesSubtitle(count: Int): String {
        val quantityString =
             getResources().getQuantityString(R.plurals.routes_subtitle,
                 count)
        return String.format(quantityString, count)
    }

    fun getSpannableStyleBold(
        boldText: String, @StringRes stringId: Int
    ) : Spannable {
        val text = getResources().getString(stringId)
        return getSpannableStyleBold(boldText, text)
    }

    fun getSpannableStyleBold(boldText: String, text: String): Spannable {
        return when (val start = text.indexOf(boldText)) {
            -1 -> SpannableString(text)
            else -> {
                val end = start + boldText.length
                val spannable = SpannableString(text)
                spannable.setSpan(StyleSpan(Typeface.BOLD), start, end,0)
                spannable
            }
        }
    }

    fun getSpannableStyleBoldMulti(
        string1b: String, @StringRes stringId1a: Int,
        string2b: String, @StringRes stringId2a: Int
    ) : Spannable {
        val spannableStringBuilder = SpannableStringBuilder()
        val spannable1 =
            getSpannableStyleBold(string1b, stringId1a)
        spannableStringBuilder.append(spannable1)
        spannableStringBuilder.append(
            SpannableString(
                when (isLandscape()) {
                    true -> "\n"
                    else -> " "
                }
            )
        )
        val spannable2 =
            getSpannableStyleBold(string2b, stringId2a)
        spannableStringBuilder.append(spannable2)
        return spannableStringBuilder
    }

    fun getSpannableStyleSuperScript(
        integer: Int, string: String
    ) : Spannable {
        val text = String.format(string, integer)
        val start = text.length - 1
        val end = text.length
        val spannable = SpannableString(text)
        spannable.setSpan(SuperscriptSpan(), start, end,0)
        spannable.setSpan(RelativeSizeSpan(0.9F), start, end,0)
        return spannable
    }

    fun getStreetArtExtraText(
        context: Context, snippet: Boolean, streetArt: StreetArtKt
    ) : String {
        return when (streetArt.extra) {
            null -> {
                when (streetArt.credit) {
                    null -> {
                        when (streetArt.date) {
                            null -> {
                                when (snippet) {
                                    true -> {
                                        context.getString(
                                            R.string.snippet_no_info
                                        )
                                    }
                                    else -> {
                                        when (streetArt.description) {
                                            null -> {
                                                context.getString(
                                                    R.string.snippet_no_info
                                                )
                                            }
                                            else -> streetArt.description
                                        }
                                    }
                                }
                            }
                            else -> streetArt.date
                        }
                    }
                    else -> streetArt.credit
                }
            }
            else -> streetArt.extra
        }
    }

    fun getStreetViewLinkText(angle: Int, context: Context): Spannable {
        val boldText: String
        val spannable =
            when (angle) {
                -1 -> {
                    boldText = context.getString(R.string.no_street_view_b)
                    getSpannableStyleBold(boldText, R.string.no_street_view_a)
                }
                else -> {
                    boldText = context.getString(R.string.street_view_link_b)
                    getSpannableStyleBold(boldText, R.string.street_view_link_a)
                }
            }

        return spannable
    }

    fun getWarning(index: Int): String {
        val routeWarnings =
            getResources().getStringArray(R.array.route_warnings)
        return (routeWarnings[index.minus(1)])
    }

    fun getWaypointPositionText(
        position: Int, round: Int, size: Int
    ) : String {
        val res = getResources()
        val stringId =
            when (round) {
                -5 -> R.string.environ_count
                -4 -> R.string.location_count
                -3 -> R.string.site_count
                in base1way..extRet -> R.string.route_count // -2..1
                else -> R.string.track_count // 2
            }
        return String.format(res.getString(stringId),
            position, size)
    }

    fun getWaypointSubtitleText(
        position: Int,
        size: Int,
        @StringRes stringId: Int = R.string.place_count
    ) : String {
        val res = getResources()
        return when (position ==  1 && size == 2) {
            true -> res.getString(R.string.place_start)
            else -> {
                when (position) {
                    size -> res.getString(R.string.place_end)
                    else -> String.format(
                        res.getString(stringId), position, size
                    )
                }
            }
        }
    }

    fun toggleFullscreen(
        actionBar: ActionBar?, recyclerView: RecyclerView, root: View,
        // Vertical guideline is not present in portrait layouts
        toggleGroup: Group, verticalGuideline: Guideline? = null
    ) {
        actionBar?.let {
            when (toggleGroup.isVisible) {
                true -> it.hide()
                else -> it.show()
            }
        }

        var guidelinePercent: Float? = null
        val slide = Slide().apply {
            val res = getResources()
            duration = when (toggleGroup.isVisible) {
                true -> res.getInteger(
                    R.integer.toggle_close_anim_duration
                ).toLong()
                else -> res.getInteger(
                    R.integer.toggle_open_anim_duration
                ).toLong()
            }
            slideEdge = when (isLandscape()) {
                false -> Gravity.BOTTOM
                else -> {
                    guidelinePercent =
                        when (toggleGroup.isVisible) {
                            true -> 0F
                            else -> res.getFraction(
                                /* Note slightly different resource
                                   name used, compared to xml */
                                R.fraction.waypoints_guideline_p,
                                1,1
                            )
                        }
                    Gravity.START
                }
            }
            mode = when (toggleGroup.isVisible) {
                true -> Slide.MODE_OUT
                else -> Slide.MODE_IN
            }
        }

        guidelinePercent?.let {
            verticalGuideline?.setGuidelinePercent(it)
        }

        slide.addTarget(recyclerView)

        TransitionManager.beginDelayedTransition(
            root as ViewGroup, slide
        )

        // Must use "with"
        with (toggleGroup) {
            visibility = when (isVisible) {
                true -> View.GONE
                else ->  View.VISIBLE
            }
        }
    }
}
