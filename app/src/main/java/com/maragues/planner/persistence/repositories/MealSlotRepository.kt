package com.maragues.planner.persistence.repositories

import com.maragues.planner.model.DayMeals
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.model.MealSlot
import io.reactivex.Flowable
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 12/2/18.
 */
interface MealSlotRepository {
    fun mealsAndRecipesBetween(startDate: LocalDate,
                               endDate: LocalDate): Flowable<Map<MealSlot, List<Recipe>>>

    fun insert(mealSlotRecipe: MealSlotRecipe)

    fun replaceRecipe(mealSlotReplaced: MealSlotRecipe, newRecipeId: Long)

    fun dayMealsDayBetween(startDate: LocalDate, endDate: LocalDate): Flowable<List<DayMeals>>
}

