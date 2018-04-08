package com.maragues.planner.visor

import android.content.Context
import android.support.v7.widget.GridLayoutManager

class ScrollableGridLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {
    override fun canScrollHorizontally() = true
    override fun canScrollVertically() = true
}