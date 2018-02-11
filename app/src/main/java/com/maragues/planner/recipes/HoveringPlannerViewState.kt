package com.maragues.planner.recipes

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.MealSlot
import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 28/1/18.
 */
internal data class HoveringPlannerViewState(
        val visible: Boolean,
        val meals: Map<MealSlot, List<Recipe>>
) {
    companion object {
        fun emptyForDays(daysToCreate: Long): HoveringPlannerViewState {
            val todayDate = LocalDate.now()
            val mealsAndRecipes = mutableMapOf<MealSlot, List<Recipe>>()
            for (i in 0L until daysToCreate) {
                val date = todayDate.plusDays(i)
                mealsAndRecipes.put(MealSlot(date, LUNCH), listOf())
                mealsAndRecipes.put(MealSlot(date, DINNER), listOf())
            }

            return HoveringPlannerViewState(false, mealsAndRecipes)
        }
    }
}
