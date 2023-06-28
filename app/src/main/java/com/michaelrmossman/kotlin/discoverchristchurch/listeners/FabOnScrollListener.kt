package com.michaelrmossman.kotlin.discoverchristchurch.listeners

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class FabOnScrollListener(
    private val action: () -> Unit,
    private val eFab: ExtendedFloatingActionButton
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recycler: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recycler, dx, dy)

        if (dy < 0 && !eFab.isExtended) {
            eFab.extend()
        } else if (dy > 0 && eFab.isExtended) {
            eFab.shrink()
        }
    }

    override fun onScrollStateChanged(recycler: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recycler, newState)

        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            action()
        }
    }
}

