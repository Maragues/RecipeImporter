package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.relationships.MealSlotsAndRecipeIds
import com.maragues.planner.recipes.MealType
import io.reactivex.Flowable
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 7/1/18.
 */
@Dao
abstract class MealSlotDao {

    @Query("SELECT date, mealtype, group_concat(recipeId, \",\") as recipeIds " +
            "FROM mealSlotRecipe " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "GROUP BY date, mealtype")
    abstract fun mealsAndRecipeIdsBetween(startDate: LocalDate,
                                          endDate: LocalDate): Flowable<List<MealSlotsAndRecipeIds>>
}
