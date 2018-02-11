package com.maragues.planner.persistence.entities

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import com.maragues.planner.recipes.MealType
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
)