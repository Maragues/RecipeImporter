package com.maragues.planner.recipes

import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.MealSlotRepository
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner.recipes.model.MealType.DINNER
import com.maragues.planner.recipes.model.MealType.LUNCH
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 28/1/18.
 */
class HoveringPlannerViewModel(val mealSlotRepository: MealSlotRepository) : BaseViewModel() {
    companion object {
        const val DAYS_DISPLAYED = 5L
    }

    private val endDate = LocalDate.now()
    private val startDate = endDate.plusDays(DAYS_DISPLAYED)

    private val hoveringPlannerViewStateSubject: BehaviorSubject<HoveringPlannerViewState> = BehaviorSubject.create()

    internal fun viewStateObservable(): Observable<HoveringPlannerViewState> {
        return hoveringPlannerViewStateSubject
                .startWith(initialViewState())
                .doOnSubscribe({ loadData() })
    }

    private fun loadData() {
        disposables().add(
                mealSlotRepository.mealsAndRecipesBetween(startDate, endDate)
                        .map { { addMissingMealSlots(it) } }
                        .subscribe(
                                {
                                    hoveringPlannerViewStateSubject.onNext(
                                            HoveringPlannerViewState(
                                                    hoveringPlannerViewStateSubject.value.visible,
                                                    it.invoke()
                                            ))
                                },
                                Throwable::printStackTrace
                        )
        )
    }

    private fun initialViewState(): HoveringPlannerViewState {
        return HoveringPlannerViewState.emptyForDays(DAYS_DISPLAYED)
    }

    private fun addMissingMealSlots(mealSlotAndRecipes: Map<MealSlot, List<Recipe>>): Map<MealSlot, List<Recipe>> {
        val mutableMap = mutableMapOf<MealSlot, List<Recipe>>()

        (0L until DAYS_DISPLAYED)
                .map { startDate.plusDays(it) }
                .forEach { date ->
                    arrayOf(LUNCH, DINNER).forEach {
                        val mealSlot = MealSlot(date, it)

                        if (!mealSlotAndRecipes.containsKey(mealSlot)) {
                            mutableMap.put(mealSlot, listOf())
                        }
                    }
                }

        return mutableMap
    }
}