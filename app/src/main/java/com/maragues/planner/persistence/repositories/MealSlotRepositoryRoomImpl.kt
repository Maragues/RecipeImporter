package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.room.MealSlotDao
import com.maragues.planner.persistence.room.RecipeDao
import io.reactivex.Flowable
import org.threeten.bp.LocalDate
import javax.inject.Inject

/**
 * Created by miguelaragues on 12/2/18.
 */
internal class MealSlotRepositoryRoomImpl
@Inject constructor(val mealSlotDao: MealSlotDao,
                    val recipeDao: RecipeDao) : MealSlotRepository {

    override fun mealsAndRecipesBetween(startDate: LocalDate,
                                        endDate: LocalDate): Flowable<Map<MealSlot, List<Recipe>>> {
        return mealSlotDao.mealsAndRecipeIdsBetween(startDate, endDate)
                .map { mealsAndRecipeIDs ->
                    mealsAndRecipeIDs.associateBy(
                            { MealSlot(it.date, it.mealType) },
                            { recipeDao.recipesByIds(it.recipeIds.joinToString { "," }) }
                    )
                }
    }
}
