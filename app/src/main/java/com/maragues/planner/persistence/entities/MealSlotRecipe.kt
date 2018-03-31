package com.maragues.planner.persistence.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner.recipes.model.MealType
import org.threeten.bp.LocalDate

/**
 * Created by miguelaragues on 11/2/18.
 */

@Entity(
        primaryKeys = ["date", "mealType", "recipeId"],
        foreignKeys = arrayOf(
                ForeignKey(
                        entity = Recipe::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("recipeId")
                )
        )
)
data class MealSlotRecipe(
        val date: LocalDate,
        val mealType: MealType,
        val recipeId: Long
) {
    companion object {
        fun create(mealSlot: MealSlot, recipe: Recipe): MealSlotRecipe {
            require(recipe.id != null, { "Recipe Id can't be null" })

            return MealSlotRecipe(mealSlot.date, mealSlot.mealType, recipe.id!!)
        }
    }
}