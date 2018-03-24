package com.maragues.planner.persistence.repositories

import com.maragues.planner.persistence.entities.Recipe
import com.maragues.planner.persistence.entities.Tag
import com.maragues.planner.persistence.room.RecipeDao
import io.reactivex.BackpressureStrategy.BUFFER
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import java.io.File.separator
import java.util.concurrent.Callable
import javax.inject.Inject

/**
 * Created by miguelaragues on 7/1/18.
 */
internal class RecipesRepositoryRoomImpl
@Inject constructor(val recipeDao: RecipeDao) : RecipeRepository {
    override fun filterByName(query: String): Flowable<List<Recipe>> {
        return recipeDao.filterByName(query)
    }

    override fun filterByTag(tagFilter: Observable<Set<Tag>>): Flowable<List<Recipe>> {
        return tagFilter
                .toFlowable(BUFFER)
                .map({ it.map { it.name } })
                .flatMap { recipeDao.filterByTag(it.joinToString(separator = ",")) }
    }

    override fun list(): Flowable<List<Recipe>> {
        return recipeDao.readAll()
    }

    override fun save(recipe: Recipe): Single<Long> {
        return Single.fromCallable({ recipeDao.insert(recipe) })
    }
}