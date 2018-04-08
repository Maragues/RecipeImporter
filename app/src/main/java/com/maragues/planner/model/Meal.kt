package com.maragues.planner.model

import com.maragues.planner.model.Meal.Companion.emptyDinner
import com.maragues.planner.model.Meal.Companion.emptyLunch
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.recipes.model.MealType
import com.maragues.planner.recipes.model.MealType.DINNER
import com.maragues.planner.recipes.model.MealType.LUNCH
import org.threeten.bp.LocalDate

data class Meal(val date: LocalDate, val mealType: MealType, val recipes: List<Recipe>) {
    companion object {
        fun emptyLunch(date: LocalDate) = Meal(date, LUNCH, listOf())
        fun emptyDinner(date: LocalDate) = Meal(date, DINNER, listOf())
    }
}

data class DayMeals(val date: LocalDate, val lunch: Meal, val dinner: Meal) : Comparable<DayMeals> {
    override fun compareTo(other: DayMeals): Int {
        return other.date.compareTo(date)
    }

    companion object {
        fun empty(date: LocalDate) = DayMeals(date, emptyLunch(date), emptyDinner(date))
    }
}