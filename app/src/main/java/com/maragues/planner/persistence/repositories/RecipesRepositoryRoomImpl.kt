package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.room.RecipeDao
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * Created by miguelaragues on 7/1/18.
 */
internal class RecipesRepositoryRoomImpl
@Inject constructor(val recipeDao: RecipeDao) : RecipeRepository {

    override fun list(): Flowable<List<Recipe>> {
        return recipeDao.readAll()
    }

    override fun save(recipe: Recipe): Completable {
        return Completable.fromAction({
            recipeDao.insert(recipe)
        })
    }
}