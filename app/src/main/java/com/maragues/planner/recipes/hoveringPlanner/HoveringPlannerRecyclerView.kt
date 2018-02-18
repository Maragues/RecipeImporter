package com.maragues.planner.recipes.hoveringPlanner

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.DragEvent
import android.view.DragEvent.ACTION_DRAG_ENDED
import android.view.DragEvent.ACTION_DRAG_STARTED
import android.view.View
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
        const val COLUMNS = 2
    }

    init {
        layoutManager = GridLayoutManager(context, COLUMNS)
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
            }, 2000)
        }

        return false
    }

}