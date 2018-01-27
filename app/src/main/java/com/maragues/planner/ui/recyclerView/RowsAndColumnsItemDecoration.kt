package com.maragues.planner.ui.recyclerView

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.util.Log
import android.view.View

/**
 * Created by miguelaragues on 27/1/18.
 */
class RowsAndColumnsItemDecoration(val rowSpace: Int, val columnSpace: Int, val spanCount: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View?, parent: RecyclerView, state: State?) {
        super.getItemOffsets(outRect, view, parent, state)

        val totalRows = parent.adapter.itemCount / spanCount
        val layoutPosition = parent.getChildLayoutPosition(view)
        val viewRow = layoutPosition / spanCount
        //if it's not last row, add bottom space
        if (viewRow != totalRows) {
            outRect.bottom = rowSpace
        } else {
            outRect.bottom = 0
        }

        val viewColumn = layoutPosition % spanCount
        //if it's not last column, add right space
        if (viewColumn != spanCount) {
            outRect.right = columnSpace
        } else {
            outRect.right = 0
        }

        outRect.left = 0
        outRect.top = 0
    }
}