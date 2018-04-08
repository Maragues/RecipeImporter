package com.maragues.planner.persistence.repositories

import com.maragues.planner.model.DayMeals
import io.reactivex.Flowable
import java.time.LocalDate

interface MealRepository {
    fun dayMealsBetween(startDate: LocalDate, endDate: LocalDate): Flowable<List<DayMeals>>
}