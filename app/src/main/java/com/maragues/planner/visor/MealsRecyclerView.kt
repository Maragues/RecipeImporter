package com.maragues.planner.visor

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
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