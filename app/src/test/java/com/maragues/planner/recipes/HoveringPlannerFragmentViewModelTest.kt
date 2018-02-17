package com.maragues.planner.recipes

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.repositories.MealSlot
import com.maragues.planner.persistence.repositories.MealSlotRepository
import com.maragues.planner.recipes.HoveringPlannerFragmentViewModel.Companion.DAYS_DISPLAYED
import com.maragues.planner.recipes.MealType.DINNER
import com.maragues.planner.recipes.MealType.LUNCH
import com.maragues.planner.test.BaseUnitTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 17/2/18.
 */
class HoveringPlannerFragmentViewModelTest : BaseUnitTest() {
    lateinit var viewModel: HoveringPlannerFragmentViewModel

    @Mock
    lateinit var mealSlotRepository: MealSlotRepository

    override fun setUp() {
        super.setUp()

        viewModel = spy(HoveringPlannerFragmentViewModel(mealSlotRepository))
    }

    @Test
    fun addMissingMealSlots_emptyMap_returnsMapFilledWithDAYS_DISPLAYEDAndEmptyRecipes() {
        val mapResult: Map<MealSlot, List<Recipe>> = viewModel.addMissingMealSlots(mapOf())

        assertEquals((HoveringPlannerFragmentViewModel.DAYS_DISPLAYED * 2).toInt(), mapResult.size)

        val today = LocalDate.now()

        (0L until DAYS_DISPLAYED)
                .map { today.plusDays(it) }
                .forEach { date ->
                    arrayOf(LUNCH, DINNER).forEach {
                        val mealSlot = MealSlot(date, it)

                        assertTrue("Map does not contain $mealSlot. Map is $mapResult", mapResult.containsKey(mealSlot))
                        assertTrue(mapResult[mealSlot]!!.isEmpty())
                    }
                }
    }

    @Test
    fun addMissingMealSlots_mapWith1MealWithRecipes_returnsMapFilledWithDAYS_DISPLAYEDAndTheSingleRecipe() {
        val today = LocalDate.now()

        val mealSlotWithRecipe = MealSlot(today.plusDays(3), DINNER)

        val inputMap = mutableMapOf<MealSlot, List<Recipe>>()
        val expectedRecipe = mock(Recipe::class.java)
        inputMap.put(mealSlotWithRecipe, listOf(expectedRecipe))

        val mapResult = viewModel.addMissingMealSlots(inputMap)

        assertEquals((HoveringPlannerFragmentViewModel.DAYS_DISPLAYED * 2).toInt(), mapResult.size)

        assertEquals(1, mapResult[mealSlotWithRecipe]!!.size)
        assertEquals(expectedRecipe, mapResult[mealSlotWithRecipe]!![0])

        (0L until DAYS_DISPLAYED)
                .map { today.plusDays(it) }
                .forEach { date ->
                    arrayOf(LUNCH, DINNER).forEach {
                        val mealSlot = MealSlot(date, it)

                        assertTrue("Map does not contain $mealSlot. Map is $mapResult", mapResult.containsKey(mealSlot))

                        if (mealSlotWithRecipe != mealSlot) {
                            assertTrue(mapResult[mealSlot]!!.isEmpty())
                        }
                    }
                }
    }
}