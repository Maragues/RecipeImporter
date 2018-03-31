package com.maragues.planner.recipes.hoveringPlanner

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.annotation.VisibleForTesting
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner.persistence.repositories.MealSlotRepository
import com.maragues.planner.recipes.hoveringPlanner.HoveringWeekPlannerAdapter.ReplaceRecipeAction
import com.maragues.planner.recipes.model.MealType.DINNER
import com.maragues.planner.recipes.model.MealType.LUNCH
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by miguelaragues on 11/2/18.
 */
internal class HoveringPlannerFragmentViewModel(private val mealSlotRepository: MealSlotRepository) : BaseViewModel() {
    companion object {
        const val DAYS_DISPLAYED = 4L
    }

    private val startDate = LocalDate.now()
    private val endDate = startDate.plusDays(DAYS_DISPLAYED)

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
                        .doFinally({ Timber.d("FragmentView model finalized subscription to mealsAndRecipesBetween") })
                        .doOnSubscribe({ Timber.d("FragmentView model subscribed to mealsAndRecipesBetween") })
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

    @VisibleForTesting
    fun addMissingMealSlots(mealSlotAndRecipes: Map<MealSlot, List<Recipe>>): Map<MealSlot, List<Recipe>> {
        val mutableMap = mutableMapOf<MealSlot, List<Recipe>>()

        (0L until DAYS_DISPLAYED)
                .map { startDate.plusDays(it) }
                .forEach { date ->
                    arrayOf(LUNCH, DINNER).forEach {
                        val mealSlot = MealSlot(date, it)

                        if (!mealSlotAndRecipes.containsKey(mealSlot)) {
                            mutableMap.put(mealSlot, listOf())
                        } else {
                            mutableMap.put(mealSlot, mealSlotAndRecipes[mealSlot]!!)
                        }
                    }
                }

        return mutableMap
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

    fun onRecipeReplacedObservable(recipeReplacedObservable: Observable<ReplaceRecipeAction>) {
        disposables().add(
                recipeReplacedObservable
                        .observeOn(Schedulers.io())
                        .subscribe(
                                { mealSlotRepository.replaceRecipe(it.mealSlotReplaced, it.newRecipeId) }
                                , Throwable::printStackTrace
                        )
        )
    }

    class Factory
    @Inject constructor(private val mealSlotRepository: MealSlotRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HoveringPlannerFragmentViewModel(mealSlotRepository) as T
        }
    }
}