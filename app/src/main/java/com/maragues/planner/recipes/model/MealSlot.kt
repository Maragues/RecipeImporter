package com.maragues.planner.recipes.model

import com.maragues.planner.recipes.model.MealType.LUNCH
import org.threeten.bp.LocalDate

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