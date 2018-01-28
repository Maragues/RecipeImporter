package com.maragues.planner.recipes

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 28/1/18.
 */
internal data class HoveringPlannerViewState(
        val visible: Boolean,
        val meals: List<HoveringMealViewState>
) {
    companion object {
        fun emptyForDays(daysToCreate: Int): HoveringPlannerViewState {
            val todayDate = LocalDate.now()
            val meals: MutableList<HoveringMealViewState> = mutableListOf()
            for (i in 0L until HoveringPlannerViewModel.DAYS_DISPLAYED) {
                val date = todayDate.plusDays(i)
                meals.add(HoveringMealViewState(date, LUNCH, listOf()))
                meals.add(HoveringMealViewState(date, DINNER, listOf()))
            }

            return HoveringPlannerViewState(false, meals.toList())
        }
    }
}

internal data class HoveringMealViewState(val date: LocalDate,
                                          val mealType: MealType,
                                          val recipes: List<Recipe>)

internal enum class MealType {
    LUNCH, DINNER
}