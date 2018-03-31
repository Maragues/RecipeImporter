package com.maragues.planner.recipes.hoveringPlanner

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.DragEvent
import android.view.DragEvent.ACTION_DRAG_ENDED
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.View
import com.maragues.planner.recipes.hoveringPlanner.HoveringPlannerRecyclerView.Companion
import com.maragues.planner.recipes.hoveringPlanner.HoveringPlannerRecyclerView.Companion.COLUMNS
import com.maragues.planner.ui.recyclerView.RowsAndColumnsItemDecoration
import com.maragues.planner_kotlin.R.dimen

/**
 * RecylerView that becomes visible when a user starts a drag and goes invisible some time after the
 * drag is completed
 *
 * Created by miguelaragues on 27/1/18.
 */
class HoveringPlannerRecyclerView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {
    companion object {
        const val COLUMNS = 3
        const val SPAN_COUNT = 7
        const val FADE_OUT_DELAY_MILLIS = 2000L
    }

    init {
        val gridLayoutManager = GridLayoutManager(context, SPAN_COUNT)
        gridLayoutManager.spanSizeLookup = HoverSpanSizeLookup()
        layoutManager = gridLayoutManager

        decorateRecyclerView()
    }

    private fun decorateRecyclerView() {
        val rowSpacing = resources.getDimensionPixelSize(dimen.recipe_planner_grid_spacing_row)
        val columnSpacing = resources.getDimensionPixelSize(dimen.recipe_planner_grid_spacing_column)
        addItemDecoration(RowsAndColumnsItemDecoration(rowSpacing, columnSpacing, COLUMNS))
    }

    override fun onDragEvent(event: DragEvent): Boolean {
        when (event.action) {
            ACTION_DRAG_STARTED -> {
                (parent as View).alpha = 1f

                return true
            }

            ACTION_DRAG_ENDED -> postDelayed({
                (parent as View).alpha = 0f
            }, FADE_OUT_DELAY_MILLIS)
        }

        return false
    }
}

private class HoverSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
    init {
        isSpanIndexCacheEnabled = true
    }

    override fun getSpanSize(position: Int): Int {
        return if (isHeader(position)) 1 else 3
    }

    fun isHeader(position: Int): Boolean = position % COLUMNS == 0
}