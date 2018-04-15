package com.maragues.planner.visor

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.maragues.planner.common.BaseViewModel
import com.maragues.planner.model.DayMeals
import com.maragues.planner.model.Meal
import com.maragues.planner.persistence.relationships.MealSlotsAndRecipeIds
import com.maragues.planner.persistence.repositories.MealSlotRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.WeekFields
import java.util.Locale
import javax.inject.Inject

class PlannerVisorViewModel(private val mealSlotRepository: MealSlotRepository) : BaseViewModel() {
    fun viewStateObservable(): Observable<PlannerVisorViewState> {
        val fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek()
        val firstDayCurrentWeek = LocalDate.now().with(fieldISO, 1)

        val startDate = firstDayCurrentWeek.minusWeeks(1)
        val endDate = firstDayCurrentWeek.plusWeeks(1).with(fieldISO, 7)

        return mealSlotRepository.dayMealsDayBetween(startDate, endDate)
                .map { PlannerVisorViewState(fillAllDates(it, startDate, endDate)) }
                .toObservable()
    }

    private fun fillAllDates(dayMealsWithRecipes: List<DayMeals>, startDate: LocalDate, endDate: LocalDate): List<DayMeals> {
        var currentDate = startDate

        val datesWithRecipes = dayMealsWithRecipes.map { it.date }

        val outList = dayMealsWithRecipes.toMutableList()

        while (!currentDate.isAfter(endDate)) {
            if (!datesWithRecipes.contains(currentDate)) outList.add(DayMeals.empty(currentDate))

            currentDate = currentDate.plusDays(1)
        }

        return outList
    }

    fun onMealReplacedObservable(mealReplacedObservable: Observable<MealSlotsAndRecipeIds>) {
        disposables().add(
                mealReplacedObservable
                        .onTerminateDetach()
                        .observeOn(Schedulers.io())
                        .subscribe(
                                mealSlotRepository::replaceMeal,
                                Throwable::printStackTrace
                        )
        )
    }

    class Factory
    @Inject constructor(private val mealSlotRepository: MealSlotRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlannerVisorViewModel(mealSlotRepository) as T
        }
    }
}