package com.maragues.planner.recipes

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.MealSlot
import com.maragues.planner.persistence.repositories.MealSlotRepository
import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 11/2/18.
 */
class HoveringPlannerFragmentViewModel(private val mealSlotRepository: MealSlotRepository) : BaseViewModel() {
    companion object {
        const val DAYS_DISPLAYED = 4L
    }

    private val endDate = LocalDate.now()
    private val startDate = endDate.minusDays(DAYS_DISPLAYED)

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
                                                    true,
//                                                    hoveringPlannerViewStateSubject.value.visible,
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

    class Factory(private val mealSlotRepository: MealSlotRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HoveringPlannerFragmentViewModel(mealSlotRepository) as T
        }
    }

    fun addRecipeObservable(recipeAddedObservable: Observable<MealSlotRecipe>) {
        disposables().add(
                recipeAddedObservable
                        .observeOn(Schedulers.io())
                        .subscribe(
                                { mealSlotRepository.insert(it) },
                                Throwable::printStackTrace
                        )
        )
    }
}