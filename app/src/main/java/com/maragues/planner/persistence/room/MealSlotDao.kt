package com.maragues.planner.persistence.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import android.arch.persistence.room.Transaction
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.relationships.MealSlotsAndRecipeIds
import com.maragues.planner.recipes.model.MealType
import io.reactivex.Flowable
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 7/1/18.
 */
@Dao
abstract class MealSlotDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(mealSlotRecipe: MealSlotRecipe)

    @Query("DELETE from mealslotrecipe WHERE date=:mealDate AND mealType=:mealType")
    abstract fun deleteMealSlotRecipes(mealDate: LocalDate, mealType: MealType)

    @Query("SELECT date, mealtype, group_concat(recipeId, \",\") as recipeIds " +
            "FROM mealSlotRecipe " +
            "WHERE date BETWEEN :startDate AND :endDate " +
            "GROUP BY date, mealtype")
    abstract fun mealsAndRecipeIdsBetween(startDate: LocalDate,
                                          endDate: LocalDate): Flowable<List<MealSlotsAndRecipeIds>>

    @Query("UPDATE mealSlotRecipe SET recipeId=:newRecipeId WHERE recipeId=:oldRecipeId AND date=:mealDate AND mealType=:mealType")
    abstract fun replaceRecipe(mealDate: LocalDate, mealType: MealType, oldRecipeId: Long, newRecipeId: Long)

    @Transaction
    open fun replaceAllRecipes(mealDate: LocalDate, mealType: MealType, newRecipeIds: List<Long>) {
        deleteMealSlotRecipes(mealDate, mealType)

        newRecipeIds.forEach { insert(MealSlotRecipe(mealDate, mealType, it)) }
    }
}
