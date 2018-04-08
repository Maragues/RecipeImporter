package com.maragues.planner.persistence.repositories

import com.maragues.planner.model.DayMeals
import com.maragues.planner.model.Meal
import com.maragues.planner.persistence.entities.MealSlotRecipe
import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.entities.TagAction
import com.maragues.planner.persistence.room.MealSlotDao
import com.maragues.planner.persistence.room.RecipeDao
import com.maragues.planner.recipes.model.MealSlot
import com.maragues.planner.recipes.model.MealType.DINNER
import com.maragues.planner.recipes.model.MealType.LUNCH
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by miguelaragues on 12/2/18.
 */
internal class MealSlotRepositoryRoomImpl
@Inject constructor(val mealSlotDao: MealSlotDao,
                    val recipeDao: RecipeDao) : MealSlotRepository {
    override fun replaceRecipe(mealSlotReplaced: MealSlotRecipe, newRecipeId: Long) {
        mealSlotDao.replaceRecipe(mealSlotReplaced.date, mealSlotReplaced.mealType, newRecipeId)
    }

    override fun insert(mealSlotRecipe: MealSlotRecipe) {
        mealSlotDao.insert(mealSlotRecipe)
    }

    override fun mealsAndRecipesBetween(startDate: LocalDate,
                                        endDate: LocalDate): Flowable<Map<MealSlot, List<Recipe>>> {
        return mealSlotDao.mealsAndRecipeIdsBetween(startDate, endDate)
                .map { mealsAndRecipeIds ->
                    mealsAndRecipeIds.associateBy(
                            { MealSlot(it.date, it.mealType) },
                            { recipeDao.recipesByIds(it.recipeIds) }
                    )
                }
    }

    override fun dayMealsDayBetween(startDate: LocalDate,
                                    endDate: LocalDate): Flowable<List<DayMeals>> {
        return mealsAndRecipesBetween(startDate, endDate)
                .map { mealsAndRecipeIds ->
                    val dateAndMeals = mutableMapOf<LocalDate, MutableList<Meal>>()
                    mealsAndRecipeIds.entries.forEach {
                        dateAndMeals.putIfAbsent(it.key.date, mutableListOf())

                        dateAndMeals[it.key.date]?.add(Meal(it.key.date, it.key.mealType, it.value))
                    }

                    dateAndMeals.entries.map {
                        val lunch = it.value.singleOrNull { it.mealType == LUNCH }
                                ?: Meal.emptyLunch(it.key)
                        val dinner = it.value.singleOrNull { it.mealType == DINNER }
                                ?:  Meal.emptyDinner(it.key)

                        DayMeals(it.key, lunch, dinner)
                    }
                }
    }

}
