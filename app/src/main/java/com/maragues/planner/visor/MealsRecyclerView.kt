package com.maragues.planner.visor

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.State
import android.util.AttributeSet
import android.view.View
import com.maragues.planner_kotlin.R
import io.reactivex.Observable
import io.reactivex.subjects.ReplaySubject

class MealsRecyclerView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RecyclerView(context, attrs, defStyleAttr) {

    companion object {
        const val WEEKS_DISPLAYED = 2
        const val DAYS_DISPLAYED = 3
    }

    private val columnWidthSubject = ReplaySubject.create<Int>()
    private val columnHeightSubject = ReplaySubject.create<Int>()

    private val spacing: Int = resources.getDimensionPixelSize(R.dimen.visor_meals_separation)

    init {
        addItemDecoration(MealsItemSeparation(spacing))
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)

        if (!columnWidthSubject.hasComplete()) {
            val width = MeasureSpec.getSize(widthSpec)
            val height = MeasureSpec.getSize(heightSpec)

            if (width != 0) {
                val widthPerWeek = width / WEEKS_DISPLAYED
                val heightPerDay = height / DAYS_DISPLAYED
                if (widthPerWeek > 0) {
                    columnWidthSubject.onNext(widthPerWeek)
                    columnHeightSubject.onNext(heightPerDay)

                    columnWidthSubject.onComplete()
                    columnHeightSubject.onComplete()
                }
            }
        }
    }

    internal fun columnWidthObservable(): Observable<Int> {
        return columnWidthSubject.hide()
    }

    internal fun columnHeightObservable(): Observable<Int> {
        return columnHeightSubject.hide()
    }
}

internal class MealsItemSeparation(val separation: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: State) {
        val position = parent.getChildLayoutPosition(view)

        val mealsAdapter = parent.adapter as MealsAdapter

        val column = mealsAdapter.getWeekIndex(position)
        val row = mealsAdapter.getWeekOffset(position)

        outRect.left = if (column == 0) 0 else separation
        outRect.right = if (isLastColumn(column, mealsAdapter)) 0 else separation
        outRect.top = if (row == 0) 0 else separation
        outRect.bottom = if (isLastRow(row)) 0 else separation
    }

    private fun isLastRow(row: Int) = row == (MealsAdapter.DAYS_IN_WEEK - 1)

    private fun isLastColumn(column: Int, mealsAdapter: MealsAdapter) = column == (mealsAdapter.getTotalWeeks() - 1)
}