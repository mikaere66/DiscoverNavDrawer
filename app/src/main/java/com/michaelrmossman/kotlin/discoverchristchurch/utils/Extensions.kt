package com.michaelrmossman.kotlin.discoverchristchurch.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.Group
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polygon
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.michaelrmossman.kotlin.discoverchristchurch.R
import com.michaelrmossman.kotlin.discoverchristchurch.utils.MapUtils.getPolyCenter
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getAppContext
import com.michaelrmossman.kotlin.discoverchristchurch.utils.SysUtils.getResources
import com.michaelrmossman.kotlin.discoverchristchurch.views.RadioGridGroup

/**
 * Extension functions used throughout the app
 */
fun Group.getReferencedViews() = referencedIds.map {
    rootView.findViewById<View>(it)
}

fun Int.getProgress(ofValue: Int): Int {
    return when (ofValue) {
        0 -> 0
        // https://discuss.kotlinlang.org/t/math-operation/9001/2
        else -> this.times(100).div(ofValue)
    }
}

fun List<Boolean>.allFalse(): Boolean {
    return !this.contains(true)
}

fun List<Boolean>.allTrue(): Boolean {
    return !this.contains(false)
}

fun MaterialButtonToggleGroup.getSelectedIndex(checkedId: Int): Int {
    val button = findViewById<Button>(checkedId)
    return indexOfChild(button)
}

fun Polygon.getCenter(): LatLng {
    val polyPoints = this.points
    return getPolyCenter(polyPoints)
}

//fun Polyline.getCenter(): LatLng {
//    val polyPoints = this.points
//    return getPolyCenter(polyPoints)
//}

fun RadioGridGroup.getSelectedIndex(checkedId: Int): Int {
    val button = findViewById<Button>(checkedId)
    return indexOfChild(button)
}

fun RadioGroup.selectedIndex(): Int {
    val radioButtonId = this.checkedRadioButtonId
    val radioButton: View = this.findViewById(radioButtonId)
    return this.indexOfChild(radioButton)
}

// https://slions.net/threads/extra-action-button-on-android-snackbar.87/
fun Snackbar.addAction1(
    @LayoutRes aLayoutId: Int,
    @StringRes aLabel: Int,
    aListener: View.OnClickListener?
) : Snackbar {
    addAction2(aLayoutId, getResources().getString(aLabel), aListener)
    return this
}

fun Snackbar.addAction2(
    @LayoutRes aLayoutId: Int, aLabel: String,
    aListener: View.OnClickListener?
) : Snackbar {
    // Define button
    val button =
        LayoutInflater.from(view.context)
            .inflate(aLayoutId,null) as Button
    /* Using our special knowledge of the snackbar action
       button id, we can hook our extra button next to it */
    view.findViewById<Button>(R.id.snackbar_action).let {
        // Copy layout
        button.layoutParams = it.layoutParams
        // Copy colors
        (button as? Button)?.setTextColor(it.textColors)
        // Add button
        (it.parent as? ViewGroup)?.addView(button)
    }
    button.text = aLabel
    /* Ideally, we should use [Snackbar.dispatchDismiss] rather
       than [Snackbar.dismiss] though this should do for now */
    button.setOnClickListener { this.dismiss(); aListener?.onClick(it) }
    return this
}

// Usage : e.g. binding.root.showSnackbar(R.string.whatever)
fun View.showSnackbar(
    @StringRes stringId: Int,
    length: Int = Snackbar.LENGTH_SHORT,
    view: View? = null
) : Snackbar {
    return showSnackbar(getAppContext().getString(stringId), length, view)
}

// Usage : e.g. binding.root.showSnackbar(getString(R.string.whatever))
fun View.showSnackbar(
    string: String,
    length: Int = Snackbar.LENGTH_SHORT,
    view: View? = null
) : Snackbar {
    val snackbar = Snackbar.make(this, string, length)
    view?.let { snackbar.anchorView = it }
    snackbar.show()
    return snackbar
}
