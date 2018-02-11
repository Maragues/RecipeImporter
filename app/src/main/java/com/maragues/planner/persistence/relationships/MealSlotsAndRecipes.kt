package com.maragues.planner.persistence.relationships

import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import com.maragues.planner.recipes.MealType
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 11/2/18.
 */
@TypeConverters(RecipeListConverter::class)
data class MealSlotsAndRecipeIds(val date: LocalDate,
                                 val mealType: MealType,
                                 val recipeIds: List<Long>)

class RecipeListConverter {

    @TypeConverter
    fun groupConcatToRecipeIdList(recipeIds: String): List<Long> {
        return recipeIds.split(",").map { it.toLong() }
    }
}