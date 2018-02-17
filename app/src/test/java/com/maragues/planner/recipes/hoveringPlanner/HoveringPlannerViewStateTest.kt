package com.maragues.planner.recipes.hoveringPlanner

import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner.recipes.model.MealType.DINNER
import com.maragues.planner.recipes.model.MealType.LUNCH
import com.maragues.planner.test.BaseUnitTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 28/1/18.
 */
class HoveringPlannerViewStateTest : BaseUnitTest() {

    @Test
    fun initialState_isNotVisible() {
        val days = 5L

        val viewState = HoveringPlannerViewState.emptyForDays(days)

        assertEquals(false, viewState.visible)
    }

    @Test
    fun initialState_containsHoveringMealStatesForTheNextNDays() {
        val days = 5L

        val viewState = HoveringPlannerViewState.emptyForDays(days)

        val expectedMeals = HoveringPlannerFragmentViewModel.DAYS_DISPLAYED * 2
        assertEquals(expectedMeals.toInt(), viewState.meals.size)

        val today = LocalDate.now()

        (0L until days)
                .map { today.plusDays(it) }
                .forEach { loopDate ->
                    arrayOf(LUNCH, DINNER).forEach {
                        val mealSlot = MealSlot(loopDate, it)
                        assertTrue(viewState.meals.containsKey(mealSlot))
                        assertTrue(viewState.meals[mealSlot]!!.isEmpty())
                    }
                }
    }
}