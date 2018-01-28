package com.maragues.planner.recipes

import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.LocalDate
import timber.log.Timber

/**
 * Created by miguelaragues on 28/1/18.
 */
class HoveringPlannerViewModel : BaseViewModel() {
    companion object {
        const val DAYS_DISPLAYED = 5
    }

    init {

    }

    private val hoveringPlannerViewStateSubject: BehaviorSubject<HoveringPlannerViewState> = BehaviorSubject.create()

    internal fun viewStateObservable(): Observable<HoveringPlannerViewState> {
        return hoveringPlannerViewStateSubject
                .startWith(initialViewState())
                .hide()
    }

    private fun initialViewState(): HoveringPlannerViewState {
        val todayDate = LocalDate.now()
        val meals: MutableList<HoveringMealViewState> = mutableListOf()
        for (i in 0L until DAYS_DISPLAYED) {
            val date = todayDate.plusDays(i)
            meals.add(HoveringMealViewState(date, LUNCH, listOf()))
            meals.add(HoveringMealViewState(date, DINNER, listOf()))
        }

        return HoveringPlannerViewState(false, meals.toList())
    }
}