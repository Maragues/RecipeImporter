package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.MealType
import com.maragues.planner.recipes.MealType.LUNCH
import com.maragues.planner.recipes.MealTypeComparator
import io.reactivex.Flowable
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 12/2/18.
 */
interface MealSlotRepository {
    fun mealsAndRecipesBetween(startDate: LocalDate,
                               endDate: LocalDate): Flowable<Map<MealSlot, List<Recipe>>>

    fun insert(mealSlotRecipe: MealSlotRecipe)
}

data class MealSlot(val date: LocalDate, val mealType: MealType) : Comparable<MealSlot> {
    override fun compareTo(other: MealSlot): Int {
        val result = date.compareTo(other.date)

        if (result == 0) {
            return MealTypeComparator().compare(mealType, other.mealType)
        }

        return result;
    }

    fun isLunch(): Boolean {
        return mealType == LUNCH
    }

}