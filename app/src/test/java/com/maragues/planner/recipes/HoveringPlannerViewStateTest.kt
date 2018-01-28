package com.maragues.planner.recipes

import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import com.maragues.planner.test.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 28/1/18.
 */
class HoveringPlannerViewStateTest : BaseUnitTest(){

    @Test
    fun initialState_containsHoveringMealStatesForTheNextNDays() {
        val days = 5

        val viewState = HoveringPlannerViewState.emptyForDays(days)

        val expectedMeals = HoveringPlannerViewModel.DAYS_DISPLAYED * 2
        assertEquals(expectedMeals, viewState.meals.size)

        val today = LocalDate.now()

        for (i in 0L until days) {
            val realIndex = i*2
            assertEquals("At position $realIndex", HoveringMealViewState(today.plusDays(i), LUNCH, listOf()), viewState.meals[realIndex.toInt()])
            assertEquals("At position ${realIndex.toInt() + 1}, with i=$i", HoveringMealViewState(today.plusDays(i), DINNER, listOf()), viewState.meals[(realIndex.toInt() + 1)])
        }
    }
}